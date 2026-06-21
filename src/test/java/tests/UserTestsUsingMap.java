package tests;

import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import models.User;
import utils.UserDataBuilder;

@Epic("PET-STORE API")
@Feature("USER API : MAP")
public class UserTestsUsingMap extends BaseTest{

    @Test(priority = 1, groups = {"user","smoke","regression"})
    @Story("Create user")
    @Description("Verify that a new user can be created successfully")
    void testCreateUser1() {
        User payload = UserDataBuilder.getTestDataUserPayload("User_1");
        Response createResponse = UserClient.createUser(payload);
        userAssertions.verifyStatusCode(createResponse, 200);
    }

    @Test (priority = 2, groups = {"user","regression"}, dependsOnMethods = "testCreateUser1")
    @Story("Get user by username")
    @Description("Verify user can be fetch successfully")
    void testGetUser1(){
        User payload = UserDataBuilder.getTestDataUserPayload("User_1");
        Response getResponse = UserClient.getUser(payload.getUsername());
        userAssertions.verifyStatusCode(getResponse,200);
        userAssertions.verifyUserSchema(getResponse);
        userAssertions.verifyUsername(getResponse, payload.getUsername());
        userAssertions.verifyFirstName(getResponse, payload.getFirstName());
        userAssertions.verifyLastName(getResponse, payload.getLastName());
        userAssertions.verifyEmail(getResponse, payload.getEmail());
        userAssertions.verifyPhone(getResponse, payload.getPhone());
    }

    @Test(priority = 3, groups = {"user","smoke"})
    @Story("Create user")
    @Description("Verify that a new user can be created successfully")
    void testCreateUser2() {
        User payload = UserDataBuilder.getTestDataUserPayload("User_2");
        Response createResponse = UserClient.createUser(payload);
        userAssertions.verifyStatusCode(createResponse, 200);
    }

    @Test (priority = 4, groups = {"user","regression"}, dependsOnMethods = "testCreateUser2")
    @Story("Get user by username")
    @Description("Verify user can be fetch successfully")
    void testGetUser2(){
        User payload = UserDataBuilder.getTestDataUserPayload("User_2");
        Response getResponse = UserClient.getUser(payload.getUsername());
        userAssertions.verifyStatusCode(getResponse,200);
        userAssertions.verifyUserSchema(getResponse);
        userAssertions.verifyUsername(getResponse, payload.getUsername());
        userAssertions.verifyFirstName(getResponse, payload.getFirstName());
        userAssertions.verifyLastName(getResponse, payload.getLastName());
        userAssertions.verifyEmail(getResponse, payload.getEmail());
        userAssertions.verifyPhone(getResponse, payload.getPhone());
    }
}
