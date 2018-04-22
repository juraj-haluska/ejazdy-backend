package net.spacive.apps.ejazdybackend.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import net.spacive.apps.ejazdybackend.model.entity.LessonEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DynamoDao {

    @Autowired
    private DynamoDBMapper dbMapper;

    public List<LessonEntity> getLessonsByInstructor(String instructorId) {
        final DynamoDBQueryExpression<LessonEntity> queryExpression =
                new DynamoDBQueryExpression<LessonEntity>()
                        .withHashKeyValues(
                                new LessonEntity().withInstructorId(instructorId)
                        );

        return dbMapper.query(LessonEntity.class, queryExpression);
    }

    public List<LessonEntity> getLessonsByStudent(String instructorId, String studentId) {
        final DynamoDBQueryExpression<LessonEntity> queryExpression =
                new DynamoDBQueryExpression<LessonEntity>()
                        .withHashKeyValues(
                                new LessonEntity().withInstructorId(instructorId)
                        )
                        .withRangeKeyCondition(
                                "studentId",
                                new Condition()
                                        .withComparisonOperator(ComparisonOperator.EQ)
                                        .withAttributeValueList(new AttributeValue().withS(studentId))
                        );

        return dbMapper.query(LessonEntity.class, queryExpression);
    }

    public void createLesson(LessonEntity lesson) {
        dbMapper.save(lesson);
    }

    public void deleteLesson(LessonEntity lesson) {
        dbMapper.delete(lesson);
    }

    public void updateLesson(LessonEntity lesson) {
        DynamoDBMapperConfig.Builder config = DynamoDBMapperConfig.builder();
        config.setSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE);
        dbMapper.save(lesson, config.build());
    }
}
