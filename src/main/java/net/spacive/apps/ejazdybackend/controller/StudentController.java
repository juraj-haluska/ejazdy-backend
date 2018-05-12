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

/**
 * REST API for students resource.
 *
 * @author  Juraj Haluska
 */
@RestController
@CrossOrigin
@RequestMapping("/students")
public class StudentController {

    /**
     * Instance of UserService.
     */
    private final UserService userService;

    /**
     * Instance of LessonService.
     */
    private final LessonService lessonService;

    /**
     * Constructor.
     *
     * @param userService injected param.
     * @param lessonService injected param.
     */
    @Autowired
    public StudentController(UserService userService, LessonService lessonService) {
        this.userService = userService;
        this.lessonService = lessonService;
    }

    /**
     * List all students.
     *
     * <p>Allowed only for instructor and admin.
     *
     * @return list of students.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<CognitoUser> getAllStudents() {
        return userService.getAllStudents();
    }

    /**
     * Invite new student by email.
     *
     * <p>Allowed only for admin.
     *
     * @param email email of student to invite
     * @return new user instance.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CognitoUser inviteStudent(@RequestParam("email") String email) {
        return userService.inviteNewStudentByEmail(email);
    }

    /**
     * Delete student.
     *
     * <p>Allowed only for admin.
     *
     * @param id an unique id of student.
     * @return instance of deleted student.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CognitoUser deleteStudent(@PathVariable String id) {
        return userService.deleteStudent(id);
    }

    /**
     * Get single student by UUID.
     *
     * <p>Allowed only for admin and instructor.
     *
     * @param id an unique id of student.
     * @return instance of student.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @GetMapping("/{id}")
    public CognitoUser getStudent(@PathVariable String id) {
        return userService.getUser(id);
    }

    /**
     * List lessons of student.
     *
     * <p>When since is used, from and to cannot be used too.
     * From and to have to be used together.
     *
     * <p>Allowed only for admin and instructor.
     *
     * @param id id an unique id of student.
     * @param since optional date since.
     * @param from optional date from.
     * @param to optional date to.
     * @return list of lessons.
     */
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

    /**
     * Get lessons by invoking student.
     *
     * <p>"me" path is used because we can avoid processing user roles
     * inside controller methods.
     *
     * <p>Allowed only for student.
     *
     * @param auth object which contains principal.
     * @param since optional date since.
     * @param from optional date from.
     * @param to optional date to.
     * @return list of lessons.
     */
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

    /**
     * Get number of hours of finished lessons by student.
     *
     * <p>Can be accessed by student, instructor, admin.
     *
     * @param id unique id of student.
     * @return number of hours.
     */
    @GetMapping("/{id}/hours")
    public Double getStudentHours(@PathVariable String id) {
        return lessonService.getHoursCompletedByStudent(id);
    }
}
