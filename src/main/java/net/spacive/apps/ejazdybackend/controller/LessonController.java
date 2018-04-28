package net.spacive.apps.ejazdybackend.controller;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import net.spacive.apps.ejazdybackend.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/lesson")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    // create lesson - instructors are allowed to create lessons only for themselves
    // maybe instructor id shouldn't be required in Lesson at this stage
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PostMapping
    public Lesson addLesson(Authentication auth, @RequestBody Lesson lesson) throws Exception {
        CognitoUser instructor = (CognitoUser) auth.getPrincipal();
        return lessonService.createLessonByInstructor(
                instructor,
                lesson.getStartTime(),
                lesson.getStopTime()
        );
    }

    // delete lesson - admin without restrictions, instructor may
    // delete only it's own lessons
    // think about instructor id in lesson again
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @DeleteMapping
    public Lesson deleteLesson(Authentication auth, @RequestBody Lesson lesson) throws Exception {
        boolean isAdmin = auth.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        if (!isAdmin) {
            CognitoUser instructor = (CognitoUser) auth.getPrincipal();
            if (!instructor.getId().equals(lesson.getInstructorId())) {
                throw new Exception("wrong instructor id");
            }
        }

        return lessonService.deleteLesson(
                lesson
        );
    }

    // register student to lesson - admin and instructor without restrictions
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @PutMapping("student/{id}")
    public Lesson registerStudentToLesson(
            @PathVariable String id,
            @RequestBody Lesson lesson) throws Exception {
        CognitoUser student = new CognitoUser.Builder()
                .withId(id)
                .build();

        return lessonService.registerStudentToLesson(
                student,
                lesson
        );
    }

    // unregister student from lesson - admin and instructor without restrictions
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @PutMapping("/student")
    public Lesson unregisterStudentFromLesson(@RequestBody Lesson lesson) throws Exception {
        return lessonService.forceUnregisterStudentFromLesson(
                lesson
        );
    }

    // get all lessons of instructor - available for everyone in system
    @GetMapping("/instructor/{id}")
    public List<Lesson> getLessonsByInstructor(@PathVariable String id) {
        return lessonService.getLessonsByInstructor(
                new CognitoUser.Builder()
                .withId(id)
                .build()
        );
    }

    // get all lessons of student
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @GetMapping("/student/{id}")
    public List<Lesson> getLessonsByStudent(@PathVariable String id) {
        return lessonService.getLessonsByStudent(
                new CognitoUser.Builder()
                        .withId(id)
                        .build()
        );
    }

    // get all lessons of student by calling student
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/student/me")
    public List<Lesson> getLessonsByCallingStudent(Authentication auth) {
        CognitoUser me = (CognitoUser) auth.getPrincipal();
        return lessonService.getLessonsByStudent(me);
    }

    // register/unregister student to/from lesson - calling student
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PutMapping("/student/me")
    public Lesson registerCallingStudentToLesson (
            Authentication auth,
            @RequestBody Lesson lesson
    ) throws Exception {
        CognitoUser me = (CognitoUser) auth.getPrincipal();

        if (lesson.getStudentId() == null || lesson.getStudentId().length() == 0) {
            return lessonService.unregisterStudentFromLesson(me.getId(), lesson);
        } else {
            return lessonService.registerStudentToLesson(
                    me,
                    lesson
            );
        }
    }
}
