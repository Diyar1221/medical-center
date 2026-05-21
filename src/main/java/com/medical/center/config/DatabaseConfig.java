package com.medical.center.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "RAILWAY_ENVIRONMENT", havingValue = "true", matchIfMissing = false)
public class DatabaseConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Railway даёт URL вида: postgresql://user:pass@host:5432/db
        // Spring Boot требует: jdbc:postgresql://host:5432/db
        String jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");

        // Если URL содержит user:pass@ — извлекаем credentials
        if (jdbcUrl.contains("@")) {
            String withoutJdbc = jdbcUrl.replace("jdbc:postgresql://", "");
            String[] parts = withoutJdbc.split("@");
            String credentials = parts[0];
            String hostAndDb = parts[1];

            String username = credentials.split(":")[0];
            String password = credentials.split(":")[1];
            String cleanUrl = "jdbc:postgresql://" + hostAndDb;

            return DataSourceBuilder.create()
                .url(cleanUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
        }

        return DataSourceBuilder.create()
            .url(jdbcUrl)
            .driverClassName("org.postgresql.Driver")
            .build();
    }
}
