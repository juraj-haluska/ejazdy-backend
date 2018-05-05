package net.spacive.apps.ejazdybackend.controller;

import net.spacive.apps.ejazdybackend.config.Utils;
import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import net.spacive.apps.ejazdybackend.service.LessonService;
import net.spacive.apps.ejazdybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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
        return userService.getUser(id);
    }

    // get lessons of student
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @GetMapping("/{id}/lessons")
    public List<Lesson> getLessonsByStudent(
            @PathVariable String id,
            @RequestParam("since") Optional<String> since,
            @RequestParam("from") Optional<String> from,
            @RequestParam("to") Optional<String> to) {

        if (since.isPresent()) {
            Calendar sinceCal = Utils.parseISOString(since.get());
            return lessonService.getLessonsByStudentSince(
                    id,
                    sinceCal
            );
        } else if(from.isPresent() && to.isPresent()) {
            Calendar fromCal = Utils.parseISOString(from.get());
            Calendar toCal = Utils.parseISOString(to.get());
            return lessonService.getLessonsByStudentRange(
                    id,
                    fromCal,
                    toCal
            );
        } else {
            return lessonService.getLessonsByStudent(id);
        }
    }

    // "me" path is used because we can avoid processing user roles
    // inside controller methods
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/me/lessons")
    public List<Lesson> getLessonsByMe(
            Authentication auth,
            @RequestParam("since") Optional<String> since,
            @RequestParam("from") Optional<String> from,
            @RequestParam("to") Optional<String> to) {

        CognitoUser student = (CognitoUser) auth.getPrincipal();
        student = userService.getUser(student.getId());

        return getLessonsByStudent(
                student.getId(),
                since,
                from,
                to
        );
    }
}
