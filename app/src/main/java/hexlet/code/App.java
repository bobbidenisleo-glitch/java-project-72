package hexlet.code;

import io.javalin.Javalin;

public class App {
    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            // Включаем логирование для разработки (Javalin 6)
            config.jetty.modifyServer(server -> {
                // Можно оставить пустым или добавить Jetty настройки
            });
        });
        
        // Корневой маршрут
        app.get("/", ctx -> ctx.result("Hello World"));
        
        return app;
    }
    
    public static void main(String[] args) {
        // Получаем порт из переменной окружения PORT (для Render)
        String port = System.getenv().getOrDefault("PORT", "7070");
        Javalin app = getApp();
        app.start(Integer.parseInt(port));
    }
}
