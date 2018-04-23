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

    @Autowired
    private DynamoDBMapper dbMapper;

    public List<Lesson> getLessonsByInstructor(String instructorId) {
        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        );

        return dbMapper.query(Lesson.class, queryExpression);
    }

    public List<Lesson> getLessonsByStudent(String instructorId, String studentId) {
        final DynamoDBQueryExpression<Lesson> queryExpression =
                new DynamoDBQueryExpression<Lesson>()
                        .withHashKeyValues(
                                new Lesson().withInstructorId(instructorId)
                        )
                        .withRangeKeyCondition(
                                "studentId",
                                new Condition()
                                        .withComparisonOperator(ComparisonOperator.EQ)
                                        .withAttributeValueList(new AttributeValue().withS(studentId))
                        );

        return dbMapper.query(Lesson.class, queryExpression);
    }

    public void createLesson(Lesson lesson) {
        dbMapper.save(lesson);
    }

    public void deleteLesson(Lesson lesson) {
        dbMapper.delete(lesson);
    }

    public void updateLesson(Lesson lesson) {
        DynamoDBMapperConfig.Builder config = DynamoDBMapperConfig.builder();
        config.setSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE);
        dbMapper.save(lesson, config.build());
    }
}