package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.database.DynamoDao;
import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * This class is an implementation of the lesson service.
 *
 * @author  Juraj Haluska
 */
@Service
public class LessonService {

    /**
     * Reference to DynamoDao.
     */
    private final DynamoDao dynamoDao;

    /**
     * Respresents the lenght of the day in milliseconds.
     */
    private final static long dayInMilis = 86400000;

    /**
     * Radio used for convesion between milliseconds and hours.
     */
    private final static double milisToHoursRat = 3600000;

    /**
     * LessonService constructor.
     *
     * @param dynamoDao injected DynamoDao.
     */
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

    /**
     * Register student to lesson.
     *
     * <p>Lessons are always owned by instructor and instructors
     * id is used as hash key in DynamoDB. This is reason why
     * instructorId must be passed.
     *
     * <p>Start time is the range key in DynamoDB and it must
     * be unique among lessons of particular instructor.
     *
     * @param student an unique id of the student.
     * @param instructorId an unique id of the instructor.
     * @param startTime beginning time of the lesson.
     * @return lesson instance to which the student was registered.
     * @throws Exception if another student is already registered to it.
     */
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

    /**
     * Unregister student from lesson.
     *
     * <p>Force mode will delete any student from specified
     * lesson and ignore 24h before lesson.
     *
     * <p>No force mode will delete student from lesson if this student
     * is actually registered to it.
     *
     * <p>Start time is the range key in DynamoDB.
     *
     * @param studentId an unique id of the student.
     * @param instructorId an unique id of the instructor.
     * @param startTime beginning time of the lesson.
     * @param force force delete mode.
     * @return lesson instance from which the student was unregistered.
     * @throws Exception (no force only) if the lesson specified belongs to another student.
     */
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

    /**
     * List all lessons of specified student.
     *
     * @param studentId an unique id of the student.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByStudent(String studentId) {
        return dynamoDao.getLessonsByStudent(studentId);
    }

    /**
     * List all lessons of specified instructor.
     *
     * @param instructorId an unique id of the instructor.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByInstructor(String instructorId) {
        return dynamoDao.getLessonsByInstructor(instructorId);
    }

    /**
     * List lessons of specified instructor since
     * specified date.
     *
     * @param instructorId an unique id of the instructor.
     * @param since since when date.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByInstructorSince(String instructorId, Calendar since) {
        return dynamoDao.getLessonsByInstructorSince(instructorId, since);
    }

    /**
     * List lessons of specified student since
     * specified date.
     *
     * @param studentId an unique id of the student.
     * @param since since when date.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByStudentSince(String studentId, Calendar since) {
        return dynamoDao.getLessonsByStudentSince(studentId, since);
    }

    /**
     * List lessons of specified instructor within
     * date range.
     *
     * @param instructorId an unique id of the instructor.
     * @param from starting by date.
     * @param to ending by date.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByInstructorRange(
            String instructorId,
            Calendar from,
            Calendar to) {

        return dynamoDao.getLessonsByInstructorRange(instructorId, from, to);
    }

    /**
     * List lessons of specified student within
     * date range.
     *
     * @param studentId an unique id of the student.
     * @param from starting by date.
     * @param to ending by date.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByStudentRange(
            String studentId,
            Calendar from,
            Calendar to) {

        return dynamoDao.getLessonsByStudentRange(studentId, from, to);
    }

    /**
     * Permamently will delete lesson from the system.
     *
     * @param instructorId an unique id of the instructor.
     * @param startTime startTime which is unique across instructors lessons.
     * @return an instance of the deleted lesson.
     */
    public Lesson deleteLesson(String instructorId, Calendar startTime) {
        Lesson toDelete = new Lesson()
                .withInstructorId(instructorId)
                .withStartTime(startTime);

        dynamoDao.deleteLesson(toDelete);
        return toDelete;
    }

    /**
     * Get amount of completed hours by student.
     *
     * @param studentId an unique id of the student.
     * @return amount of hours.
     */
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
