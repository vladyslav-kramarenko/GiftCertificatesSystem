package com.epam.esm.dao.impl.tag;

public class TagSqlQueries {
    public static final String CREATE_TAG = "INSERT INTO tag(name) VALUES (?)";

    public static final String GET_TAG_BY_ID = "SELECT * FROM tag WHERE id = ?";

    public static final String GET_TAG_BY_NAME = "SELECT * FROM tag WHERE name = ?";

    public static final String GET_ALL_TAGS = "SELECT * FROM tag";

    public static final String DELETE_TAG_BY_ID = "DELETE FROM tag WHERE id = ?";
    public static final String ADD_TAG_TO_CERTIFICATE =
            "INSERT INTO gift_certificate_has_tag(gift_certificate_id, tag_id) VALUES (?, ?)";
    public static final String DELETE_CERTIFICATE_TAGS_BY_CERTIFICATE_ID =
            "DELETE FROM gift_certificate_has_tag WHERE gift_certificate_id = ?";
    public static final String COUNT_CERTIFICATE_TAGS_BY_CERTIFICATE_ID_AND_TAG_ID =
            "SELECT COUNT(*) FROM gift_certificate_has_tag WHERE gift_certificate_id = ? AND tag_id = ?";
}
