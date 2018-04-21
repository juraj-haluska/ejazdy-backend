package net.spacive.apps.ejazdybackend.model.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Calendar;

@DynamoDBTable(tableName="Lesson")
public class LessonEntity {
    private String instructorId;
    private Calendar startsAt;

    @DynamoDBHashKey(attributeName="instructor")
    public String getInstructorId() {
        return instructorId;
    }
    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    @DynamoDBRangeKey(attributeName="startsAd")
    public Calendar getStartsAt() {
        return startsAt;
    }
    public void setStartsAt(Calendar startsAt) {
        this.startsAt = startsAt;
    }
}
