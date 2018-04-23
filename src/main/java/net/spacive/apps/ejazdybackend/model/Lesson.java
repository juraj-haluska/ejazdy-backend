package net.spacive.apps.ejazdybackend.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Objects;

@DynamoDBTable(tableName="Lesson")
public class Lesson {

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

    public Lesson withInstructorId(String instructorId) {
        this.instructorId = instructorId;
        return this;
    }

    public Lesson withStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public Lesson withStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public Lesson withStopTime(String stopTime) {
        this.stopTime = stopTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson that = (Lesson) o;
        return Objects.equals(instructorId, that.instructorId) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(stopTime, that.stopTime) &&
                Objects.equals(studentId, that.studentId);
    }
}
