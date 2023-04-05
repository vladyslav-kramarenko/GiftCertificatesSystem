package com.epam.esm.dao.impl.giftCertificate;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GiftCertificateRowMapper implements RowMapper<GiftCertificate> {

    @Override
    public GiftCertificate mapRow(ResultSet rs, int rowNum) throws SQLException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(rs.getLong("id"));
        certificate.setName(rs.getString("name"));
        certificate.setDescription(rs.getString("description"));
        certificate.setPrice(rs.getBigDecimal("price"));
        certificate.setDuration(rs.getInt("duration"));
        certificate.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
        certificate.setLastUpdateDate(rs.getTimestamp("last_update_date").toLocalDateTime());

        List<Tag> tags = new ArrayList<>();

        do {
            Tag tag = new Tag();
            tag.setId(rs.getLong("tag_id"));
            tag.setName(rs.getString("tag_name"));
            tags.add(tag);
        } while (rs.next() && rs.getLong("id") == certificate.getId());

        certificate.setTags(tags);
        return certificate;
    }
}