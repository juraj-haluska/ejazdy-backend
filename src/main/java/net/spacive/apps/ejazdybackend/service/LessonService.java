package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.database.DynamoDao;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    @Autowired
    private DynamoDao dynamoDao;

    public Lesson createLessonByInstructor(String instructorId, Lesson lesson) {
        final Lesson newLesson = new Lesson()
                .withInstructorId(instructorId)
                .withStartTime(lesson.getStartTime())
                .withStopTime(lesson.getStopTime());

        dynamoDao.createLesson(newLesson);
        return newLesson;
    }

    // only if lesson is free
    public Lesson registerStudentToLesson(String studentId, String instructorId, String startTime) throws Exception {
        Lesson fetchedLesson = dynamoDao.getLessonByInstructor(instructorId, startTime);

        if (fetchedLesson.getStudentId() == null || fetchedLesson.getStudentId().length() == 0) {
            fetchedLesson.withStudentId(studentId);
            fetchedLesson.withStopTime(null);

            dynamoDao.updateLesson(fetchedLesson, true);
            return fetchedLesson;
        } else {
            throw new Exception("lesson already registered to another student");
        }
    }

    // no foce mode - this method will delete student from lesson if this student
    // is actually registered to it.
    // force mode - delete any student from specified lesson
    // TODO: disable unregistration in no force mode within 24h prior to lesson beginning
    public Lesson unregisterStudentFromLesson(String studentId, String instructorId, String startTime, boolean force) throws Exception {
        Lesson fetchedLesson = dynamoDao.getLessonByInstructor(instructorId, startTime);
        if (fetchedLesson.getStudentId().equals(studentId) || force) {
            Lesson toUpdate = new Lesson()
                    .withInstructorId(instructorId)
                    .withStartTime(startTime)
                    .withStudentId(null);

            dynamoDao.updateLesson(toUpdate, false);
            return toUpdate;
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

    public Lesson deleteLesson(String instructorId, String startTime) throws Exception {
        Lesson toDelete = new Lesson()
                .withInstructorId(instructorId)
                .withStartTime(startTime);

        dynamoDao.deleteLesson(toDelete);
        return toDelete;
    }
}
