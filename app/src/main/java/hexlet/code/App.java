package hexlet.code;

import io.javalin.Javalin;

public class App {
    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.jetty.modifyServer(server -> {});
        });
        
        app.get("/", ctx -> ctx.result("Hello World"));
        
        return app;
    }
    
    public static void main(String[] args) {
        String port = System.getenv().getOrDefault("PORT", "7070");
        Javalin app = getApp();
        app.start(Integer.parseInt(port));
    }
}
