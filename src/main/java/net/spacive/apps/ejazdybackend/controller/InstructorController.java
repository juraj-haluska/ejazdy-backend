package net.spacive.apps.ejazdybackend.controller;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import net.spacive.apps.ejazdybackend.service.LessonService;
import net.spacive.apps.ejazdybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/instructors")
public class InstructorController {

    private final UserService userService;
    private final LessonService lessonService;

    @Autowired
    public InstructorController(UserService userService, LessonService lessonService) {
        this.userService = userService;
        this.lessonService = lessonService;
    }

    // list all instructors
    @GetMapping
    public List<CognitoUser> getAllInstructors() {
        return userService.getAllInstructors();
    }

    // invite new instructor by email
    // cognito will handle this
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CognitoUser inviteInstructor(@RequestParam("email") String email) {
        return userService.inviteNewInstructorByEmail(email);
    }

    // get single instructor by UUID
    // accessible by every role
    @GetMapping("/{id}")
    public CognitoUser getInstructor(@PathVariable String id) {
        return userService.getUser(id);
    }

    // get lessons of instructor specified by id
    // accessible by every role
    @GetMapping("/{id}/lessons")
    public List<Lesson> getInstructorsLessons(@PathVariable String id) {
        return lessonService.getLessonsByInstructor(id);
    }

    // create lesson - instructors are allowed to create lessons only for themselves
    @PostMapping("/{id}/lessons")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public Lesson createLesson(
            @PathVariable String id,
            @RequestBody Lesson lesson,
            Authentication auth) throws Exception {

        // check if instructor's id in token is same as id in path
        CognitoUser instructor = (CognitoUser) auth.getPrincipal();

        if (instructor.getId().equals(id)) {
            return lessonService.createLessonByInstructor(instructor, lesson);
        } else {
            throw new Exception("instructor id must be same as instructor id in token");
        }
    }

    // delete lesson - admin without restrictions, instructor may
    // delete only his own lessons
    @DeleteMapping("/{id}/lessons/{startTime}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public Lesson deleteLesson(
            @PathVariable String id,
            @PathVariable String startTime,
            Authentication auth) throws Exception {

        final boolean isAdmin = auth.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        if (!isAdmin) {
            CognitoUser instructor = (CognitoUser) auth.getPrincipal();

            if (!instructor.getId().equals(id)) {
                throw new Exception("wrong instructor id");
            }
        }

        return lessonService.deleteLesson(id, startTime);
    }

    // register student to lesson - admin no restrictions
    // instructor only to himself
    @PostMapping("/{id}/lessons/{startTime}/student/{studentId}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public Lesson addStudentToLesson(
            @PathVariable String id,
            @PathVariable String startTime,
            @PathVariable String studentId,
            Authentication auth) throws Exception {

        final boolean isAdmin = auth.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        if (!isAdmin) {
            CognitoUser instructor = (CognitoUser) auth.getPrincipal();

            if (!instructor.getId().equals(id)) {
                throw new Exception("wrong instructor id");
            }
        }

        CognitoUser student = userService.getUser(studentId);

        return lessonService.registerStudentToLesson(
                student,
                id,
                startTime
        );
    }

    // register calling student to lesson
    // id of calling student is extracted from jwt
    @PostMapping("/{id}/lessons/{startTime}/student/me")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public Lesson addInvokingStudentToLesson(
            @PathVariable String id,
            @PathVariable String startTime,
            Authentication auth) throws Exception {

        CognitoUser student = (CognitoUser) auth.getPrincipal();

        return lessonService.registerStudentToLesson(
                student,
                id,
                startTime
        );
    }

    // admin - no restrictions
    // instructor - only his students
    @DeleteMapping("/{id}/lessons/{startTime}/student/{studentId}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public Lesson deleteStudentFromLesson(
            @PathVariable String id,
            @PathVariable String startTime,
            @PathVariable String studentId,
            Authentication auth) throws Exception {

        final boolean isAdmin = auth.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        if (!isAdmin) {
            CognitoUser instructor = (CognitoUser) auth.getPrincipal();

            if (!instructor.getId().equals(id)) {
                throw new Exception("wrong instructor id");
            }
        }

        return lessonService.unregisterStudentFromLesson(
                studentId,
                id,
                startTime,
                true
        );
    }

    // id of calling student is extracted from jwt
    @DeleteMapping("/{id}/lessons/{startTime}/student/me")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public Lesson deleteInvokingStudentFromLesson(
            @PathVariable String id,
            @PathVariable String startTime,
            Authentication auth) throws Exception {

        CognitoUser student = (CognitoUser) auth.getPrincipal();

        return lessonService.unregisterStudentFromLesson(
                student.getId(),
                id,
                startTime,
                false
        );
    }

}
