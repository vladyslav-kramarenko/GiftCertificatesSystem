package com.epam.esm.core.util;

public final class CoreConstants {
    public static final int MAX_GIFT_CERTIFICATE_NAME_LENGTH = 64;
    public static final int MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH = 255;
    public static final int MAX_USER_FIRST_NAME_LENGTH = 45;
    public static final int MAX_USER_LAST_NAME_LENGTH = 45;
    public static final int MAX_TAG_NAME_LENGTH = 64;
    public static final String[] ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS = {"id","name", "create_date", "update_date"};
    public static final String[] ALLOWED_TAG_SORT_FIELDS = {"name", "id"};
    public static final String[] ALLOWED_USER_SORT_FIELDS = {"first_name","last_name", "id"};
    public static final String[] ALLOWED_ORDER_SORT_FIELDS = {"sum", "id"};
    public static final String[] ALLOWED_SORT_DIRECTIONS = {"asc", "desc"};
}
