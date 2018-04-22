package net.spacive.apps.ejazdybackend.dao;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidp.model.UserType;
import net.spacive.apps.ejazdybackend.config.CognitoConfiguration;
import net.spacive.apps.ejazdybackend.model.entity.InstructorEntity;
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

    public List<InstructorEntity> getAllInstructors() {

        ListUsersInGroupRequest request = new ListUsersInGroupRequest()
                .withGroupName("instructor")
                .withUserPoolId(config.getPoolId());

        List<UserType> users = cognito.listUsersInGroup(request).getUsers();
        List<InstructorEntity> instructors = new ArrayList<>(users.size());

        // map UserType to InstructorEntity
        users.forEach(user -> {
            final InstructorEntity instructor = new InstructorEntity();

            // process attributes
            user.getAttributes().forEach(attr -> {
                String attrName = attr.getName();
                switch (attrName) {
                    case "sub": {
                        instructor.setId(attr.getValue());
                    }
                    break;
                    case "email": {
                        instructor.setEmail(attr.getValue());
                    } break;
                    case "phone_number": {
                        instructor.setPhone(attr.getValue());
                    } break;
                }
            });

            instructors.add(instructor);
        });

        return instructors;
    }
}
