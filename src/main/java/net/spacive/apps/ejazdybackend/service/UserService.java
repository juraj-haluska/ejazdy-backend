package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private CognitoService cognitoService;

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

    public List<CognitoUser> getAllInstructors() {
        return cognitoService.getUsersInGroup("instructor");
    }

    public List<CognitoUser> getAllStudents() {
        return cognitoService.getUsersInGroup("student");
    }

}
