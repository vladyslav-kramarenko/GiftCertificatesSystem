package com.epam.esm.core.util;

public final class CoreConstants {
    public static final int MAX_GIFT_CERTIFICATE_NAME_LENGTH = 64;
    public static final int MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH = 255;
    public static final int MAX_USER_FIRST_NAME_LENGTH = 45;
    public static final int MAX_USER_LAST_NAME_LENGTH = 45;
    public static final int MAX_TAG_NAME_LENGTH = 64;

    public static final int ONE_MINUTE = 60 * 1000;
    public static final int ONE_HOUR = 60 * 60 * 1000;

    public static final int ONE_DAY = 24 * 60 * 60 * 1000;
    public static final String[] ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS = {"id", "name", "create_date", "update_date"};
    public static final String[] ALLOWED_TAG_SORT_FIELDS = {"name", "id"};
    public static final String[] ALLOWED_USER_SORT_FIELDS = {"first_name", "last_name", "id"};
    public static final String[] ALLOWED_ORDER_SORT_FIELDS = {"sum", "id"};
    public static final String[] ALLOWED_SORT_DIRECTIONS = {"asc", "desc"};
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String GIFT_CERTIFICATE_SERVICE_TOKEN_ISSUER = "https://gift-certificate-service.com";
    public static final String AUTH0_TOKEN_ISSUER = "https://dev-kramarenko.eu.auth0.com/";
    public static final String AUTH0_EMAIL_CLAIM = "https://gift-certificates-system-api/email";
    public static final String AUTH0_ROLE_CLAIM = "https://gift-certificates-system-api/roles";
    public static final String GIFT_CERTIFICATE_SERVICE_USER_ID_CLAIM = "user_id";
    public static final String GIFT_CERTIFICATE_SERVICE_ROLES_CLAIM = "roles";
    public static final String USER_ID_AUTHORITY_PREFIX = "USER_ID_";
    public static final String ROLE_AUTHORITY_PREFIX = "ROLE_";
}
