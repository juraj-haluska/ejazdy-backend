package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.database.DynamoDao;
import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        checkStudentId(lesson);
        lesson.setStudentId(null);
        dynamoDao.updateLesson(lesson);
        return lesson;
    }

    public List<Lesson> getLessonsByStudent(CognitoUser student) {

        // not the best approach
        // TODO new index for this in dynamo?

        List<Lesson> studentsLessons = new ArrayList<>();

        List<CognitoUser> instructors = cognitoService.getUsersInGroup("instructor");

        instructors.forEach(instructor -> {
            List<Lesson> lessons = dynamoDao.getLessonsByStudent(
                    instructor.getId(),
                    student.getId()
            );
            studentsLessons.addAll(lessons);
        });

        return studentsLessons;
    }

    public List<Lesson> getLessonsByStudent(CognitoUser student, CognitoUser instructor) {
        List<Lesson> studentsLessons = dynamoDao.getLessonsByStudent(
                instructor.getId(),
                student.getId()
        );

        return studentsLessons;
    }

    public List<Lesson> getLessonsByInstructor(CognitoUser instructor) {
        return dynamoDao.getLessonsByInstructor(instructor.getId());
    }

    public Lesson deleteLessonByInstructor(CognitoUser instructor, Lesson lesson) throws Exception {
        checkInstructorId(lesson);

        if (!instructor.getId().equals(lesson.getInstructorId())) {
            throw new Exception("lesson has to belong to invoking instructor");
        }

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
