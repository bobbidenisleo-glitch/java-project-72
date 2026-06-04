package hexlet.code.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    public static void init() {
        // Получаем URL базы данных из переменной окружения или используем H2 в памяти
        String dbUrl = System.getenv().getOrDefault("JDBC_DATABASE_URL", 
            "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        
        // Для PostgreSQL — указываем драйвер
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
