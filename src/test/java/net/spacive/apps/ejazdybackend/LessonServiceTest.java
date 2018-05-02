package net.spacive.apps.ejazdybackend;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import net.spacive.apps.ejazdybackend.service.LessonService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LessonServiceTest {

    @Autowired
    private LessonService lessonService;

    @Test
    public void createAndDeleteLesson() {

        // create new lesson
        CognitoUser instructor = createRandomInstructor();

        final String startTime = "123456";
        final String stopTime = "654321";

        final Lesson toCreateLesson = new Lesson()
                .withStartTime(startTime)
                .withStopTime(stopTime);

        lessonService.createLessonByInstructor(
                instructor.getId(),
                toCreateLesson
        );

        // check if it exists in db
        Lesson newLesson = null;

        for (Lesson lesson : lessonService.getLessonsByInstructor(instructor.getId())) {
            if (lesson.getStartTime().equals(startTime) &&
                    lesson.getStopTime().equals(stopTime)
                    ) {
                newLesson = lesson;
            }
        }

        Assert.assertNotNull(newLesson);
        Assert.assertEquals(newLesson.getInstructorId(), instructor.getId());

        // delete lesson
        Lesson deletedLesson;
        try {
            deletedLesson = lessonService.deleteLesson(
                    instructor.getId(),
                    newLesson.getStartTime()
            );
        } catch (Exception e) {
            e.printStackTrace();
            deletedLesson = null;
        }

        Assert.assertNotNull(deletedLesson);
    }

    @Test
    public void registerStudentToLesson() {
        CognitoUser instructor = createRandomInstructor();
        CognitoUser student = createRandomStudent();

        final Lesson toCreateLesson = new Lesson()
                .withStartTime(Calendar.getInstance().toString())
                .withStopTime(Calendar.getInstance().toString());

        // create lesson
        Lesson createdLesson = lessonService.createLessonByInstructor(
                instructor.getId(),
                toCreateLesson
        );

        // register student to lesson
        Lesson registeredLesson;
        try {
            registeredLesson = lessonService.registerStudentToLesson(
                    student.getId(),
                    instructor.getId(),
                    createdLesson.getStartTime()
            );
        } catch (Exception e) {
            e.printStackTrace();
            registeredLesson = null;
        }

        Assert.assertNotNull(registeredLesson);
        Assert.assertEquals(registeredLesson.getStudentId(), student.getId());

        Lesson studentLesson = null;
        for(Lesson lesson: lessonService.getLessonsByStudent(student.getId())) {
            if (lesson.equals(registeredLesson)) {
                studentLesson = lesson;
            }
        }

        Assert.assertNotNull(studentLesson);
        Assert.assertEquals(studentLesson, registeredLesson);

        // cleanup
        lessonService.deleteLesson(instructor.getId(), createdLesson.getStartTime());
    }

    @Test
    public void unregisterStudentFromLesson() throws Exception {
        CognitoUser instructor = createRandomInstructor();
        CognitoUser student = createRandomStudent();

        final Lesson toCreateLesson = new Lesson()
                .withStartTime(Calendar.getInstance().toString())
                .withStopTime(Calendar.getInstance().toString());

        // create lesson
        Lesson createdLesson = lessonService.createLessonByInstructor(
                instructor.getId(),
                toCreateLesson
        );


        // register student to lesson
        Lesson registeredLesson = lessonService.registerStudentToLesson(
                student.getId(),
                instructor.getId(),
                createdLesson.getStartTime()
        );

        Lesson unregisteredLesson;
        try {
            unregisteredLesson = lessonService.unregisterStudentFromLesson(
                    student.getId(),
                    instructor.getId(),
                    createdLesson.getStartTime(),
                    true
            );
        } catch (Exception e) {
            e.printStackTrace();
            unregisteredLesson = null;
        }

        Assert.assertNotNull(unregisteredLesson);

        // cleanup
        lessonService.deleteLesson(instructor.getId(), createdLesson.getStartTime());
    }

    private CognitoUser createRandomInstructor() {
        return new CognitoUser.Builder()
                .withId(UUID.randomUUID().toString())
                .withEmail("test@test.test")
                .withPhone("112")
                .withUserGroup("instructor")
                .build();
    }

    private CognitoUser createRandomStudent() {
        return new CognitoUser.Builder()
                .withId(UUID.randomUUID().toString())
                .withEmail("test@test.test")
                .withPhone("123")
                .withUserGroup("student")
                .build();
    }
}
