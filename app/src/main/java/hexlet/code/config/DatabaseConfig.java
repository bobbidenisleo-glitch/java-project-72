package hexlet.code.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class DatabaseConfig {
    private static HikariDataSource dataSource;

    public static void init() {
        if (dataSource == null) {
            var config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1");
            config.setUsername("");
            config.setPassword("");
            config.setDriverClassName("org.h2.Driver");
            dataSource = new HikariDataSource(config);
            log.info("Database initialized");
        }
    }

    public static Connection getConnection() throws SQLException {
        log.info("DatabaseConfig.getConnection() called");
        return dataSource.getConnection();
    }

    public static void resetDataSource() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
            log.info("Database connection pool closed");
        }
    }
}
