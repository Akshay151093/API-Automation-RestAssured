package endpoints;

public class Routes {

    public static String base_url = "https://petstore.swagger.io/v2";

    /* User module */
    public static String POST_URL = base_url + "/user";
    public static String GET_URL = base_url + "/user/{username}";
    public static String PUT_URL = base_url + "/user/{username}";
    public static String DELETE_URL = base_url + "/user/{username}";
}
