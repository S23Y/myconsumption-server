import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.starfishrespect.myconsumption.server.business.Application;
import org.starfishrespect.myconsumption.server.business.controllers.ConfigController;

/**
 * Created by thibaud on 25.05.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class ConfigControllerTest {

    @Autowired
    WebApplicationContext context;

    @InjectMocks
    ConfigController controller;

    private MockMvc mvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    public void canAccessConfigNoAuth() throws Exception {
        // @formatter:off
        mvc.perform(get("/configs/co2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        // @formatter:on
    }
}
