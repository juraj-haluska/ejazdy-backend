package net.spacive.apps.ejazdybackend.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import net.spacive.apps.ejazdybackend.model.entity.LessonEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.UUID;

@Repository
public class DynamoDao {

    @Autowired
    private DynamoDBMapper dbMapper;

    public void test() {
        LessonEntity lesson = new LessonEntity();
        lesson.setStartsAt(Calendar.getInstance());
        lesson.setInstructorId(UUID.randomUUID().toString());

        dbMapper.save(lesson);
    }
}
