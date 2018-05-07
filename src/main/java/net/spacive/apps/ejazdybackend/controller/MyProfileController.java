package net.spacive.apps.ejazdybackend.controller;

import net.spacive.apps.ejazdybackend.model.CognitoUser;
import net.spacive.apps.ejazdybackend.service.LessonService;
import net.spacive.apps.ejazdybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/profile")
public class MyProfileController {

    private final UserService userService;
    private final LessonService lessonService;

    @Autowired
    public MyProfileController(UserService userService, LessonService lessonService) {
        this.userService = userService;
        this.lessonService = lessonService;
    }

    @GetMapping
    public CognitoUser getMyProfile(Authentication auth) {
        CognitoUser fromToken = (CognitoUser) auth.getPrincipal();

        // fromToken might not contain all attributes
        // so we need to fetch them from user service
        return userService.getUser(fromToken.getId());
    }
}
