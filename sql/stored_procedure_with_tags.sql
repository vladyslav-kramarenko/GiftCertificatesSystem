DELIMITER $$

CREATE PROCEDURE search_gift_certificates_with_tags(
    IN search_term VARCHAR(255),
    IN sort_conditions VARCHAR(255),
    IN page_offset INT,
    IN page_limit INT,
    IN tags_filter VARCHAR(255)
)
BEGIN
    SET @search_term = search_term;
    SET @sort_conditions = sort_conditions;
    SET @page_offset = page_offset;
    SET @page_limit = page_limit;
    SET @tags_filter = REPLACE(tags_filter, ',', "','");

    SET @query = CONCAT('
        SELECT
            gc.id,
            gc.name,
            gc.description,
            gc.price,
            gc.duration,
            gc.create_date,
            gc.last_update_date,
			t.id AS tag_id,
            t.name AS tag_name
        FROM gift_certificate gc
        LEFT JOIN gift_certificate_has_tag gct ON gc.id = gct.gift_certificate_id
        LEFT JOIN tag t ON gct.tag_id = t.id
        WHERE (
            gc.name LIKE CONCAT("%", ?, "%") OR
            gc.description LIKE CONCAT("%", ?, "%")
        )
        AND gc.id IN (
            SELECT gct.gift_certificate_id
            FROM gift_certificate_has_tag gct
            JOIN tag t ON gct.tag_id = t.id',
                        IF(
                                    LENGTH(@tags_filter) = 0,
                                    '',
                                    CONCAT(' WHERE t.name IN (''', @tags_filter, ''')')
                            ),
                        ' GROUP BY gct.gift_certificate_id
                        HAVING
                            COUNT(t.id)
                            >=
                            LENGTH(?)-LENGTH(REPLACE(?, ",", ""))+1
                    )
                    ORDER BY ', @sort_conditions,
                        ' LIMIT ?, ?;'
        );

    PREPARE stmt FROM @query;
    EXECUTE stmt USING
        @search_term,
        @search_term,
        @tags_filter,
        @tags_filter,
        @page_offset,
        @page_limit;
    DEALLOCATE PREPARE stmt;
END$$

DELIMITER ;