package com.epam.esm;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.giftCertificate.GiftCertificateDaoImpl;
import com.epam.esm.dao.impl.tag.TagDaoImpl;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Application configuration class that sets up beans for the application.
 */
@EnableTransactionManagement(proxyTargetClass = true)
@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Autowired
    private Environment env;

    /**
     * Creates a data source for the test environment using an H2 embedded database.
     *
     * @return the configured DataSource for the test environment
     */
    @Bean
    @Profile("test")
    public DataSource embeddedDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
    }

    /**
     * Creates a data source for the production environment using HikariCP.
     *
     * @return the configured DataSource for the production environment
     */
    @Bean
    @Profile("!test")
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

    /**
     * Creates a JdbcTemplate instance with the given DataSource.
     *
     * @param dataSource the DataSource to be used by the JdbcTemplate
     * @return a new JdbcTemplate instance
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Creates a GiftCertificateDao instance with the given JdbcTemplate.
     *
     * @param jdbcTemplate the JdbcTemplate to be used by the GiftCertificateDao
     * @return a new GiftCertificateDao instance
     */
    @Bean
    public GiftCertificateDao giftCertificateDao(JdbcTemplate jdbcTemplate) {
        return new GiftCertificateDaoImpl(jdbcTemplate);
    }

    /**
     * Creates a TagDao instance with the given JdbcTemplate.
     *
     * @param jdbcTemplate the JdbcTemplate to be used by the TagDao
     * @return a new TagDao instance
     */
    @Bean
    public TagDao tagDao(JdbcTemplate jdbcTemplate) {
        return new TagDaoImpl(jdbcTemplate);
    }

    /**
     * Creates a PlatformTransactionManager instance with the given DataSource.
     *
     * @param dataSource the DataSource to be used by the PlatformTransactionManager
     * @return a new PlatformTransactionManager instance
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

