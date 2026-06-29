package hexlet.code.repository;

import hexlet.code.model.UrlCheck;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck check) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, check.getUrlId());
            stmt.setInt(2, check.getStatusCode());
            stmt.setString(3, check.getTitle());
            stmt.setString(4, check.getH1());
            stmt.setString(5, check.getDescription());
            stmt.setTimestamp(6, Timestamp.valueOf(check.getCreatedAt()));
            stmt.executeUpdate();
            
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                check.setId(generatedKeys.getLong(1));
            }
        }
    }
    
    public static List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC";
        List<UrlCheck> checks = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UrlCheck check = new UrlCheck(
                    rs.getLong("url_id"),
                    rs.getInt("status_code"),
                    rs.getString("title"),
                    rs.getString("h1"),
                    rs.getString("description")
                );
                check.setId(rs.getLong("id"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    check.setCreatedAt(createdAt.toLocalDateTime());
                }
                checks.add(check);
            }
        }
        return checks;
    }
    
    // НОВЫЙ МЕТОД: получаем все проверки одним запросом
    public static List<UrlCheck> findAll() throws SQLException {
        String sql = "SELECT * FROM url_checks ORDER BY id DESC";
        List<UrlCheck> checks = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UrlCheck check = new UrlCheck(
                    rs.getLong("url_id"),
                    rs.getInt("status_code"),
                    rs.getString("title"),
                    rs.getString("h1"),
                    rs.getString("description")
                );
                check.setId(rs.getLong("id"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    check.setCreatedAt(createdAt.toLocalDateTime());
                }
                checks.add(check);
            }
        }
        return checks;
    }

    public static List<UrlCheck> findLatestChecks() throws SQLException {
        String sql = "SELECT DISTINCT ON (url_id) * FROM url_checks ORDER BY url_id DESC, id DESC";
        List<UrlCheck> checks = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UrlCheck check = new UrlCheck(
                    rs.getLong("url_id"),
                    rs.getInt("status_code"),
                    rs.getString("title"),
                    rs.getString("h1"),
                    rs.getString("description")
                );
                check.setId(rs.getLong("id"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    check.setCreatedAt(createdAt.toLocalDateTime());
                }
                checks.add(check);
            }
        }
        return checks;
    }

}

