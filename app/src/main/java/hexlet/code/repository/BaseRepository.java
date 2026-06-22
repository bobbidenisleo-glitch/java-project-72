package hexlet.code.repository;

import java.sql.Connection;
import java.sql.SQLException;
import hexlet.code.config.DatabaseConfig;

public class BaseRepository {
    protected static Connection getConnection() throws SQLException {
        System.out.println("BaseRepository.getConnection() called");
        return DatabaseConfig.getConnection();
    }
}
