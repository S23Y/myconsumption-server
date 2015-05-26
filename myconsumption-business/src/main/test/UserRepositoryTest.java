import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.starfishrespect.myconsumption.server.Application;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

/**
 * Created by thibaud on 26.05.15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Before
    public void setUp() throws DaoException {
        User user = new User("yoda", "maytheforcebewithyou");

        repository.insertUser(user);
    }

    @Test
    public void canFetchYoda() {
        System.out.println(repository.getUser("yoda").toString());
    }
}
