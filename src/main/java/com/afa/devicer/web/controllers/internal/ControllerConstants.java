package com.afa.devicer.web.controllers.internal;

@SuppressWarnings({"PMD.DataClass"})
public final class ControllerConstants {

    public static final String ACTUATOR = "/actuator";
    public static final String API_DOCS = "/v3/api-docs";
    public static final String SWAGGER = "/swagger-ui";

    public static final String ROOT = "/";
    public static final String BASE_API = "/web";

    // WEB CONTROLLERS
    public static final String INDEX = BASE_API + "/index";
    public static final String PRODUCTS = BASE_API + "/products";
    public static final String CUSTOMERS = BASE_API + "/customers";
    public static final String ORDERS = BASE_API + "/orders";
    public static final String PERSON_SETTINGS = BASE_API + "/persons/settings";

    // REST CONTROLLERS
    public static final String REST_PRODUCTS = BASE_API + "/wiki/products";
    public static final String REST_DELIVERY = BASE_API + "/wiki/delivery";
    public static final String REST_INTEGRATIONS_CDEK = BASE_API + "/wiki/integrations/cdek";
    public static final String REST_INTEGRATIONS_UNION = BASE_API + "/wiki/integrations/union";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";


}
