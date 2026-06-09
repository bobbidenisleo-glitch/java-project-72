package hexlet.code;

import hexlet.code.config.DatabaseConfig;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinJte;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
public class App {
    public static Javalin getApp() throws Exception {
        DatabaseConfig.init();
        
        try (Connection conn = DatabaseConfig.getConnection();
     Statement stmt = conn.createStatement()) {

    var inputStream = App.class.getClassLoader()
        .getResourceAsStream("schema.sql");

    if (inputStream == null) {
        throw new RuntimeException("schema.sql not found in classpath");
    }

    String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
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
        
        app.get("/urls", ctx -> {
            var urls = UrlRepository.findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("urls", urls);
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flashType"));
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flashType", null);
            ctx.render("urls/index.jte", model);
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

            var url = UrlRepository.findById(id).orElse(null);
            if (url == null) {
                ctx.status(404);
                ctx.result("URL not found");
                return;
            }

            try {
                HttpResponse<String> response = Unirest.get(url.getName())
                    .header("User-Agent", "Mozilla/5.0")
                    .asString();

                int statusCode = response.getStatus();
                String body = response.getBody();

                Document doc = Jsoup.parse(body);

                String title = doc.title();

                var h1Element = doc.selectFirst("h1");
                String h1 = (h1Element != null) ? h1Element.text() : "";

                var metaElement = doc.selectFirst("meta[name=description]");
                String description = (metaElement != null)
                    ? metaElement.attr("content")
                    : "";

                UrlCheck check = new UrlCheck(id, statusCode, title, h1, description);
                UrlCheckRepository.save(check);

                ctx.sessionAttribute("flash", "Страница успешно проверена");
                ctx.sessionAttribute("flashType", "success");

            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
                ctx.sessionAttribute("flashType", "danger");
            }

            ctx.redirect("/urls/" + id);
        });
        
        return app;
    }
    
    public static void main(String[] args) throws Exception {
        String port = System.getenv().getOrDefault("PORT", "7070");
        Javalin app = getApp();
        app.start(Integer.parseInt(port));
    }
}
