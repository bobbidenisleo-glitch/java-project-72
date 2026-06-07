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

    // Конструктор без id (для новых записей)
    public UrlCheck(Long urlId, int statusCode, String title, String h1, String description) {
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    // Конструктор с id (для чтения из БД)
    public UrlCheck(Long id, Long urlId, int statusCode, String title, String h1, String description, LocalDateTime createdAt) {
        this.id = id;
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public Long getUrlId() {
        return urlId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getH1() {
        return h1;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
