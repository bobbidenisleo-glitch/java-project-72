package hexlet.code.repository;

import hexlet.code.model.Url;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, Timestamp.valueOf(url.getCreatedAt()));
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            }
        }
    }
    
    public static Optional<Url> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Url url = new Url(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }
    
    public static Optional<Url> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Url url = new Url(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }
    
    public static List<Url> findAll() throws SQLException {
        String sql = "SELECT * FROM urls ORDER BY id DESC";
        List<Url> urls = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Url url = new Url(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                urls.add(url);
            }
        }
        return urls;
    }
}
