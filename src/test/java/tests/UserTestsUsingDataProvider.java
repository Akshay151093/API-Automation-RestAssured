package tests;

import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.*;
import models.User;
import utils.DataProviders;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Epic("PET-STORE API")
@Feature("USER API : DATA PROVIDER")
public class UserTestsUsingDataProvider extends BaseTest{

    private static final Map<String, User> testUsers = new ConcurrentHashMap<>();

    @Test(priority = 1, groups = {"user","smoke","regression"}, dataProvider = "payload", dataProviderClass = DataProviders.class)
    @Story("Create users")
    @Description("Verify that new users can be created successfully")
    void testCreateUsers(String id, String un, String fn,
                         String ln, String e, String pwd, String ph, String s) {
        User payload = new User();
        payload.setId(Integer.parseInt(id));
        payload.setUsername(un);
        payload.setFirstName(fn);
        payload.setLastName(ln);
        payload.setEmail(e);
        payload.setPassword(pwd);
        payload.setPhone(ph);
        payload.setUserStatus(Integer.parseInt(s));
        Response createResponse = UserClient.createUser(payload);
        userAssertions.verifyStatusCode(createResponse, 200);
        testUsers.put(payload.getUsername(), payload);
    }

    @Test (priority = 2, groups = {"user","regression"}, dependsOnMethods = "testCreateUsers", dataProvider = "usernames", dataProviderClass = DataProviders.class)
    @Story("Get users")
    @Description("Verify users can be fetch successfully")
    void testGetUsers(String un){
        User expectedUser = testUsers.get(un);
        if (expectedUser == null) {
            throw new IllegalStateException("No created user found for username: " + un);
        }
        Response getResponse = UserClient.getUser(un);
        userAssertions.verifyStatusCode(getResponse, 200);
        userAssertions.verifyUserSchema(getResponse);
        userAssertions.verifyUsername(getResponse, expectedUser.getUsername());
        userAssertions.verifyFirstName(getResponse, expectedUser.getFirstName());
        userAssertions.verifyLastName(getResponse, expectedUser.getLastName());
        userAssertions.verifyEmail(getResponse, expectedUser.getEmail());
        userAssertions.verifyPhone(getResponse, expectedUser.getPhone());
    }

    @Test (priority = 3, groups = {"user","regression"}, dependsOnMethods = "testGetUsers", dataProvider = "usernames", dataProviderClass = DataProviders.class)
    @Story("Delete users")
    @Description("Verify that users can be deleted successfully")
    void testDeleteUsers(String un){
        Response deleteResponse = UserClient.deleteUser(un);
        userAssertions.verifyStatusCode(deleteResponse,200);
        Response getResponse = UserClient.getUser(un);
        userAssertions.verifyStatusCode(getResponse,404);
        testUsers.remove(un);
    }
}
