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

@Repository
public class DynamoDao {

    private final DynamoDBMapper dbMapper;

    @Autowired
    public DynamoDao(DynamoDBMapper dbMapper) {
        this.dbMapper = dbMapper;
    }

    public List<Lesson> getLessonsByInstructor(String instructorId) {
        checkValidId(instructorId);

        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        );

        return dbMapper.query(Lesson.class, queryExpression);
    }

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

    public void deleteLesson(Lesson lesson) {
        dbMapper.delete(lesson);
    }

    public void updateLesson(Lesson lesson, boolean ignoreNullAttrs) {
        DynamoDBMapperConfig.Builder config = DynamoDBMapperConfig.builder();
        if (ignoreNullAttrs) {
            config.setSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES);
        } else {
            config.setSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE);
        }

        dbMapper.save(lesson, config.build());
    }

    private void checkValidId(String  id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        if (id.length() == 0) throw new IllegalArgumentException("ID cannot be empty string");
        UUID.fromString(id);
    }
}
