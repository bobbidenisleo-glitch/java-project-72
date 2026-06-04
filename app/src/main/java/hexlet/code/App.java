package hexlet.code;

import hexlet.code.config.DatabaseConfig;
import io.javalin.Javalin;
import java.sql.Connection;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static Javalin getApp() throws Exception {
        // Инициализируем базу данных
        DatabaseConfig.init();
        
        // Создаём таблицы из schema.sql
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = new String(Files.readAllBytes(
                Paths.get("app/src/main/resources/schema.sql")));
            stmt.execute(sql);
        }
        
        Javalin app = Javalin.create(config -> {
            config.jetty.modifyServer(server -> {});
        });
        
        app.get("/", ctx -> ctx.result("Hello World"));
        
        return app;
    }
    
    public static void main(String[] args) throws Exception {
        String port = System.getenv().getOrDefault("PORT", "7070");
        Javalin app = getApp();
        app.start(Integer.parseInt(port));
    }
}
