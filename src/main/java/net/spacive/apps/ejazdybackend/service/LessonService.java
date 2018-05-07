package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.database.DynamoDao;
import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class LessonService {

    private final DynamoDao dynamoDao;

    private final static long dayInMilis = 86400000;
    private final static double milisToHoursRat = 3600000;

    @Autowired
    public LessonService(DynamoDao dynamoDao) {
        this.dynamoDao = dynamoDao;
    }

    public Lesson createLessonByInstructor(CognitoUser instructor, Lesson lesson) {

        final Lesson newLesson = new Lesson()
                .withInstructorId(instructor.getId())
                .withStartTime(lesson.getStartTime())
                .withStopTime(lesson.getStopTime())
                .withInstructorName(
                        instructor.getFirstName() + " " + instructor.getLastName()
                );

        if (dynamoDao.createLesson(newLesson)) {
            return newLesson;
        } else {
            return null;
        }
    }

    // only if lesson is free
    public Lesson registerStudentToLesson(CognitoUser student, String instructorId, Calendar startTime) throws Exception {
        Lesson fetchedLesson = dynamoDao.getLessonByInstructor(instructorId, startTime);

        if (fetchedLesson.getStudentId() == null || fetchedLesson.getStudentId().length() == 0) {

            fetchedLesson.withStudentId(student.getId());
            fetchedLesson.withStudentName(student.getFirstName() + " " + student.getLastName());

            dynamoDao.updateLesson(fetchedLesson, true);
            return fetchedLesson;
        } else {
            throw new Exception("lesson is already registered to another student: "
                + fetchedLesson.getStudentId() + "/" + fetchedLesson.getStudentName()
            );
        }
    }

    // no foce mode - this method will delete student from lesson if this student
    // is actually registered to it.
    // force mode - delete any student from specified lesson and ignore 24h before lesson
    public Lesson unregisterStudentFromLesson(String studentId, String instructorId, Calendar startTime, boolean force) throws Exception {
        Lesson fetchedLesson = dynamoDao.getLessonByInstructor(instructorId, startTime);

        // 24 hours before lesson begins
        Calendar actual = Calendar.getInstance();    // actual time
        Calendar shift24 = (Calendar) fetchedLesson.getStartTime().clone();
        shift24.setTimeInMillis(
                // shift lesson start - 24h
                shift24.getTimeInMillis() - dayInMilis
        );

        if (actual.after(shift24) && !force) {
            // disable unregistration
            return fetchedLesson;
        }

        if (fetchedLesson.getStudentId().equals(studentId) || force) {
            fetchedLesson.setStudentId(null);
            fetchedLesson.setStudentName(null);
            dynamoDao.updateLesson(fetchedLesson, false);
            return fetchedLesson;
        } else {
            throw new Exception("lesson belongs another student");
        }
    }

    public List<Lesson> getLessonsByStudent(String studentId) {
        return dynamoDao.getLessonsByStudent(studentId);
    }

    public List<Lesson> getLessonsByInstructor(String instructorId) {
        return dynamoDao.getLessonsByInstructor(instructorId);
    }

    public List<Lesson> getLessonsByInstructorSince(String instructorId, Calendar since) {
        return dynamoDao.getLessonsByInstructorSince(instructorId, since);
    }

    public List<Lesson> getLessonsByStudentSince(String studentId, Calendar since) {
        return dynamoDao.getLessonsByStudentSince(studentId, since);
    }

    public List<Lesson> getLessonsByInstructorRange(
            String instructorId,
            Calendar from,
            Calendar to) {

        return dynamoDao.getLessonsByInstructorRange(instructorId, from, to);
    }

    public List<Lesson> getLessonsByStudentRange(
            String studentId,
            Calendar from,
            Calendar to) {

        return dynamoDao.getLessonsByStudentRange(studentId, from, to);
    }

    public Lesson deleteLesson(String instructorId, Calendar startTime) {
        Lesson toDelete = new Lesson()
                .withInstructorId(instructorId)
                .withStartTime(startTime);

        dynamoDao.deleteLesson(toDelete);
        return toDelete;
    }

    public Double getHoursCompletedByStudent(String studentId) {

        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(0);

        List<Lesson> lessons = getLessonsByStudentRange(
                studentId,
                from,
                Calendar.getInstance()
        );

        long milisTotal = 0;

        // sum up durations of lessons in miliseconds
        for (Lesson l: lessons) {
            long start = l.getStartTime().getTimeInMillis();
            long stop = l.getStopTime().getTimeInMillis();

            if (stop > start) {
                milisTotal += stop - start;
            }
        }

        // convert to hours
        return milisTotal / milisToHoursRat;
    }
}
