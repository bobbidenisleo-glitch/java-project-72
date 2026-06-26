package hexlet.code.repository;

import hexlet.code.model.Url;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UrlRepository extends BaseRepository {

    public static List<Url> findAll() throws SQLException {
        log.info("UrlRepository.findAll() called");
        var sql = "SELECT * FROM urls ORDER BY id DESC";
        try (var conn = getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var urls = new ArrayList<Url>();
            while (resultSet.next()) {
                var url = new Url(resultSet.getString("name"));
                url.setId(resultSet.getLong("id"));
                var createdAt = resultSet.getTimestamp("created_at");
                if (createdAt != null) {
                    url.setCreatedAt(createdAt.toLocalDateTime());
                }
                urls.add(url);
            }
            return urls;
        }
    }

    public static Optional<Url> findById(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var url = new Url(resultSet.getString("name"));
                url.setId(resultSet.getLong("id"));
                var createdAt = resultSet.getTimestamp("created_at");
                if (createdAt != null) {
                    url.setCreatedAt(createdAt.toLocalDateTime());
                }
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var url = new Url(resultSet.getString("name"));
                url.setId(resultSet.getLong("id"));
                var createdAt = resultSet.getTimestamp("created_at");
                if (createdAt != null) {
                    url.setCreatedAt(createdAt.toLocalDateTime());
                }
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, Timestamp.valueOf(url.getCreatedAt()));
            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            }
            log.info("Url saved: id={}, name={}", url.getId(), url.getName());
        }
    }
}
