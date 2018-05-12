package net.spacive.apps.ejazdybackend.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.util.DateUtils;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * This class is an implementation of database access object
 * for AWS service - DynamoDB.
 *
 * <p>This class manipulates mainly with Lesson.
 *
 * @author  Juraj Haluska
 * @see Lesson
 */
@Repository
public class DynamoDao {

    /**
     * reference to dynamo db mapper provided by AWS SDK.
     */
    private final DynamoDBMapper dbMapper;

    /**
     * Constructor.
     * @param dbMapper injected param.
     */
    @Autowired
    public DynamoDao(DynamoDBMapper dbMapper) {
        this.dbMapper = dbMapper;
    }

    /**
     * Get list of lessons by instructor.
     *
     * @param instructorId an unique id of instructor.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByInstructor(String instructorId) {
        checkValidId(instructorId);

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        );

        return dbMapper.query(Lesson.class, queryExpression);
    }

    /**
     * Get list of lessons by instructor since date.
     *
     * @param instructorId an unique id of instructor.
     * @param since the date since which the lessons should be fetched.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByInstructorSince(
            String instructorId,
            Calendar since) {

        checkValidId(instructorId);

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        ).withRangeKeyCondition(
                        "startTime",
                        new Condition()
                                .withComparisonOperator(ComparisonOperator.GE)
                                .withAttributeValueList(
                                        new AttributeValue(
                                                DateUtils.formatISO8601Date(since.getTime())
                                        )
                                )
                );

        return dbMapper.query(Lesson.class, queryExpression);
    }

    /**
     * Get list of lessons within the date range by instructor.
     *
     * @param instructorId an unique id of instructor.
     * @param from starting date.
     * @param to ending date.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByInstructorRange(
            String instructorId,
            Calendar from,
            Calendar to) {

        checkValidId(instructorId);

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        ).withRangeKeyCondition(
                        "startTime",
                        new Condition()
                                .withComparisonOperator(ComparisonOperator.BETWEEN)
                                .withAttributeValueList(
                                        new AttributeValue(
                                                DateUtils.formatISO8601Date(from.getTime())
                                        ),
                                        new AttributeValue(
                                                DateUtils.formatISO8601Date(to.getTime())
                                        )
                                )
                );

        return dbMapper.query(Lesson.class, queryExpression);
    }

    /**
     * Get list of lessons by student.
     *
     * @param studentId an unique id of student.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByStudent(String studentId) {
        checkValidId(studentId);

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withStudentId(studentId)
                        )
                        .withConsistentRead(false);

        return dbMapper.query(Lesson.class, queryExpression);
    }

    /**
     * Get list of lessons by student since date.
     *
     * @param studentId an unique id of student.
     * @param since the date since which the lessons should be fetched.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByStudentSince(
            String studentId,
            Calendar since) {

        checkValidId(studentId);

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withStudentId(studentId)
                        )
                        .withConsistentRead(false)
                        .withRangeKeyCondition(
                                "startTime",
                                new Condition()
                                        .withComparisonOperator(ComparisonOperator.GE)
                                        .withAttributeValueList(
                                                new AttributeValue(
                                                        DateUtils.formatISO8601Date(since.getTime())
                                                )
                                        )
                        );

        return dbMapper.query(Lesson.class, queryExpression);
    }

    /**
     * Get list of lessons within the date range by student.
     *
     * @param studentId an unique id of student.
     * @param from starting date.
     * @param to ending date.
     * @return list of lessons.
     */
    public List<Lesson> getLessonsByStudentRange(
            String studentId,
            Calendar from,
            Calendar to) {

        checkValidId(studentId);

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withStudentId(studentId)
                        )
                        .withConsistentRead(false)
                        .withRangeKeyCondition(
                                "startTime",
                                new Condition()
                                        .withComparisonOperator(ComparisonOperator.BETWEEN)
                                        .withAttributeValueList(
                                                new AttributeValue(
                                                        DateUtils.formatISO8601Date(from.getTime())
                                                ),
                                                new AttributeValue(
                                                        DateUtils.formatISO8601Date(to.getTime())
                                                )
                                        )
                        );

        return dbMapper.query(Lesson.class, queryExpression);
    }

    /**
     * Get details of single lesson by instructor.
     *
     * @param instructorId an unique id of instructor.
     * @param startTime start time of the lesson.
     * @return lesson instance.
     */
    public Lesson getLessonByInstructor(String instructorId, Calendar startTime) {
        checkValidId(instructorId);

        final String startTimeString = DateUtils.formatISO8601Date(startTime.getTime());

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        ).withRangeKeyCondition(
                        "startTime",
                        new Condition()
                                .withComparisonOperator(ComparisonOperator.EQ)
                                .withAttributeValueList(
                                        new AttributeValue(startTimeString)
                                )
                );

        List<Lesson> lessons = dbMapper.query(Lesson.class, queryExpression);

        if (lessons != null && lessons.size() > 0) {
            return lessons.get(0);
        } else {
            return null;
        }
    }

    /**
     * Get details of single lesson by student.
     *
     * @param studentId an unique id of student.
     * @param startTime start time of the lesson.
     * @return lesson instance.
     */
    public Lesson getLessonByStudent(String studentId, Calendar startTime) {
        checkValidId(studentId);

        final String startTimeString = DateUtils.formatISO8601Date(startTime.getTime());

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withStudentId(studentId)
                        )
                        .withConsistentRead(false)
                        .withRangeKeyCondition(
                                "startTime",
                                new Condition()
                                        .withComparisonOperator(ComparisonOperator.EQ)
                                        .withAttributeValueList(
                                                new AttributeValue(startTimeString)
                                        )
                        );

        List<Lesson> lessons = dbMapper.query(Lesson.class, queryExpression);

        if (lessons != null && lessons.size() > 0) {
            return lessons.get(0);
        } else {
            return null;
        }
    }

    /**
     * Create new lesson.
     *
     * <p>instructorId and startTime have to be present in
     * lesson object passed as param.
     *
     * @param lesson lesson which should be created.
     * @return true if lesson was created.
     */
    public boolean createLesson(Lesson lesson) {
        Lesson exists = getLessonByInstructor(
                lesson.getInstructorId(),
                lesson.getStartTime()
        );
        if (exists == null) {
            dbMapper.save(lesson);
            return true;
        }

        return false;
    }

    /**
     * Delete lesson.
     *
     * @param lesson lesson which should be deleted.
     */
    public void deleteLesson(Lesson lesson) {
        dbMapper.delete(lesson);
    }

    /**
     * Update lesson
     *
     * <p>if ignoreNullAttrs is true, corresponding null
     * params in lesson will be unchanged.
     *
     * @param lesson lesson which should be updated.
     * @param ignoreNullAttrs ignore null attributes.
     */
    public void updateLesson(Lesson lesson, boolean ignoreNullAttrs) {
        DynamoDBMapperConfig.Builder config = DynamoDBMapperConfig.builder();
        if (ignoreNullAttrs) {
            config.setSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES);
        } else {
            config.setSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE);
        }

        dbMapper.save(lesson, config.build());
    }

    /**
     * Check format of id
     *
     * <p>If the format is invalid, method will cause
     * runtime exception.
     *
     * @param id
     */
    private void checkValidId(String id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        if (id.length() == 0) throw new IllegalArgumentException("ID cannot be empty string");
        UUID.fromString(id);
    }
}
