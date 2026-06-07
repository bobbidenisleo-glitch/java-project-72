package hexlet.code.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
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

        if (dbUrl != null && dbUrl.startsWith("postgresql://")) {
            try {
                URI dbUri = new URI(dbUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();
                
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                
                dataSource = new HikariDataSource(config);
                return;
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid DATABASE_URL format", e);
            }
        }
        
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        
        if (dbUrl.startsWith("jdbc:postgresql")) {
            config.setDriverClassName("org.postgresql.Driver");
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
}
