package com.epam.esm.dao;

import com.epam.esm.dao.impl.giftCertificate.GiftCertificateDaoImpl;
import com.epam.esm.dao.impl.tag.TagDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class TestConfig {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public GiftCertificateDao giftCertificateDao(JdbcTemplate jdbcTemplate) {
        return new GiftCertificateDaoImpl(jdbcTemplate);
    }

    @Bean
    public TagDao tagDao(JdbcTemplate jdbcTemplate) {
        return new TagDaoImpl(jdbcTemplate);
    }
}
