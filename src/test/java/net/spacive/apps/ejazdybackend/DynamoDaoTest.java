package net.spacive.apps.ejazdybackend;

import net.spacive.apps.ejazdybackend.database.DynamoDao;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DynamoDaoTest {

    private static final Logger log = LoggerFactory.getLogger(DynamoDaoTest.class.getName());

    @Autowired
    private DynamoDao dynamoDao;

    @Test(expected = Exception.class)
    public void getLessonsByNullInstructor() {
        dynamoDao.getLessonsByInstructor(null);
    }

    @Test(expected = Exception.class)
    public void getLessonsByEmptyInstructor() {
        dynamoDao.getLessonsByInstructor("");
    }

    @Test(expected = Exception.class)
    public void getLessonsByNoUUIDInstructor() {
        dynamoDao.getLessonsByInstructor("i wanna an exception");
    }

    @Test
    public void createAndDeleteLesson() {
        final String instructor = UUID.randomUUID().toString();
        final Calendar startTime = Calendar.getInstance();

        Lesson newLesson = new Lesson()
                .withInstructorId(instructor)
                .withStartTime(startTime);

        dynamoDao.createLesson(newLesson);

        boolean contains = false;

        List<Lesson> fetchedLessons = dynamoDao.getLessonsByInstructor(instructor);

        log.info(fetchedLessons.toString());

        for (Lesson lesson : fetchedLessons) {
            if (lesson.equals(newLesson)) {
                contains = true;
            }
        }

        Assert.assertTrue(contains);

        dynamoDao.deleteLesson(newLesson);

        contains = false;

        fetchedLessons = dynamoDao.getLessonsByInstructor(instructor);
        for (Lesson lesson : fetchedLessons) {
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
        final Calendar startTime = Calendar.getInstance();

        Lesson newLesson = new Lesson()
                .withInstructorId(instructor)
                .withStartTime(startTime);

        dynamoDao.createLesson(newLesson);

        Lesson toUpdateLesson = new Lesson()
                .withInstructorId(instructor)
                .withStartTime(startTime)
                .withStopTime(startTime)
                .withStudentId(student);

        dynamoDao.updateLesson(toUpdateLesson, true);

        List<Lesson> studentLessons = dynamoDao.getLessonsByStudent(
                student
        );

        Assert.assertNotEquals(studentLessons, null);
        Assert.assertNotEquals(studentLessons, 0);

        boolean containsUpdated = false;

        List<Lesson> instructorLessons = dynamoDao.getLessonsByInstructor(instructor);
        for (Lesson lesson : instructorLessons) {
            if (lesson.equals(toUpdateLesson)) {
                containsUpdated = true;
            }
        }

        Assert.assertTrue(containsUpdated);

        dynamoDao.deleteLesson(newLesson);
    }

    @Test
    public void getLessonsByRandomInstructor() {
        final String instructor = UUID.randomUUID().toString();

        List<Lesson> fetchedLessons = dynamoDao.getLessonsByInstructor(instructor);

        Assert.assertNotNull(fetchedLessons);
        Assert.assertEquals(fetchedLessons.size(), 0);
    }

    @Test(expected = Exception.class)
    public void createLessonWithoutPrimaryKey() {
        dynamoDao.createLesson(new Lesson());
    }

    @Test
    public void getLessonsByInstructor() {
        final int lessonCount = 10;

        String instructorId = UUID.randomUUID().toString();

        List<Calendar> startTimes = new ArrayList<>();

        // generate lessonCount lessons
        IntStream.range(0, lessonCount).forEach(n -> {
            startTimes.add(Calendar.getInstance());
            dynamoDao.createLesson(
                    new Lesson()
                            .withInstructorId(instructorId)
                            .withStartTime(startTimes.get(n))
            );
        });

        List<Lesson> fetchedLessons = dynamoDao.getLessonsByInstructor(instructorId);

        Assert.assertNotNull(fetchedLessons);
        Assert.assertEquals(fetchedLessons.size(), lessonCount);

        // cleanup
        startTimes.forEach(startTime -> dynamoDao.deleteLesson(
                new Lesson()
                        .withInstructorId(instructorId)
                        .withStartTime(startTime)
        ));
    }

    @Test
    public void getLessonsByStudent() {
        final int lessonCount = 10;

        final String instructorId = UUID.randomUUID().toString();
        final String studentId = UUID.randomUUID().toString();

        List<Calendar> startTimes = new ArrayList<>();

        // generate lessonCount lessons
        IntStream.range(0, lessonCount).forEach(n -> {
            startTimes.add(Calendar.getInstance());
            dynamoDao.createLesson(
                    new Lesson()
                            .withInstructorId(instructorId)
                            .withStartTime(startTimes.get(n))
                            .withStudentId(studentId)
            );
        });

        List<Lesson> fetchedLessons = dynamoDao.getLessonsByStudent(studentId);

        Assert.assertNotNull(fetchedLessons);
        Assert.assertEquals(fetchedLessons.size(), lessonCount);

        // cleanup
        startTimes.forEach(startTime -> dynamoDao.deleteLesson(
                new Lesson()
                        .withInstructorId(instructorId)
                        .withStartTime(startTime)
        ));
    }

    @Test
    public void getLessonByInstructor() {
        final String instructorId = UUID.randomUUID().toString();
        final Calendar startTime = Calendar.getInstance();

        final Lesson newLesson = new Lesson()
                .withInstructorId(instructorId)
                .withStartTime(startTime);

        dynamoDao.createLesson(newLesson);


        Lesson fetched = dynamoDao.getLessonByInstructor(instructorId, startTime);

        Assert.assertNotNull(fetched);
        Assert.assertEquals(newLesson, fetched);

        // cleanup
        dynamoDao.deleteLesson(fetched);
    }

    @Test
    public void getLessonByStudent() {
        final String instructorId = UUID.randomUUID().toString();
        final String studentId = UUID.randomUUID().toString();
        final Calendar startTime = Calendar.getInstance();

        final Lesson newLesson = new Lesson()
                .withInstructorId(instructorId)
                .withStartTime(startTime)
                .withStudentId(studentId);

        dynamoDao.createLesson(newLesson);


        Lesson fetched = dynamoDao.getLessonByStudent(studentId, startTime);

        Assert.assertNotNull(fetched);
        Assert.assertEquals(newLesson, fetched);

        // cleanup
        dynamoDao.deleteLesson(fetched);
    }
}
