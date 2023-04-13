package com.epam.esm.dao.impl.giftCertificate;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GiftCertificateResultSetExtractor implements ResultSetExtractor<List<GiftCertificate>> {

    @Override
    public List<GiftCertificate> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, GiftCertificate> giftCertificateMap = new LinkedHashMap<>();

        while (rs.next()) {
            Long id = rs.getLong("id");
            GiftCertificate certificate = giftCertificateMap.get(id);

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
                giftCertificateMap.put(id, certificate);
            }

            long tagId = rs.getLong("tag_id");
            if (tagId > 0) {
                Tag tag = new Tag(tagId,rs.getString("tag_name"));
                certificate.getTags().add(tag);
            }
        }

        return new ArrayList<>(giftCertificateMap.values());
    }
}

