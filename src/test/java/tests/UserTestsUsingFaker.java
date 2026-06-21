package tests;

import assertions.UserAssertions;
import endpoints.UserEndPoints;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.*;
import payload.User;
import utils.UserDataBuilder;

@Epic("PET-STORE API")
@Feature("USER API : FAKER DATA")
public class UserTestsUsingFaker {

    User payload;
    UserAssertions userAssertions;

    public UserTestsUsingFaker(){
        userAssertions = new UserAssertions();
    }

    @BeforeClass
    public void setupData(){
        payload = UserDataBuilder.getRandomUserPayload();
    }

    @Test (priority = 1)
    @Story("Create user")
    @Description("Verify that a new user can be created successfully")
    void testCreateUser() {
        Response createResponse = UserEndPoints.createUser(payload);
        userAssertions.verifyStatusCode(createResponse,200);
    }

    @Test (priority = 2, dependsOnMethods = "testCreateUser")
    @Story("Get user by username")
    @Description("Verify user can be fetch successfully")
    void testGetUser(){
        Response getResponse = UserEndPoints.getUser(payload.getUsername());
        userAssertions.verifyStatusCode(getResponse,200);
        userAssertions.verifyUsername(getResponse, payload.getUsername());
        userAssertions.verifyFirstName(getResponse, payload.getEmail());
        userAssertions.verifyLastName(getResponse, payload.getPhone());
    }

    @Test (priority = 3, dependsOnMethods = "testCreateUser")
    @Story("Update user by username")
    @Description("Verify user can be updated successfully")
    void testUpdateUserByName() {
        UserDataBuilder.updateUserEmail(payload);
        UserDataBuilder.updateUserPhone(payload);
        Response updateResponse = UserEndPoints.updateUser(payload.getUsername(), payload);
        userAssertions.verifyStatusCode(updateResponse,200);
        userAssertions.verifyStatusCode(updateResponse,200);
        Response getResponse = UserEndPoints.getUser(payload.getUsername());
        userAssertions.verifyEmail(getResponse, payload.getEmail());
        userAssertions.verifyPhone(getResponse, payload.getPhone());
    }

    @Test (priority = 4, dependsOnMethods = "testCreateUser")
    @Story("Delete user by username")
    @Description("Verify user can be deleted successfully")
    void testDeleteUserByName() {
        Response deleteResponse = UserEndPoints.deleteUser(payload.getUsername());
        userAssertions.verifyStatusCode(deleteResponse,200);
        Response getResponse = UserEndPoints.getUser(payload.getUsername());
        userAssertions.verifyStatusCode(getResponse,404);
    }
}