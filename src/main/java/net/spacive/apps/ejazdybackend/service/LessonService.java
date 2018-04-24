package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.database.DynamoDao;
import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    @Autowired
    private DynamoDao dynamoDao;

    @Autowired
    private CognitoService cognitoService;

    // TODO return new lesson or throw exception
    public Lesson createLessonByInstructor(CognitoUser instructor, String startTime, String stopTime) {
        final Lesson newLesson = new Lesson()
                .withInstructorId(instructor.getId())
                .withStartTime(startTime)
                .withStopTime(stopTime);

        dynamoDao.createLesson(newLesson);
        return newLesson;
    }

    public Lesson registerStudentToLesson(CognitoUser student, Lesson lesson) throws Exception {
        checkInstructorId(lesson);
        lesson.setStudentId(student.getId());
        dynamoDao.updateLesson(lesson);
        return lesson;
    }

    public Lesson unregisterStudentFromLesson(Lesson lesson) throws Exception {
        // TODO check if the time is 24h before the actual lesson
        // only in case of the student, maybe with param - force?
        checkStudentId(lesson);
        lesson.setStudentId(null);
        dynamoDao.updateLesson(lesson);
        return lesson;
    }

    public List<Lesson> getLessonsByStudent(CognitoUser student) {
        return dynamoDao.getLessonsByStudent(student.getId());
    }

    public List<Lesson> getLessonsByInstructor(CognitoUser instructor) {
        return dynamoDao.getLessonsByInstructor(instructor.getId());
    }

    public Lesson deleteLesson(Lesson lesson) throws Exception {
        checkInstructorId(lesson);
        dynamoDao.deleteLesson(lesson);
        return lesson;
    }

    private void checkInstructorId(Lesson lesson) throws Exception {
        if (lesson.getInstructorId() == null || lesson.getInstructorId().length() == 0) {
            throw new Exception("Lesson object does not contain instructorId");
        }
    }

    private void checkStudentId(Lesson lesson) throws Exception {
        if (lesson.getStudentId() == null || lesson.getStudentId().length() == 0) {
            throw new Exception("Lesson object does not contain studentId");
        }
    }
}
