package tests;

import clients.UserClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.*;
import models.User;
import utils.UserDataBuilder;

@Epic("PET-STORE API")
@Feature("USER API : FAKER DATA")
public class UserTestsUsingFaker extends BaseTest{

    User payload;

    @BeforeClass
    public void setupData(){
        payload = UserDataBuilder.getRandomUserPayload();
    }

    @Test (priority = 1, groups = {"user","smoke","regression"})
    @Story("Create user")
    @Description("Verify that a new user can be created successfully")
    void testCreateUser() {
        Response createResponse = UserClient.createUser(payload);
        userAssertions.verifyStatusCode(createResponse,200);
    }

    @Test (priority = 2, groups = {"user","regression"}, dependsOnMethods = "testCreateUser")
    @Story("Get user by username")
    @Description("Verify user can be fetch successfully")
    void testGetUser(){
        Response getResponse = UserClient.getUser(payload.getUsername());
        userAssertions.verifyStatusCode(getResponse,200);
        userAssertions.verifyUserSchema(getResponse);
        userAssertions.verifyUsername(getResponse, payload.getUsername());
        userAssertions.verifyFirstName(getResponse, payload.getFirstName());
        userAssertions.verifyLastName(getResponse, payload.getLastName());
        userAssertions.verifyEmail(getResponse, payload.getEmail());
        userAssertions.verifyPhone(getResponse, payload.getPhone());
    }

    @Test (priority = 3, groups = {"user","regression"}, dependsOnMethods = "testCreateUser")
    @Story("Update user by username")
    @Description("Verify user can be updated successfully")
    void testUpdateUserByName() {
        UserDataBuilder.updateUserEmail(payload);
        UserDataBuilder.updateUserPhone(payload);
        Response updateResponse = UserClient.updateUser(payload.getUsername(), payload);
        userAssertions.verifyStatusCode(updateResponse,200);
        Response getResponse = UserClient.getUser(payload.getUsername());
        userAssertions.verifyEmail(getResponse, payload.getEmail());
        userAssertions.verifyPhone(getResponse, payload.getPhone());
    }

    @Test (priority = 4, groups = {"user","regression"}, dependsOnMethods = "testCreateUser")
    @Story("Delete user by username")
    @Description("Verify user can be deleted successfully")
    void testDeleteUserByName() {
        Response deleteResponse = UserClient.deleteUser(payload.getUsername());
        userAssertions.verifyStatusCode(deleteResponse,200);
        Response getResponse = UserClient.getUser(payload.getUsername());
        userAssertions.verifyStatusCode(getResponse,404);
    }
}