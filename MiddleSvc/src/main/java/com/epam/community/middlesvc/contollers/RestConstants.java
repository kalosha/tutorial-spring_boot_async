package com.epam.community.middlesvc.contollers;

public final class RestConstants {
    private RestConstants() {
    }

    public static final String SWAGGER_UI = "/swagger-ui/index.html";
    public static final String API_DELIMITER = "/";
    public static final String API_VERSION = "v0.1";
    public static final String API_REST = "api";
    public static final String ACTUATOR = API_DELIMITER + "actuator";
    public static final String ROOT = API_DELIMITER + API_REST + API_DELIMITER + API_VERSION;
    public static final String HELPER_ENDPOINT = ROOT + API_DELIMITER + "helpers";
    public static final String CARS_ENDPOINT = ROOT + API_DELIMITER + "cars";
}
