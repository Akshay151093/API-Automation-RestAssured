package utils;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import payload.User;

import java.util.LinkedHashMap;

public class UserDataBuilder {

    private static final Logger logger = LogManagerUtil.getLogger(UserDataBuilder.class);
    private static final Faker faker = new Faker();
    private static final TestDataManager testDataManager = new TestDataManager();

    @Step("Create user payload all data")
    public static User getRandomUserPayload() {
        logger.info("Generating random user payload.");
        User user = new User();
        user.setId(faker.idNumber().hashCode());
        user.setUsername(faker.name().username());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().safeEmailAddress());
        user.setPassword(faker.internet().password());
        user.setPhone(faker.phoneNumber().cellPhone());
        user.setUserStatus(0);
        logger.debug("Random user payload generated for username '{}'.", user.getUsername());
        return user;
    }

    @Step("Update user payload with new email")
    public static void updateUserEmail(User user) {
        logger.info("Updating email for user '{}'.", user.getUsername());
        user.setEmail(faker.internet().safeEmailAddress());
        logger.debug("Email updated successfully.");
    }

    @Step("Update user payload with new phone")
    public static void updateUserPhone(User user) {
        logger.info("Updating phone for user '{}'.", user.getUsername());
        user.setPhone(faker.phoneNumber().cellPhone());
        logger.debug("Phone updated successfully.");
    }

    public static User getTestDataUserPayload(String testDataLabel){
        logger.info("Building user payload using test data '{}'.", testDataLabel);
        LinkedHashMap<String, String> testData = testDataManager.
                getTestData(testDataLabel, "TestData_Users", "Input_1");
        logger.debug("Loaded {} test data fields.", testData.size());
        User user = new User();
        user.setId(Integer.parseInt(testData.get("ID")));
        user.setUsername(testData.get("UserName"));
        user.setFirstName(testData.get("FirstName"));
        user.setLastName(testData.get("LastName"));
        user.setEmail(testData.get("Email"));
        user.setPassword(testData.get("Password"));
        user.setPhone(testData.get("Phone"));
        user.setUserStatus(Integer.parseInt(testData.get("Status")));
        logger.info("User payload created successfully from '{}'.", testDataLabel);
        return user;
    }
}
