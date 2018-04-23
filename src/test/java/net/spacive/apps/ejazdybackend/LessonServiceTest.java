package net.spacive.apps.ejazdybackend;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import net.spacive.apps.ejazdybackend.service.LessonService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LessonServiceTest {

    private static final Logger log = LoggerFactory.getLogger(LessonServiceTest.class.getName());

    @Autowired
    private LessonService lessonService;

    @Test
    public void createAndDeleteLesson() {

        // create new lesson
        CognitoUser instructor = createRandomInstructor();

        final String startTime = "123456";
        final String stopTime = "654321";

        lessonService.createLessonByInstructor(
                instructor,
                startTime,
                stopTime
        );

        // check if it exists in db
        Lesson newLesson = null;

        for (Lesson lesson : lessonService.getLessonsByInstructor(instructor)) {
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
            deletedLesson = lessonService.deleteLessonByInstructor(
                    instructor,
                    newLesson
            );
        } catch (Exception e) {
            e.printStackTrace();
            deletedLesson = null;
        }

        Assert.assertNotNull(deletedLesson);
    }

    private CognitoUser createRandomInstructor() {
        return new CognitoUser.Builder()
                .withId(UUID.randomUUID().toString())
                .withEmail("test@test.test")
                .withPhone("112")
                .withUserGroup("instructor")
                .build();
    }
}
