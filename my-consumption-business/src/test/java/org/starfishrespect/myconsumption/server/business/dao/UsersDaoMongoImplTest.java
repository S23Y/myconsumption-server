package org.starfishrespect.myconsumption.server.business.dao;

/**
 * Created by Patrick Herbeuval on 25/03/14.
 */
public class UsersDaoMongoImplTest {

    /*private UsersDaoMongoImpl userMongo;

    private static final String collectionName = "userstest";

    @Before
    public void setUp() throws Exception {
        AbstractApplicationContext ctx = new GenericXmlApplicationContext("org/starfishrespect/server/server/business/server-business.xml");
        userMongo = (UsersDaoMongoImpl) ctx.getBean("usersDao");
        userMongo.setCollectionName(collectionName);
        userMongo.init();
        userMongo.reset();
        for (int i = 0; i < 10; i++) {
            User user = new User("user_" + i);
            for (int j = 0; j < 10; j++) {
                user.addSensor(new FluksoSensor("sensor_" + i + "_" + j, "sensor_" + i + "_" + j, "token"));
            }
            user.setPassword("password");
            userMongo.insertUser(user);
        }
    }

    @Test
    public void testGetUser() throws Exception {
        Assert.assertNotNull(userMongo.getUser("user_0"));
        Assert.assertNull(userMongo.getUser("user_fake"));
    }

    @Test
    public void testInsertUser() throws Exception {
        User user = new User("user_42");
        user.setPassword("passwd");
        Assert.assertTrue(userMongo.insertUser(user));
        Assert.assertFalse(userMongo.insertUser(user));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User("user_9");
        user.setPassword("passwd");
        Assert.assertTrue(userMongo.updateUser(user));
        user = new User("user_fake");
        user.setPassword("passwd");
        Assert.assertFalse(userMongo.updateUser(user));
    }

    @Test (expected = UserException.class)
    public void testInsertUser2() throws Exception {
        User user = new User("444user_43");
        userMongo.insertUser(user);
    }

    @Test
    public void testUserExists() throws Exception {
        Assert.assertTrue(userMongo.userExists("user_2"));
        Assert.assertFalse(userMongo.userExists("marcel"));
    }

    @Test
    public void deleteSensorTest() throws Exception {
        Assert.assertTrue(userMongo.deleteUser("user_3"));
        Assert.assertFalse(userMongo.deleteUser("user_3"));
    }

    @Test
    public void testHasSensor() throws Exception {
        Assert.assertTrue(userMongo.hasSensor("user_4", "sensor_4_4"));
        Assert.assertFalse(userMongo.hasSensor("user_4", "sensor_3_4"));
        Assert.assertFalse(userMongo.hasSensor("user_4noexist", "sensor_3_4"));
    }

    @Test
    public void testSensorExists() throws Exception {
        Assert.assertTrue(userMongo.sensorExists("sensor_4_4"));
        Assert.assertFalse(userMongo.sensorExists("sensor_noexists"));
    }*/

}
