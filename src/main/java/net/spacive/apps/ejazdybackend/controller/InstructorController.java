package net.spacive.apps.ejazdybackend.controller;

import net.spacive.apps.ejazdybackend.config.Utils;
import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import net.spacive.apps.ejazdybackend.service.LessonService;
import net.spacive.apps.ejazdybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * REST API for instructors resource.
 *
 * @author  Juraj Haluska
 */
@RestController
@CrossOrigin
@RequestMapping("/instructors")
public class InstructorController {

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
    public InstructorController(UserService userService, LessonService lessonService) {
        this.userService = userService;
        this.lessonService = lessonService;
    }

    /**
     * List all instructors.
     *
     * <p>Allowed only every role.
     *
     * @return list of instructors.
     */
    @GetMapping
    public List<CognitoUser> getAllInstructors() {
        return userService.getAllInstructors();
    }

    /**
     * Invite new instructor by email.
     *
     * <p>Allowed only for admin.
     *
     * @param email email of student to invite
     * @return new user instance.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CognitoUser inviteInstructor(@RequestParam("email") String email) {
        return userService.inviteNewInstructorByEmail(email);
    }

    /**
     * Delete instructor.
     *
     * <p>Allowed only for admin.
     *
     * @param id an unique id of instructor.
     * @return instance of deleted instructor.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CognitoUser deleteInstructor(@PathVariable String id) {
        return userService.deleteInstructor(id);
    }

    /**
     * Get single instructor by UUID.
     *
     * <p>Allowed for every role.
     *
     * @param id an unique id of instructor.
     * @return instance of instructor.
     */
    @GetMapping("/{id}")
    public CognitoUser getInstructor(@PathVariable String id) {
        return userService.getUser(id);
    }

    /**
     * List lessons of instructor.
     *
     * <p>When since is used, from and to cannot be used too.
     * From and to have to be used together.
     *
     * <p>Allowed for every role.
     *
     * @param id id an unique id of instructor.
     * @param since optional date since.
     * @param from optional date from.
     * @param to optional date to.
     * @return list of lessons.
     */
    @GetMapping("/{id}/lessons")
    public List<Lesson> getInstructorsLessons(
            @PathVariable String id,
            @RequestParam("since") Optional<String> since,
            @RequestParam("from") Optional<String> from,
            @RequestParam("to") Optional<String> to) {

        if (since.isPresent()) {
            Calendar sinceCal = Utils.parseISOString(since.get());
            return lessonService.getLessonsByInstructorSince(
                    id,
                    sinceCal
            );
        } else if(from.isPresent() && to.isPresent()) {
            Calendar fromCal = Utils.parseISOString(from.get());
            Calendar toCal = Utils.parseISOString(to.get());
            return lessonService.getLessonsByInstructorRange(
                    id,
                    fromCal,
                    toCal
            );
        } else {
            return lessonService.getLessonsByInstructor(id);
        }
    }

    /**
     * Create new lesson.
     *
     * <p>Instructors are allowed to create lessons only for themselves.
     *
     * <p>Accessible only by instructor.
     *
     * @param id id of instructor.
     * @param lesson lesson to create.
     * @param auth security object containing principal.
     * @return new lesson.
     * @throws Exception if id in lesson is not same as id of calling instructor.
     */
    @PostMapping("/{id}/lessons")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public Lesson createLesson(
            @PathVariable String id,
            @RequestBody Lesson lesson,
            Authentication auth) throws Exception {

        // check if instructor's id in token is same as id in path
        CognitoUser instructor = (CognitoUser) auth.getPrincipal();
        instructor = userService.getUser(instructor.getId());

        if (instructor.getId().equals(id)) {
            return lessonService.createLessonByInstructor(instructor, lesson);
        } else {
            throw new Exception("instructor id must be same as instructor id in token");
        }
    }

    /**
     * Delete lesson.
     *
     * <p>Admin without restrictions, instructor may
     * delete only his own lessons.
     *
     * @param id unique id of instructor.
     * @param startTime unique start time for specified instructor.
     * @param auth security object which contains principal.
     * @return deleted lesson.
     * @throws Exception if instructors id is wrong.
     */
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

        return lessonService.deleteLesson(id, Utils.parseISOString(startTime));
    }

    /**
     * Register student to lesson.
     *
     * <p>Admin - no restrictons, instructor only to himself.
     *
     * @param id of instructor in lesson.
     * @param startTime start time of lesson.
     * @param studentId student who should be registered.
     * @param auth security object which contains principal.
     * @return updated lesson instance.
     * @throws Exception if instructor id is wrong.
     */
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
                Utils.parseISOString(startTime)
        );
    }

    /**
     * Register calling student to lesson.
     *
     * <p>Id of calling student is extracted from jwt.
     *
     * <p>Allowed only to student.
     *
     * @param id id of instructor.
     * @param startTime start time.
     * @param auth security object which contains principal.
     * @return updated lesson.
     * @throws Exception if deleting wrong lesson.
     */
    @PostMapping("/{id}/lessons/{startTime}/student/me")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public Lesson addInvokingStudentToLesson(
            @PathVariable String id,
            @PathVariable String startTime,
            Authentication auth) throws Exception {

        CognitoUser student = (CognitoUser) auth.getPrincipal();
        student = userService.getUser(student.getId());

        return lessonService.registerStudentToLesson(
                student,
                id,
                Utils.parseISOString(startTime)
        );
    }

    /**
     * Delete student from lesson.
     *
     * <p>Admin - no restrictions, instructor - only his student.
     * @param id id of instructor.
     * @param startTime starting time of lesson.
     * @param studentId id of student to be removed.
     * @param auth security object which contains principal.
     * @return updated lesson.
     * @throws Exception wrong instructor id.
     */
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
                Utils.parseISOString(startTime),
                true
        );
    }

    /**
     * Delete invoking student from lesson.
     *
     * @param id id of instructor.s
     * @param startTime start time.
     * @param auth security object which contains principal.
     * @return updated lesson.
     * @throws Exception if deleting wrong lesson.
     */
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
                Utils.parseISOString(startTime),
                false
        );
    }
}
