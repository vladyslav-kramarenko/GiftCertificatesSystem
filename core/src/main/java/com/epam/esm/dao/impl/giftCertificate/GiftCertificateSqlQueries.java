package com.epam.esm.dao.impl.giftCertificate;

public final class GiftCertificateSqlQueries {
    public static final String CREATE_CERTIFICATE = """
            INSERT INTO gift_certificate(name, description, price, duration) VALUES (?, ?, ?, ?)""";
    public static final String GET_CERTIFICATE_WITH_TAGS_BY_ID = """
            SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date, t.id AS tag_id, t.name AS tag_name 
            FROM gift_certificate gc 
            LEFT JOIN gift_certificate_has_tag gct ON gc.id = gct.gift_certificate_id 
            LEFT JOIN tag t ON gct.tag_id = t.id 
            WHERE gc.id = ?""";
    public static final String GET_ALL_CERTIFICATES_WITH_TAGS = """
            SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date, t.id as tag_id, t.name as tag_name 
            FROM gift_certificate gc 
            LEFT JOIN gift_certificate_has_tag gct ON gc.id = gct.gift_certificate_id 
            LEFT JOIN tag t ON gct.tag_id = t.id""";

    public static final String GET_ALL_CERTIFICATES_WITH_TAGS_AND_NAME_OR_DESCRIPTION_SEARCH = """
            SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date, t.id as tag_id, t.name as tag_name 
            FROM gift_certificate gc 
            LEFT JOIN gift_certificate_has_tag gct ON gc.id = gct.gift_certificate_id 
            LEFT JOIN tag t ON gct.tag_id = t.id 
            WHERE gc.name LIKE ? OR gc.description LIKE ?""";

    public static final String DELETE_CERTIFICATE_BY_ID = """
            DELETE FROM gift_certificate WHERE id = ?""";
    public static final String UPDATE_CERTIFICATE = """
            UPDATE gift_certificate SET name = ?, description = ?, price = ?, duration = ? WHERE id = ?""";
    public static final String SELECT_CERTIFICATES_BY_TAG_ID = """
            SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date, t.id as tag_id, t.name as tag_name 
            FROM gift_certificate gc 
            LEFT JOIN gift_certificate_has_tag gct ON gc.id = gct.gift_certificate_id 
            LEFT JOIN tag t ON gct.tag_id = t.id 
            WHERE gct.tag_id = ?""";
}
