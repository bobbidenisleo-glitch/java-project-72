package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import io.javalin.http.Context;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

public class UrlsController {

    public static void index(Context ctx) {
        try {
            System.out.println("UrlsController.index() called");
            var urls = UrlRepository.findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("urls", urls);
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flashType"));
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flashType", null);
            ctx.render("urls/index.jte", model);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            ctx.status(500);
        }
    }

    public static void show(Context ctx) {
        try {
            System.out.println("UrlsController.index() called");
            long id = Long.parseLong(ctx.pathParam("id"));
            var url = UrlRepository.findById(id).orElse(null);
            if (url == null) {
                ctx.status(404);
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
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            ctx.status(500);
        }
    }

    public static void create(Context ctx) {
        try {
            System.out.println("UrlsController.index() called");
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
            System.out.println("Searching for URL: " + urlName);
            var existing = UrlRepository.findByName(urlName);
            if (existing.isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "info");
                ctx.redirect("/urls/" + existing.get().getId());
                return;
            }
            var url = new Url(urlName);
            UrlRepository.save(url);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect("/urls/" + url.getId());
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            ctx.status(500);
        }
    }

    public static void check(Context ctx) {
        try {
            System.out.println("UrlsController.index() called");
            long id = Long.parseLong(ctx.pathParam("id"));
            var url = UrlRepository.findById(id).orElse(null);
            if (url == null) {
                ctx.status(404);
                return;
            }
            var response = Unirest.get(url.getName())
                .header("User-Agent", "Mozilla/5.0")
                .asString();
            var doc = Jsoup.parse(response.getBody());
            var check = new UrlCheck(
                id,
                response.getStatus(),
                doc.title(),
                doc.selectFirst("h1") != null ? doc.selectFirst("h1").text() : "",
                doc.selectFirst("meta[name=description]") != null
                    ? doc.selectFirst("meta[name=description]").attr("content")
                    : ""
            );
            UrlCheckRepository.save(check);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect("/urls/" + id);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect("/urls/" + ctx.pathParam("id"));
        }
    }
}
