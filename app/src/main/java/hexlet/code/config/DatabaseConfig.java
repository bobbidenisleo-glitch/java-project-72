package hexlet.code.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    public static void init() {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DATABASE_URL");
        }
        
        if (dbUrl != null && !dbUrl.isBlank() && dbUrl.startsWith("postgresql://")) {
            dbUrl = "jdbc:" + dbUrl;
        }
        
        // Принудительно добавляем порт 5432, если его нет
        if (dbUrl != null && dbUrl.startsWith("jdbc:postgresql://")) {
            // Ищем первый '/' после "jdbc:postgresql://"
            String prefix = "jdbc:postgresql://";
            String rest = dbUrl.substring(prefix.length());
            int slashIndex = rest.indexOf('/');
            if (slashIndex > 0) {
                String hostPart = rest.substring(0, slashIndex);
                // Если в hostPart нет двоеточия (порта), добавляем :5432
                if (!hostPart.contains(":")) {
                    String newUrl = prefix + hostPart + ":5432" + rest.substring(slashIndex);
                    dbUrl = newUrl;
                    System.out.println("Added default port 5432 to URL");
                }
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
        System.out.println("Database initialized with URL: " + dbUrl);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            init();
        }
        return dataSource.getConnection();
    }
}
