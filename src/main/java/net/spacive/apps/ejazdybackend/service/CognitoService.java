package net.spacive.apps.ejazdybackend.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import net.spacive.apps.ejazdybackend.config.CognitoConfiguration;
import net.spacive.apps.ejazdybackend.model.CognitoUser;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CognitoService {

    private static final Logger log = LoggerFactory.getLogger(CognitoService.class.getName());

    private final AWSCognitoIdentityProvider cognito;
    private final CognitoConfiguration config;

    @Autowired
    public CognitoService(AWSCognitoIdentityProvider cognito, CognitoConfiguration config) {
        this.cognito = cognito;
        this.config = config;
    }

    public List<CognitoUser> getUsersInGroup(String userGroup) {

        ListUsersInGroupRequest request = new ListUsersInGroupRequest()
                .withGroupName(userGroup)
                .withUserPoolId(config.getPoolId());

        List<UserType> users = cognito.listUsersInGroup(request).getUsers();
        List<CognitoUser> cognitoUsers = new ArrayList<>(users.size());

        users.forEach(user -> {
            CognitoUser cognitoUser = userTypeToCognitoUser(user, userGroup);
            cognitoUsers.add(cognitoUser);
        });

        return cognitoUsers;
    }

    public CognitoUser inviteUser(String email) {

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
        return userTypeToCognitoUser(result.getUser(), null);
    }

    public CognitoUser deleteUser(CognitoUser user) {
        AdminDeleteUserRequest request = new AdminDeleteUserRequest()
                .withUsername(user.getEmail())
                .withUserPoolId(config.getPoolId());

        AdminDeleteUserResult result = cognito.adminDeleteUser(request);
        if (result.getSdkHttpMetadata().getHttpStatusCode() == HttpStatus.SC_OK) {
            return user;
        } else {
            return null;
        }
    }

    public CognitoUser addUserToGroup(CognitoUser cognitoUser, String group) {

        AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest()
                .withGroupName(group)
                .withUsername(cognitoUser.getId())
                .withUserPoolId(config.getPoolId());

        cognito.adminAddUserToGroup(request);

        return new CognitoUser.Builder()
                .withId(cognitoUser.getId())
                .withEmail(cognitoUser.getEmail())
                .withPhone(cognitoUser.getPhone())
                .withUserGroup(group)
                .withLastModifiedDate(cognitoUser.getLastModifiedDate())
                .withCreateDate(cognitoUser.getCreateDate())
                .withLastName(cognitoUser.getLastName())
                .withFirstName(cognitoUser.getFirstName())
                .withStatus(cognitoUser.getStatus())
                .build();
    }

    private CognitoUser userTypeToCognitoUser(UserType user, String userGroup) {
        final CognitoUser.Builder builder = new CognitoUser.Builder();

        // process attributes
        user.getAttributes().forEach(attr -> {
            String attrName = attr.getName();
            switch (attrName) {
                case "sub": {
                    builder.withId(attr.getValue());
                }
                break;
                case "email": {
                    builder.withEmail(attr.getValue());
                }
                break;
                case "phone_number": {
                    builder.withPhone(attr.getValue());
                }
                break;
                case "given_name": {
                    builder.withFirstName(attr.getValue());
                }
                break;
                case "family_name": {
                    builder.withLastName(attr.getValue());
                }
                break;
                default: {
                    log.info("ignoring cognito user attribute: " + attrName);
                }
            }
        });

        builder.withUserGroup(userGroup);
        builder.withStatus(user.getUserStatus());
        builder.withCreateDate(user.getUserCreateDate());
        builder.withLastModifiedDate(user.getUserLastModifiedDate());

        return builder.build();
    }

    public CognitoUser getUser(String uuid) {
        ListUsersRequest request = new ListUsersRequest()
                .withFilter(
                        String.format("sub = \"%s\"", uuid)
                )
                .withLimit(1)
                .withUserPoolId(config.getPoolId());

        List<UserType> userTypes = cognito.listUsers(request).getUsers();

        if (userTypes != null && userTypes.size() > 0) {
            UserType user = userTypes.get(0);
            return userTypeToCognitoUser(user, null);
        }

        return null;
    }
}
