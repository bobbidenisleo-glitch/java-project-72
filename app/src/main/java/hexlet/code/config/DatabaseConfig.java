package hexlet.code.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    public static void init() {
        if (dataSource != null) {
            return;
        }

        String dbUrl = System.getenv("JDBC_DATABASE_URL");

        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DATABASE_URL");
        }

        HikariConfig config = new HikariConfig();

        if (dbUrl != null && dbUrl.startsWith("postgresql://")) {
            try {
                URI dbUri = new URI(dbUrl);

                String[] userInfo = dbUri.getUserInfo().split(":");
                String username = userInfo[0];
                String password = userInfo[1];

                String jdbcUrl = "jdbc:postgresql://"
                        + dbUri.getHost()
                        + dbUri.getPath();

                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");

            } catch (Exception e) {
                throw new RuntimeException("Invalid DATABASE_URL", e);
            }
        } else {
            if (dbUrl == null || dbUrl.isBlank()) {
                dbUrl = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
            }

            config.setJdbcUrl(dbUrl);

            if (dbUrl.startsWith("jdbc:postgresql")) {
                config.setDriverClassName("org.postgresql.Driver");
            }
        }

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            init();
        }
        return dataSource.getConnection();
    }

    public static void resetDataSource() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
