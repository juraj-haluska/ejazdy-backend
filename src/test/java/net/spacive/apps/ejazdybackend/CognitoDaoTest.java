package net.spacive.apps.ejazdybackend;

import net.spacive.apps.ejazdybackend.dao.CognitoDao;
import net.spacive.apps.ejazdybackend.dao.DynamoDao;
import net.spacive.apps.ejazdybackend.model.entity.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CognitoDaoTest {

    private static final Logger log = LoggerFactory.getLogger(DynamoDao.class.getName());

    @Autowired
    private CognitoDao cognitoDao;

    @Test
    public void test() {
        UserEntity invitedUser = cognitoDao.inviteUser("shanelle.karthika@itis0k.com");

        log.info("invited user: " + invitedUser.getEmail());

        cognitoDao.addUserToGroup(invitedUser, "instructor");
    }
}
