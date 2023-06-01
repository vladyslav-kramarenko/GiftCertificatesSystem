package com.epam.esm.api;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Objects;

@EntityScan(basePackages = "com.epam.esm")
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.epam.esm.core.repository")
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {"com.epam.esm.api", "com.epam.esm.core"})
@PropertySource("classpath:application-${spring.profiles.active}.properties")
@EnableCaching
public class ApiApplication
        extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApiApplication.class);
    }
    private final Environment env;

    @Autowired
    public ApiApplication(Environment env) {
        this.env = Objects.requireNonNull(env, "Environment must be initialised");
    }

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

        dataSource.setPoolName(env.getProperty("spring.datasource.hikari.poolName"));
        dataSource.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.minimumIdle"))));
        dataSource.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.maximumPoolSize"))));
        dataSource.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.idleTimeout"))));
        dataSource.setPoolName(env.getProperty("spring.datasource.hikari.poolName"));
        dataSource.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.maxLifetime"))));
        dataSource.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.connectionTimeout"))));

        return dataSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
