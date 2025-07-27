package org.example.configs;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    public static synchronized HikariDataSource getDataSource(Dotenv dotenv) {
        if (dataSource == null || dataSource.isClosed()) {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(dotenv.get("DB_URL"));
                config.setUsername(dotenv.get("DB_USER"));
                config.setPassword(dotenv.get("DB_PASSWORD"));
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(5);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);
                config.setPoolName("MarketplaceHikariCP");

                dataSource = new HikariDataSource(config);
                log.info("HikariCP connection pool initialized successfully.");
            } catch (Exception e) {
                log.error("Error initializing HikariCP connection pool: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to initialize database connection pool", e);
            }
        }
        return dataSource;
    }
}