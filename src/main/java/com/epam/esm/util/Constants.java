package com.epam.esm.util;

public final class Constants {
    public static final int MAX_GIFT_CERTIFICATE_NAME_LENGTH = 64;
    public static final int MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH = 255;
    public static final int MAX_TAG_NAME_LENGTH = 64;
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT = "name,asc";
    public static final String[] ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS = {"name", "create_date", "update_date"};
    public static final String[] ALLOWED_TAG_SORT_FIELDS = {"name", "id"};
    public static final String[] ALLOWED_SORT_DIRECTIONS = {"asc", "desc"};
}
