DELIMITER $$

CREATE PROCEDURE search_gift_certificates_with_tags(IN search_term VARCHAR(255), IN sort_conditions VARCHAR(255),
                                                          IN page_offset INT, IN page_limit INT, IN tags_filter VARCHAR(255))
BEGIN
    SET @query = CONCAT('
                    SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date,
                    gc.last_update_date,
                    t.id as tag_id, t.name as tag_name
                    FROM gift_certificate gc
                    LEFT JOIN gift_certificate_has_tag gct ON gc.id = gct.gift_certificate_id
                    LEFT JOIN tag t ON gct.tag_id = t.id
                    WHERE (
                        "', search_term, '"="" OR
                        gc.name LIKE CONCAT("%", "', search_term, '", "%") OR
                        gc.description LIKE CONCAT("%", "', search_term, '", "%")
                        )
                    AND ("', tags_filter, '" = "" OR FIND_IN_SET(t.name, "', tags_filter, '") > 0)
                    ORDER BY ', sort_conditions, '
                    LIMIT ', page_offset, ', ', page_limit, ';
    ');


    PREPARE stmt FROM @query;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END$$

DELIMITER ;