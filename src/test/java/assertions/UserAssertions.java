package assertions;

import endpoints.UserEndPoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import utils.LogManagerUtil;

public class UserAssertions {

    private static final Logger logger = LogManagerUtil.getLogger(UserEndPoints.class);

    @Step("Verify response status code is {expectedStatusCode}")
    public void verifyStatusCode(Response response, int expectedStatusCode) {
        logger.debug("Verifying Status Code. Expected={}", expectedStatusCode);
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Status code verification failed");
        logger.info("Status Code validation successful");
    }

    @Step("Verify username is '{expectedUsername}'")
    public void verifyUsername(Response response, String expectedUsername) {
        logger.debug("Verifying Username. Expected={}", expectedUsername);
        Assert.assertEquals(
                response.jsonPath().getString("username"),
                expectedUsername,
                "Username verification failed"
        );
        logger.info("Username validation successful");
    }

    @Step("Verify first name is '{expectedFirstName}'")
    public void verifyFirstName(Response response, String expectedFirstName) {
        logger.debug("Verifying Firstname. Expected={}", expectedFirstName);
        Assert.assertEquals(
                response.jsonPath().getString("firstName"),
                expectedFirstName,
                "First name verification failed"
        );
        logger.info("Firstname validation successful");
    }

    @Step("Verify last name is '{expectedLastName}'")
    public void verifyLastName(Response response, String expectedLastName) {
        logger.debug("Verifying Lastname. Expected={}", expectedLastName);
        Assert.assertEquals(
                response.jsonPath().getString("lastName"),
                expectedLastName,
                "Last name verification failed"
        );
        logger.info("Lastname validation successful");
    }

    @Step("Verify email is '{expectedEmail}'")
    public void verifyEmail(Response response, String expectedEmail) {
        logger.debug("Verifying Email. Expected={}", expectedEmail);
        Assert.assertEquals(
                response.jsonPath().getString("email"),
                expectedEmail,
                "Email verification failed"
        );
        logger.info("Email validation successful");
    }

    @Step("Verify phone number is '{expectedPhone}'")
    public void verifyPhone(Response response, String expectedPhone) {
        logger.debug("Verifying Phone. Expected={}", expectedPhone);
        Assert.assertEquals(
                response.jsonPath().getString("phone"),
                expectedPhone,
                "Phone verification failed"
        );
        logger.info("Phone validation successful");
    }

    @Step("Verify user status is '{expectedStatus}'")
    public void verifyUserStatus(Response response, int expectedStatus) {
        logger.debug("Verifying Status. Expected={}", expectedStatus);
        Assert.assertEquals(
                response.jsonPath().getInt("userStatus"),
                expectedStatus,
                "User status verification failed"
        );
        logger.info("Status validation successful");
    }

    @Step("Verify user ID is '{expectedId}'")
    public void verifyUserId(Response response, int expectedId) {
        logger.debug("Verifying Id. Expected={}", expectedId);
        Assert.assertEquals(
                response.jsonPath().getInt("id"),
                expectedId,
                "User ID verification failed"
        );
        logger.info("Id validation successful");
    }
}