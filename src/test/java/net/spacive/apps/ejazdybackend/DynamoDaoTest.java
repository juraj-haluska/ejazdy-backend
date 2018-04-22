package net.spacive.apps.ejazdybackend;

import net.spacive.apps.ejazdybackend.dao.DynamoDao;
import net.spacive.apps.ejazdybackend.model.entity.LessonEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DynamoDaoTest {

    private static final Logger log = LoggerFactory.getLogger(DynamoDao.class.getName());

    @Autowired
    private DynamoDao dynamoDao;

    @Test
    public void createAndDeleteLesson() {

        final String instructor = UUID.randomUUID().toString();
        final String startTime = Calendar.getInstance().toString();

        LessonEntity newLesson = new LessonEntity()
                .withInstructorId(instructor)
                .withStartTime(startTime);

        dynamoDao.createLesson(newLesson);

        boolean contains = false;

        List<LessonEntity> fetchedLessons = dynamoDao.getLessonsByInstructor(instructor);
        for (LessonEntity lesson: fetchedLessons) {
            if (lesson.equals(newLesson)) {
                contains = true;
            }
        }

        Assert.assertTrue(contains);

        dynamoDao.deleteLesson(newLesson);

        contains = false;

        fetchedLessons = dynamoDao.getLessonsByInstructor(instructor);
        for (LessonEntity lesson: fetchedLessons) {
            if (lesson.equals(newLesson)) {
                contains = true;
            }
        }

        Assert.assertFalse(contains);
    }

    @Test
    public void updateLessonTest() {

        final String instructor = UUID.randomUUID().toString();
        final String student = UUID.randomUUID().toString();
        final String startTime = Calendar.getInstance().toString();

        LessonEntity newLesson = new LessonEntity()
                .withInstructorId(instructor)
                .withStartTime(startTime);

        dynamoDao.createLesson(newLesson);

        LessonEntity toUpdateLesson = new LessonEntity()
                .withInstructorId(instructor)
                .withStartTime(startTime)
                .withStopTime(startTime)
                .withStudentId(student);

        dynamoDao.updateLesson(toUpdateLesson);

        List<LessonEntity> studentLessons = dynamoDao.getLessonsByStudent(
                instructor,
                student
        );

        Assert.assertNotEquals(studentLessons, null);
        Assert.assertNotEquals(studentLessons, 0);

        boolean containsUpdated = false;

        List<LessonEntity> instructorLessons = dynamoDao.getLessonsByInstructor(instructor);
        for (LessonEntity lesson: instructorLessons) {
            if (lesson.equals(toUpdateLesson)) {
                containsUpdated = true;
            }
        }

        Assert.assertTrue(containsUpdated);

        dynamoDao.deleteLesson(newLesson);
    }
}
