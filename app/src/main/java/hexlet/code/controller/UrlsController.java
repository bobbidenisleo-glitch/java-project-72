package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.Utils;
import io.javalin.http.Context;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class UrlsController {

    private static String truncate(String text) {
        if (text == null) {
            return "";
        }
        int maxLength = 255;
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public static void index(Context ctx) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
        }
    }

    public static void show(Context ctx) {
        try {
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
            ctx.status(500);
        }
    }

    public static void create(Context ctx) {
        try {
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
            
            try {
                URI uri = new URI(urlName);
                StringBuilder normalized = new StringBuilder();
                normalized.append(uri.getScheme())
                          .append("://")
                          .append(uri.getHost());
                if (uri.getPort() != -1) {
                    normalized.append(":").append(uri.getPort());
                }
                urlName = normalized.toString();
            } catch (Exception e) {
                // ignore
            }
            
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
            ctx.status(500);
        }
    }

    public static void check(Context ctx) {
        try {
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
            
            String title = truncate(doc.title());
            String h1 = doc.selectFirst("h1") != null
                ? truncate(doc.selectFirst("h1").text())
                : "";
            String description = doc.selectFirst("meta[name=description]") != null
                ? truncate(doc.selectFirst("meta[name=description]").attr("content"))
                : "";
            
            var check = new UrlCheck(
                id,
                response.getStatus(),
                title,
                h1,
                description
            );
            UrlCheckRepository.save(check);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect("/urls/" + id);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect("/urls/" + ctx.pathParam("id"));
        }
    }
}
