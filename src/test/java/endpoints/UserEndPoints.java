package endpoints;

/* Created to perform CRUD operations for user module */

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;
import payload.User;
import utils.LogManagerUtil;

import static io.restassured.RestAssured.*;

public class UserEndPoints {

    private static final Logger logger = LogManagerUtil.getLogger(UserEndPoints.class);

    public static Response createUser(User payload){
        logger.info("Calling Create User API");
        logger.debug("Endpoint : {}", Routes.POST_URL);
        logger.debug("Request Payload : {}", payload.toString());
        Response createResponse = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)

        .when()
                .post(Routes.POST_URL);
        logger.info("Create User completed");
        logger.debug("Response Body : {}", createResponse.asPrettyString());
        return createResponse;
    }

    public static Response getUser (String name){
        logger.info("Calling Get User API");
        logger.debug("Endpoint : {}", Routes.GET_URL);
        logger.debug("Request Name : {}", name);
        Response getResponse = given()
                        .pathParam("username",name)
        .when()
                .get(Routes.GET_URL);
        logger.info("Get User completed");
        logger.debug("Response Body : {}", getResponse.asPrettyString());
        return getResponse;
    }

    public static Response updateUser (String name, User payload){
        logger.info("Calling Update User API");
        logger.debug("Endpoint : {}", Routes.PUT_URL);
        logger.debug("Request Payload : {}", payload);
        Response putResponse = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("username", name)
                .body(payload)

        .when()
                .put(Routes.PUT_URL);
        logger.info("Update User completed");
        logger.debug("Response Body : {}", putResponse.asPrettyString());
        return putResponse;
    }

    public static Response deleteUser (String name){
        logger.info("Calling Delete User API");
        logger.debug("Endpoint : {}", Routes.DELETE_URL);
        logger.debug("Request Name : {}", name);
        Response deleteResponse = given()
                .pathParam("username",name)
        .when()
                .delete(Routes.DELETE_URL);
        logger.info("Delete User completed");
        logger.debug("Response Body : {}", deleteResponse.asPrettyString());
        return deleteResponse;
    }
}
