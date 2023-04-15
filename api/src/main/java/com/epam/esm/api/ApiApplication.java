package com.epam.esm.api;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@EntityScan(basePackages = "com.epam.esm.core.entity")
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.epam.esm.core.repository")
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {"com.epam.esm.api", "com.epam.esm.core"})
public class ApiApplication {

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

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
