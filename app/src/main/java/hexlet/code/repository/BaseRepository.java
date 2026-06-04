package hexlet.code.repository;

import java.sql.Connection;
import java.sql.SQLException;
import hexlet.code.config.DatabaseConfig;

public class BaseRepository {
    protected static Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }
}
