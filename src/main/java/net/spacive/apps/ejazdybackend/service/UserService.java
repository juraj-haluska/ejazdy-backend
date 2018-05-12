package net.spacive.apps.ejazdybackend.service;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class is an implementation of user service
 * responsible for user handling.
 *
 * <p>It is use case specific for driving school. It
 * manipulates two types of users - instructors and students.
 *
 * @author  Juraj Haluska
 */
@Service
public class UserService {
    /**
     * Reference to CognitoService.
     */
    private final CognitoService cognitoService;

    /**
     * Reference to LessonService.
     */
    private final LessonService lessonService;

    /**
     * UserService constructor
     *
     * @param cognitoService injected CognitoService.
     * @param lessonService injected LessonService.
     */
    @Autowired
    public UserService(CognitoService cognitoService, LessonService lessonService) {
        this.cognitoService = cognitoService;
        this.lessonService = lessonService;
    }

    /**
     * Calling this method will cause sending an email
     * invitation to the new student.
     *
     * @param email mail of the new student.
     * @return an instance of the newly created student.
     */
    public CognitoUser inviteNewStudentByEmail(String email) {
        CognitoUser invitedUser = cognitoService.inviteUser(email);
        return cognitoService.addUserToGroup(
                invitedUser,
                "student"
        );
    }

    /**
     * Calling this method will cause sending an email
     * invitation to the new instructor.
     *
     * @param email mail of the new instructor.
     * @return an instance of the newly created instructor.
     */
    public CognitoUser inviteNewInstructorByEmail(String email) {
        CognitoUser invitedUser = cognitoService.inviteUser(email);
        return cognitoService.addUserToGroup(
                invitedUser,
                "instructor"
        );
    }

    /**
     * Permanently will delete a student from the system.
     *
     * @param uuid an unique id of the student.
     * @return an instance of deleted student.
     */
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

    /**
     * Permanently will delete an instructor from the system.
     *
     * @param uuid an unique id of the instructor.
     * @return an instance of deleted instructor.
     */
    public CognitoUser deleteInstructor(String uuid) {

        // delete all lessons of this instructor
        List<Lesson> lessons = lessonService.getLessonsByInstructor(uuid);
        lessons.forEach(l -> lessonService.deleteLesson(uuid, l.getStartTime()));

        CognitoUser user = cognitoService.getUser(uuid);

        return cognitoService.deleteUser(user);
    }

    /**
     * List all instructors in the system.
     *
     * @return list of all instructors.
     */
    public List<CognitoUser> getAllInstructors() {
        return cognitoService.getUsersInGroup("instructor");
    }

    /**
     * List all students in the system.
     *
     * @return list of all students.
     */
    public List<CognitoUser> getAllStudents() {
        return cognitoService.getUsersInGroup("student");
    }

    /**
     * Get details of specified user -
     * instructor or student, depends on id.
     *
     * @param uuid an unique user id.
     * @return an instance of the user.
     */
    public CognitoUser getUser(String uuid) {
        return cognitoService.getUser(uuid);
    }
}
