package net.spacive.apps.ejazdybackend.controller;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import net.spacive.apps.ejazdybackend.service.LessonService;
import net.spacive.apps.ejazdybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/students")
public class StudentController {

    private final UserService userService;
    private final LessonService lessonService;

    @Autowired
    public StudentController(UserService userService, LessonService lessonService) {
        this.userService = userService;
        this.lessonService = lessonService;
    }

    // list all students
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<CognitoUser> getAllStudents() {
        return userService.getAllStudents();
    }

    // invite new student by email
    // cognito will handle this
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CognitoUser inviteStudent(@RequestParam("email") String email) {
        return userService.inviteNewStudentByEmail(email);
    }

    // get single student by UUID
    @GetMapping("/{id}")
    public CognitoUser getStudent(@PathVariable String id) {
        return null;
    }

    // get all lessons of student
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @GetMapping("/{id}/lessons")
    public List<Lesson> getLessonsByStudent(@PathVariable String id) {
        return lessonService.getLessonsByStudent(id);
    }
}
