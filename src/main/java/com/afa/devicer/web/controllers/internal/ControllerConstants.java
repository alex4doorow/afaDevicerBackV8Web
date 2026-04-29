package com.afa.devicer.web.controllers.internal;

@SuppressWarnings({"PMD.DataClass"})
public final class ControllerConstants {

    public static final String ACTUATOR = "/actuator";
    public static final String API_DOCS = "/v3/api-docs";
    public static final String SWAGGER = "/swagger-ui";

    public static final String ROOT = "/";
    public static final String BASE_API = "/web";

    public static final String INDEX = BASE_API + "/index";
    public static final String CUSTOMERS = BASE_API + "/customers";
    public static final String ORDERS = BASE_API + "/orders";
    public static final String PERSON_SETTINGS = BASE_API + "/persons/settings";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
}
