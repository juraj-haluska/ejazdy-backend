package net.spacive.apps.ejazdybackend.model.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Objects;

@DynamoDBTable(tableName="Lesson")
public class LessonEntity {

    private String instructorId;
    private String startTime;
    private String stopTime;
    private String studentId;

    @DynamoDBHashKey
    public String getInstructorId() {
        return instructorId;
    }
    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    @DynamoDBRangeKey
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @DynamoDBIndexRangeKey(localSecondaryIndexName = "InstructorStudent")
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @DynamoDBAttribute
    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public LessonEntity withInstructorId(String instructorId) {
        this.instructorId = instructorId;
        return this;
    }

    public LessonEntity withStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public LessonEntity withStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public LessonEntity withStopTime(String stopTime) {
        this.stopTime = stopTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonEntity that = (LessonEntity) o;
        return Objects.equals(instructorId, that.instructorId) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(stopTime, that.stopTime) &&
                Objects.equals(studentId, that.studentId);
    }
}
