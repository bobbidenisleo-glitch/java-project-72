package hexlet.code;

import hexlet.code.config.DatabaseConfig;
import hexlet.code.dto.UrlDTO;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConfig.init();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = new String(Files.readAllBytes(
                Paths.get("src/main/resources/schema.sql")));
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM url_checks");
            stmt.execute("DELETE FROM urls");
            stmt.execute("ALTER TABLE urls ALTER COLUMN id RESTART WITH 1");
            stmt.execute("ALTER TABLE url_checks ALTER COLUMN id RESTART WITH 1");
        }
    }

    @Test
    void testMainPage() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testCreateUrl() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=example.com");
            assertThat(response.code()).isBetween(200, 399);
            var savedUrl = UrlRepository.findByName("http://example.com");
            assertThat(savedUrl).isPresent();
            assertThat(savedUrl.get().getName()).isEqualTo("http://example.com");
        });
    }

    @Test
    void testCreateDuplicateUrl() throws Exception {
        var url = new Url("https://hexlet.io");
        UrlRepository.save(url);

        var app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://hexlet.io");
            assertThat(response.code()).isBetween(200, 399);

            var saved = UrlRepository.findByName("https://hexlet.io");
            assertThat(saved).isPresent();

            var pageResponse = client.get("/urls/" + saved.get().getId());
            assertThat(pageResponse.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlsPage() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Сайты");
        });
    }

    @Test
    void testUrlsPageWithData() throws Exception {
        var url = new Url("https://example.com");
        UrlRepository.save(url);

        var check = new UrlCheck(
            url.getId(),
            200,
            "Test Title",
            "Test H1",
            "Test Description"
        );
        UrlCheckRepository.save(check);

        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            var body = response.body().string();
            assertThat(body).contains("https://example.com");
            assertThat(body).contains("200");
        });
    }

    @Test
    void testShowUrl() throws Exception {
        var app = App.getApp();
        var url = new Url("https://hexlet.io");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            var body = response.body().string();
            assertThat(response.code()).isEqualTo(200);
            assertThat(body).contains("https://hexlet.io");
        });
    }

    @Test
    void testUrlNotFound() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testUrlPageContainsChecksForm() throws Exception {
        var url = new Url("https://hexlet.io");
        UrlRepository.save(url);

        var app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            var body = response.body().string();

            assertThat(body)
                .contains("/urls/" + url.getId() + "/checks")
                .contains("data-test=\"checks\"");
        });
    }

    @Test
    void testDatabaseConfigConnection() throws Exception {
        DatabaseConfig.init();
        try (Connection conn = DatabaseConfig.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isClosed()).isFalse();
        }
    }

    @Test
    void testUrlModel() {
        Url url = new Url("https://hexlet.io");
        assertThat(url.getName()).isEqualTo("https://hexlet.io");
    }

    @Test
    void testUrlCheckModel() {
        UrlCheck check = new UrlCheck(1L, 200, "Test", "Test H1", "Test Description");
        assertThat(check.getStatusCode()).isEqualTo(200);
        assertThat(check.getTitle()).isEqualTo("Test");
        assertThat(check.getDescription()).isEqualTo("Test Description");
    }

    @Test
    void testInvalidUrlSubmission() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=invalid-url");
            assertThat(response.code()).isEqualTo(422);
        });
    }

    @Test
    void testEmptyUrlSubmission() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=");
            assertThat(response.code()).isBetween(200, 399);
        });
    }

    @Test
    void testCheckNonExistingUrl() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/999999/checks", "");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testUrlAllGettersAndSetters() {
        var createdAt = java.time.LocalDateTime.now();

        var url = new Url("https://example.com");

        url.setId(100L);
        url.setName("https://hexlet.io");
        url.setCreatedAt(createdAt);

        assertThat(url.getId()).isEqualTo(100L);
        assertThat(url.getName()).isEqualTo("https://hexlet.io");
        assertThat(url.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testUrlFullConstructor() {
        var createdAt = java.time.LocalDateTime.now();

        var url = new Url(
            42L,
            "https://hexlet.io",
            createdAt
        );

        assertThat(url.getId()).isEqualTo(42L);
        assertThat(url.getName()).isEqualTo("https://hexlet.io");
        assertThat(url.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testUrlCheckAllGettersAndSetters() {
        var createdAt = java.time.LocalDateTime.now();

        var check = new UrlCheck(
            1L,
            200,
            "Title",
            "H1",
            "Description"
        );

        check.setId(10L);
        check.setUrlId(20L);
        check.setStatusCode(404);
        check.setTitle("New Title");
        check.setH1("New H1");
        check.setDescription("New Description");
        check.setCreatedAt(createdAt);

        assertThat(check.getId()).isEqualTo(10L);
        assertThat(check.getUrlId()).isEqualTo(20L);
        assertThat(check.getStatusCode()).isEqualTo(404);
        assertThat(check.getTitle()).isEqualTo("New Title");
        assertThat(check.getH1()).isEqualTo("New H1");
        assertThat(check.getDescription()).isEqualTo("New Description");
        assertThat(check.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testUrlCheckFullConstructor() {
        var createdAt = java.time.LocalDateTime.now();

        var check = new UrlCheck(
            5L,
            6L,
            200,
            "Title",
            "H1",
            "Description",
            createdAt
        );

        assertThat(check.getId()).isEqualTo(5L);
        assertThat(check.getUrlId()).isEqualTo(6L);
        assertThat(check.getStatusCode()).isEqualTo(200);
        assertThat(check.getTitle()).isEqualTo("Title");
        assertThat(check.getH1()).isEqualTo("H1");
        assertThat(check.getDescription()).isEqualTo("Description");
        assertThat(check.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testEmptyUrl() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=");
            assertThat(response.code()).isBetween(200, 399);
        });
    }

    @Test
    void testUrlWithoutProtocol() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=google.com");
            var saved = UrlRepository.findByName("http://google.com");
            assertThat(saved).isPresent();
        });
    }

    @Test
    void testDuplicateUrlBranch() throws Exception {
        var url = new Url("https://hexlet.io");
        UrlRepository.save(url);

        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://hexlet.io");
            assertThat(response.code()).isBetween(200, 399);

            var existing = UrlRepository.findByName("https://hexlet.io");
            assertThat(existing).isPresent();
            assertThat(existing.get().getId()).isEqualTo(url.getId());
        });
    }

    @Test
    void testCreateCheckForMissingUrl() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/999999/checks", "");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testInitWhenDataSourceAlreadyExists() throws Exception {
        DatabaseConfig.init();
        DatabaseConfig.init();
        try (var connection = DatabaseConfig.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isClosed()).isFalse();
        }
    }

    @Test
    void testCheckUrlWithMockServer() throws Exception {
        var mockWebServer = new MockWebServer();
        var html = "<html><head><title>Mock Title</title></head><body><h1>Mock H1</h1><meta name=\"description\" content=\"Mock Description\"></body></html>";
        mockWebServer.enqueue(new MockResponse().setBody(html));
        mockWebServer.start();

        var url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks", "");
            assertThat(response.code()).isBetween(200, 399);

            var checks = UrlCheckRepository.findByUrlId(url.getId());
            assertThat(checks).hasSize(1);
            var check = checks.get(0);
            assertThat(check.getTitle()).isEqualTo("Mock Title");
            assertThat(check.getH1()).isEqualTo("Mock H1");
            assertThat(check.getDescription()).isEqualTo("Mock Description");
            assertThat(check.getCreatedAt()).isNotNull();
        });

        mockWebServer.shutdown();
    }

    @Test
    void testMalformedUrl() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=http://[");
            assertThat(response.code()).isEqualTo(422);
        });
    }

    @Test
    void testFindAllChecks() throws Exception {
        var url = new Url("https://example.com");
        UrlRepository.save(url);

        var check = new UrlCheck(
            url.getId(),
            200,
            "Title",
            "H1",
            "Description"
        );
        UrlCheckRepository.save(check);

        var checks = UrlCheckRepository.findAll();

        assertThat(checks).hasSize(1);

        var saved = checks.get(0);
        assertThat(saved.getStatusCode()).isEqualTo(200);
        assertThat(saved.getTitle()).isEqualTo("Title");
        assertThat(saved.getH1()).isEqualTo("H1");
        assertThat(saved.getDescription()).isEqualTo("Description");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void testAppMainPage() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            var body = response.body().string();
            assertThat(body).contains("Анализатор страниц");
        });
    }

    @Test
    void testAppUrlsPage() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testAppCreateUrl() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isBetween(200, 399);
        });
    }

    @Test
    void testAppShowUrl() throws Exception {
        var url = new Url("https://test.com");
        UrlRepository.save(url);
        
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testInvalidUrlWithoutProtocol() throws Exception {
        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=invalid.domain");
            assertThat(response.code()).isEqualTo(200);
            var saved = UrlRepository.findByName("http://invalid.domain");
            assertThat(saved).isPresent();
        });
    }

    @Test
    void testResetDataSource() throws Exception {
        DatabaseConfig.init();

        try (var conn = DatabaseConfig.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isClosed()).isFalse();
        }

        DatabaseConfig.resetDataSource();

        try (var conn = DatabaseConfig.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isClosed()).isFalse();
        }
    }

    @Test
    void testUrlDTO() {
        var createdAt = java.time.LocalDateTime.now();
        var check = new UrlCheck(1L, 200, "Title", "H1", "Description");
        var dto = new UrlDTO(1L, "https://example.com", createdAt, check);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("https://example.com");
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getLastCheck()).isEqualTo(check);
    }

    @Test
    void testCheckWithInvalidUrl() throws Exception {
        var url = new Url("http://localhost:1");
        UrlRepository.save(url);

        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks", "");
            assertThat(response.code()).isBetween(200, 399);
        });
    }

    @Test
    void testCheckWithBadStatusCode() throws Exception {
        var mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        mockWebServer.start();

        var url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        var app = App.getApp();
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks", "");
            assertThat(response.code()).isBetween(200, 399);
        });

        mockWebServer.shutdown();
    }
}
