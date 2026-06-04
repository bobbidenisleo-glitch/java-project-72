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
        
        // Если в URL нет порта, добавляем стандартный порт 5432
        if (dbUrl != null && dbUrl.startsWith("jdbc:postgresql://")) {
            // Проверяем, есть ли порт после хоста
            // Формат: jdbc:postgresql://host/database или jdbc:postgresql://host:port/database
            String withoutPrefix = dbUrl.substring("jdbc:postgresql://".length());
            int slashIndex = withoutPrefix.indexOf('/');
            if (slashIndex > 0) {
                String hostPart = withoutPrefix.substring(0, slashIndex);
                if (!hostPart.contains(":")) {
                    // Нет порта — добавляем :5432
                    String fixedUrl = "jdbc:postgresql://" + hostPart + ":5432" + withoutPrefix.substring(slashIndex);
                    dbUrl = fixedUrl;
                    System.out.println("Fixed JDBC URL: " + dbUrl);
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
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            init();
        }
        return dataSource.getConnection();
    }
}
