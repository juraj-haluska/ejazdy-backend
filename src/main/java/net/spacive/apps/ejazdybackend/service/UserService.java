package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final CognitoService cognitoService;
    private final LessonService lessonService;

    @Autowired
    public UserService(CognitoService cognitoService, LessonService lessonService) {
        this.cognitoService = cognitoService;
        this.lessonService = lessonService;
    }

    public CognitoUser inviteNewStudentByEmail(String email) {
        CognitoUser invitedUser = cognitoService.inviteUser(email);
        return cognitoService.addUserToGroup(
                invitedUser,
                "student"
        );
    }

    public CognitoUser inviteNewInstructorByEmail(String email) {
        CognitoUser invitedUser = cognitoService.inviteUser(email);
        return cognitoService.addUserToGroup(
                invitedUser,
                "instructor"
        );
    }

    public CognitoUser deleteStudent(String uuid) {

        // unregister student from lessons
        List<Lesson> lessons = lessonService.getLessonsByStudent(uuid);
        lessons.forEach(l -> {
            try {
                lessonService.unregisterStudentFromLesson(
                        uuid,
                        l.getInstructorId(),
                        l.getStartTime(),
                        true

                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        CognitoUser student = cognitoService.getUser(uuid);
        return cognitoService.deleteUser(student);
    }

    public CognitoUser deleteInstructor(String uuid) {

        // delete all lessons of this instructor
        List<Lesson> lessons = lessonService.getLessonsByInstructor(uuid);
        lessons.forEach(l -> lessonService.deleteLesson(uuid, l.getStartTime()));

        CognitoUser user = cognitoService.getUser(uuid);

        return cognitoService.deleteUser(user);
    }

    public List<CognitoUser> getAllInstructors() {
        return cognitoService.getUsersInGroup("instructor");
    }

    public List<CognitoUser> getAllStudents() {
        return cognitoService.getUsersInGroup("student");
    }

    public CognitoUser getUser(String uuid) {
        return cognitoService.getUser(uuid);
    }
}
