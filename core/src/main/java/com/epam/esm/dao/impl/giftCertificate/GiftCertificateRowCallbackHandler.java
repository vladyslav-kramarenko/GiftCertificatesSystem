package com.epam.esm.dao.impl.giftCertificate;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GiftCertificateRowCallbackHandler implements RowCallbackHandler {
    private Map<Long, GiftCertificate> certificatesMap = new LinkedHashMap<>();

    @Override
    public void processRow(ResultSet rs) throws SQLException {

        long id = rs.getLong("id");

        GiftCertificate certificate = certificatesMap.get(id);
        if (certificate == null) {
            certificate = new GiftCertificate();
            certificate.setId(id);
            certificate.setName(rs.getString("name"));
            certificate.setDescription(rs.getString("description"));
            certificate.setPrice(rs.getBigDecimal("price"));
            certificate.setDuration(rs.getInt("duration"));
            certificate.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
            certificate.setLastUpdateDate(rs.getTimestamp("last_update_date").toLocalDateTime());
            certificate.setTags(new ArrayList<>());

            certificatesMap.put(id, certificate);
        }

        long tagId = rs.getLong("tag_id");
        if (tagId > 0) {
            Tag tag = new Tag();
            tag.setId(tagId);
            tag.setName(rs.getString("tag_name"));
            certificate.getTags().add(tag);
        }
    }

    public List<GiftCertificate> getCertificates() {
        return new ArrayList<>(certificatesMap.values());
    }
}
