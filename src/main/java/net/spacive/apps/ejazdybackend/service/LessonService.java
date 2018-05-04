package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.database.DynamoDao;
import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    private final DynamoDao dynamoDao;

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
    public Lesson registerStudentToLesson(CognitoUser student, String instructorId, String startTime) throws Exception {
        Lesson fetchedLesson = dynamoDao.getLessonByInstructor(instructorId, startTime);

        if (fetchedLesson.getStudentId() == null || fetchedLesson.getStudentId().length() == 0) {

            fetchedLesson.withStudentId(student.getId());
            fetchedLesson.withStudentName(student.getFirstName() + " " + student.getLastName());

            dynamoDao.updateLesson(fetchedLesson, true);
            return fetchedLesson;
        } else {
            throw new Exception("lesson is already registered to another student");
        }
    }

    // no foce mode - this method will delete student from lesson if this student
    // is actually registered to it.
    // force mode - delete any student from specified lesson
    // TODO: disable unregistration in no force mode within 24h prior to lesson beginning
    public Lesson unregisterStudentFromLesson(String studentId, String instructorId, String startTime, boolean force) throws Exception {
        Lesson fetchedLesson = dynamoDao.getLessonByInstructor(instructorId, startTime);
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

    public Lesson deleteLesson(String instructorId, String startTime) {
        Lesson toDelete = new Lesson()
                .withInstructorId(instructorId)
                .withStartTime(startTime);

        dynamoDao.deleteLesson(toDelete);
        return toDelete;
    }
}
