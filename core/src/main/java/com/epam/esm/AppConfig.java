package com.epam.esm;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.giftCertificate.GiftCertificateDaoImpl;
import com.epam.esm.dao.impl.tag.TagDaoImpl;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement(proxyTargetClass = true)
@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Autowired
    private Environment env;
    @Bean
    @Profile("test")
    @Qualifier("dataSource")
    public DataSource embeddedDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
    }
    @Bean
    @Profile("!test")
    @Qualifier("dataSource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setMaximumPoolSize(Integer.parseInt(env.getProperty(
                "spring.datasource.hikari.maximum-pool-size")));
        return dataSource;
    }
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    @Bean
    @Profile("test")
    public GiftCertificateDao giftCertificateDao(JdbcTemplate jdbcTemplate) {
        return new GiftCertificateDaoImpl(jdbcTemplate);
    }
    @Bean
    @Profile("test")
    public TagDao tagDao(JdbcTemplate jdbcTemplate) {
        return new TagDaoImpl(jdbcTemplate);
    }
}

