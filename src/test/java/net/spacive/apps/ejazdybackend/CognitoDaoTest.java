package net.spacive.apps.ejazdybackend;

import net.spacive.apps.ejazdybackend.dao.CognitoDao;
import net.spacive.apps.ejazdybackend.dao.DynamoDao;
import net.spacive.apps.ejazdybackend.model.entity.InstructorEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CognitoDaoTest {

    private static final Logger log = LoggerFactory.getLogger(DynamoDao.class.getName());

    @Autowired
    private CognitoDao cognitoDao;

    @Test
    public void test() {
        List<InstructorEntity> instructors = cognitoDao.getAllInstructors();
        instructors.forEach(i -> {
            log.info(i.getId() + " " + i.getEmail() + " " + i.getPhone());
        });
    }
}
