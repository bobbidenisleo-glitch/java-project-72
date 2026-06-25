package hexlet.code;

import hexlet.code.config.DatabaseConfig;
import hexlet.code.config.JteConfig;
import hexlet.code.controller.UrlsController;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinJte;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class App {

    public static Javalin getApp() throws SQLException, Exception {
        DatabaseConfig.init();

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement()) {
            var inputStream = App.class.getClassLoader().getResourceAsStream("schema.sql");
            if (inputStream == null) {
                throw new RuntimeException("schema.sql not found in classpath");
            }
            String sql;
            try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }
            stmt.execute(sql);
        }

        JavalinRenderer.register(new JavalinJte(JteConfig.create()), ".jte");

        Javalin app = Javalin.create(config -> {
            // Javalin 5.6.3 не имеет bundledPlugins
        });

        // Глобальный обработчик ошибок
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();

            ctx.sessionAttribute("flash",
                "Произошла внутренняя ошибка сервера");
            ctx.sessionAttribute("flashType", "danger");

            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.redirect("/");
        });

        app.get("/", ctx -> ctx.render("index.jte"));
        app.get("/urls", UrlsController::index);
        app.post("/urls", UrlsController::create);
        app.get("/urls/{id}", UrlsController::show);
        app.post("/urls/{id}/checks", UrlsController::check);

        return app;
    }

    public static void main(String[] args) throws Exception {
        Javalin app = getApp();
        app.start(Integer.parseInt(System.getenv().getOrDefault("PORT", "7070")));
    }
}
