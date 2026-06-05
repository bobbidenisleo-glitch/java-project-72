package hexlet.code.model;

import java.time.LocalDateTime;

public class UrlCheck {
    private Long id;
    private Long urlId;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private LocalDateTime createdAt;

    public UrlCheck(Long urlId, int statusCode, String title, String h1, String description) {
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUrlId() { return urlId; }
    public int getStatusCode() { return statusCode; }
    public String getTitle() { return title; }
    public String getH1() { return h1; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
