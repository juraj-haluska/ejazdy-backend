package net.spacive.apps.ejazdybackend.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DynamoDao {

    private final DynamoDBMapper dbMapper;

    @Autowired
    public DynamoDao(DynamoDBMapper dbMapper) {
        this.dbMapper = dbMapper;
    }

    public List<Lesson> getLessonsByInstructor(String instructorId) {
        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        );

        return dbMapper.query(Lesson.class, queryExpression);
    }

    public List<Lesson> getLessonsByStudent(String studentId) {
        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withStudentId(studentId)
                        )
                        .withConsistentRead(false);

        return dbMapper.query(Lesson.class, queryExpression);
    }

    public Lesson getLessonByInstructor(String instructorId, String startTime) {
        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        ).withRangeKeyCondition(
                        "startTime",
                        new Condition()
                                .withComparisonOperator(ComparisonOperator.EQ)
                                .withAttributeValueList(
                                        new AttributeValue(startTime)
                                )
                );

        List<Lesson> lessons = dbMapper.query(Lesson.class, queryExpression);

        if (lessons != null && lessons.size() > 0) {
            return lessons.get(0);
        } else {
            return null;
        }
    }

    public Lesson getLessonByStudent(String studentId, String startTime) {
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
                                                new AttributeValue(startTime)
                                        )
                        );

        List<Lesson> lessons = dbMapper.query(Lesson.class, queryExpression);

        if (lessons != null && lessons.size() > 0) {
            return lessons.get(0);
        } else {
            return null;
        }
    }

    public void createLesson(Lesson lesson) {
        dbMapper.save(lesson);
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
}
