package net.spacive.apps.ejazdybackend.controller;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/instructor")
    public List<CognitoUser> getAllInstructors() {
        return userService.getAllInstructors();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/student")
    public CognitoUser inviteStudent(@RequestParam("email") String email) {
        return userService.inviteNewStudentByEmail(email);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/instructor")
    public CognitoUser inviteInstructor(@RequestParam("email") String email) {
        return userService.inviteNewInstructorByEmail(email);
    }
}
