package hexlet.code.repository;

import java.sql.Connection;
import java.sql.SQLException;
import hexlet.code.config.DatabaseConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseRepository {
    protected static Connection getConnection() throws SQLException {
        log.info("BaseRepository.getConnection() called");
        return DatabaseConfig.getConnection();
    }
}
