package net.spacive.apps.ejazdybackend.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Objects;

@DynamoDBTable(tableName="Lesson")
public class Lesson {

    private String instructorId;
    private String startTime;
    private String stopTime;
    private String studentId;
    private String instructorName;
    private String studentName;

    @DynamoDBHashKey
    public String getInstructorId() {
        return instructorId;
    }
    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    @DynamoDBRangeKey
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "StudentIdStartTime")
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "StudentIdStartTime")
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

    @DynamoDBAttribute
    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    @DynamoDBAttribute
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public Lesson withInstructorName(String instructorName) {
        this.instructorName = instructorName;
        return this;
    }

    public Lesson withStudentName(String studentName) {
        this.studentName = studentName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(instructorId, lesson.instructorId) &&
                Objects.equals(startTime, lesson.startTime) &&
                Objects.equals(stopTime, lesson.stopTime) &&
                Objects.equals(studentId, lesson.studentId) &&
                Objects.equals(instructorName, lesson.instructorName) &&
                Objects.equals(studentName, lesson.studentName);
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "instructorId='" + instructorId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", stopTime='" + stopTime + '\'' +
                ", studentId='" + studentId + '\'' +
                ", instructorName='" + instructorName + '\'' +
                ", studentName='" + studentName + '\'' +
                '}';
    }
}
