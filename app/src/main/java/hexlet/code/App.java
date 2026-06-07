package hexlet.code;

import hexlet.code.config.DatabaseConfig;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinJte;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static Javalin getApp() throws Exception {
        DatabaseConfig.init();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = new String(Files.readAllBytes(
                Paths.get("src/main/resources/schema.sql")));
            stmt.execute(sql);
        }
        
        JavalinRenderer.register(new JavalinJte(), ".jte");
        
        Javalin app = Javalin.create(config -> {});
        
        app.get("/", ctx -> {
            Map<String, Object> model = new HashMap<>();
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flashType"));
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flashType", null);
            ctx.render("index.jte", model);
        });
        
        app.post("/urls", ctx -> {
            String urlName = ctx.formParam("url");
            if (urlName == null || urlName.isBlank()) {
                ctx.sessionAttribute("flash", "URL не может быть пустым");
                ctx.sessionAttribute("flashType", "danger");
                ctx.redirect("/");
                return;
            }
            
            if (!urlName.startsWith("http://") && !urlName.startsWith("https://")) {
                urlName = "http://" + urlName;
            }
            
            var existingUrl = UrlRepository.findByName(urlName);
            if (existingUrl.isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "info");
                ctx.redirect("/urls/" + existingUrl.get().getId());
                return;
            }
            
            var url = new Url(urlName);
            UrlRepository.save(url);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect("/urls/" + url.getId());
        });
        
        app.get("/urls/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            var url = UrlRepository.findById(id).orElse(null);
            if (url == null) {
                ctx.status(404);
                ctx.result("URL not found");
                return;
            }
            var checks = UrlCheckRepository.findByUrlId(id);
            Map<String, Object> model = new HashMap<>();
            model.put("url", url);
            model.put("checks", checks);
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flashType"));
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flashType", null);
            ctx.render("show.jte", model);
        });
        
        app.post("/urls/{id}/checks", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            var url = UrlRepository.findById(id)
                .orElse(null);
            
            try {
                Document doc = Jsoup.connect(url.getName())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();
                int statusCode = doc.connection().response().statusCode();
                String title = doc.title();
                String h1 = doc.select("h1").first() != null ? doc.select("h1").first().text() : "";
                String description = doc.select("meta[name=description]").first() != null 
                    ? doc.select("meta[name=description]").first().attr("content") : "";
                
                UrlCheck check = new UrlCheck(id, statusCode, title, h1, description);
                UrlCheckRepository.save(check);
                ctx.sessionAttribute("flash", "Проверка выполнена");
                ctx.sessionAttribute("flashType", "success");
            } catch (IOException e) {
                ctx.sessionAttribute("flash", "Ошибка: " + e.getMessage());
                ctx.sessionAttribute("flashType", "danger");
            }
            ctx.redirect("/urls/" + id);
        });
        
        app.get("/urls", ctx -> {
            var urls = UrlRepository.findAll();
            for (var url : urls) {
                var checks = UrlCheckRepository.findByUrlId(url.getId());
                if (!checks.isEmpty()) {
                    url.setLastCheck(checks.get(0));
                }
            }
            Map<String, Object> model = new HashMap<>();
            model.put("urls", urls);
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flashType"));
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flashType", null);
            ctx.render("urls/index.jte", model);
        });
        
        return app;
    }
    
    public static void main(String[] args) throws Exception {
        String port = System.getenv().getOrDefault("PORT", "7070");
        Javalin app = getApp();
        app.start(Integer.parseInt(port));
    }
}
