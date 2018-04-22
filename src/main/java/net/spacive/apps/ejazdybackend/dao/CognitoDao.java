package net.spacive.apps.ejazdybackend.dao;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import net.spacive.apps.ejazdybackend.config.CognitoConfiguration;
import net.spacive.apps.ejazdybackend.model.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CognitoDao {

    private static final Logger log = LoggerFactory.getLogger(CognitoDao.class.getName());

    @Autowired
    private AWSCognitoIdentityProvider cognito;

    @Autowired
    private CognitoConfiguration config;

    public List<UserEntity> getAllInstructors() {

        ListUsersInGroupRequest request = new ListUsersInGroupRequest()
                .withGroupName("instructor")
                .withUserPoolId(config.getPoolId());

        List<UserType> users = cognito.listUsersInGroup(request).getUsers();
        List<UserEntity> instructors = new ArrayList<>(users.size());

        users.forEach(user -> {
            UserEntity userEntity = userTypeToUserEntity(user);
            userEntity.setUserType(UserEntity.UserType.INSTRUCTOR);
            instructors.add(userEntity);
        });

        return instructors;
    }

    public UserEntity inviteUser(String email) {

        final List<AttributeType> userAttributes = new ArrayList<>();
        userAttributes.add(
                new AttributeType()
                        .withName("email")
                        .withValue(email)
        );
        userAttributes.add(
                new AttributeType()
                        .withName("email_verified")
                        .withValue("True")
        );

        AdminCreateUserRequest request = new AdminCreateUserRequest()
                .withUserPoolId(config.getPoolId())
                .withUsername(email)
                .withUserAttributes(userAttributes)
                .withDesiredDeliveryMediums("EMAIL");

        AdminCreateUserResult result = cognito.adminCreateUser(request);

        if (result.getSdkHttpMetadata().getHttpStatusCode() == 200) {
            UserEntity userEntity = userTypeToUserEntity(result.getUser());
            return userEntity;
        }
        return null;
    }

    public void addUserToGroup(UserEntity userEntity, String group) {

        AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest()
                .withGroupName(group)
                .withUsername(userEntity.getId())
                .withUserPoolId(config.getPoolId());

        cognito.adminAddUserToGroup(request);
    }


    private UserEntity userTypeToUserEntity(UserType user) {
        final UserEntity userEntity = new UserEntity();

        // process attributes
        user.getAttributes().forEach(attr -> {
            String attrName = attr.getName();
            switch (attrName) {
                case "sub": {
                    userEntity.setId(attr.getValue());
                }
                break;
                case "email": {
                    userEntity.setEmail(attr.getValue());
                }
                break;
                case "phone_number": {
                    userEntity.setPhone(attr.getValue());
                }
                break;
            }
        });

        return userEntity;
    }
}
