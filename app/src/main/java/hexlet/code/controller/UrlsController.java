package hexlet.code.controller;

import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.Utils;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UrlsController {

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.findAll();
        var latestChecks = UrlCheckRepository.findLatestChecks();
        var page = new UrlsPage(urls, latestChecks);

        Map<String, Object> model = new HashMap<>();
        model.put("page", page);
        model.put("flash", ctx.sessionAttribute("flash"));
        model.put("flashType", ctx.sessionAttribute("flashType"));
        ctx.sessionAttribute("flash", null);
        ctx.sessionAttribute("flashType", null);
        ctx.render("urls/index.jte", model);
    }

    // show() — использует orElseThrow
    public static void show(Context ctx) throws SQLException {
        long id = Long.parseLong(ctx.pathParam("id"));
        var url = UrlRepository.findById(id)
            .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));

        var checks = UrlCheckRepository.findByUrlId(id);
        Map<String, Object> model = new HashMap<>();
        model.put("url", url);
        model.put("checks", checks);
        model.put("flash", ctx.sessionAttribute("flash"));
        model.put("flashType", ctx.sessionAttribute("flashType"));
        ctx.sessionAttribute("flash", null);
        ctx.sessionAttribute("flashType", null);
        ctx.render("show.jte", model);
    }

    public static void create(Context ctx) throws SQLException {
        String urlName = ctx.formParam("url");
        if (urlName == null || urlName.isBlank()) {
            ctx.sessionAttribute("flash", "URL не может быть пустым");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect("/");
            return;
        }
        URI uri;
        try {
            if (!urlName.startsWith("http://") && !urlName.startsWith("https://")) {
                urlName = "http://" + urlName;
            }
            uri = new URI(urlName);
        } catch (URISyntaxException e) {
            ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "danger");
            Map<String, Object> model = new HashMap<>();
            model.put("flash", "Некорректный URL");
            model.put("flashType", "danger");
            ctx.render("index.jte", model);
            return;
        }
        String host = uri.getHost();
        if (host == null || host.isBlank() || !host.contains(".")) {
            ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "danger");
            Map<String, Object> model = new HashMap<>();
            model.put("flash", "Некорректный URL");
            model.put("flashType", "danger");
            ctx.render("index.jte", model);
            return;
        }
        String normalizedUrl = uri.getScheme() + "://" + uri.getHost();
        if (uri.getPort() != -1) {
            normalizedUrl += ":" + uri.getPort();
        }
        var existing = UrlRepository.findByName(normalizedUrl);
        if (existing.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flashType", "info");
            ctx.redirect("/urls/" + existing.get().getId());
            return;
        }
        var url = new Url(normalizedUrl);
        UrlRepository.save(url);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flashType", "success");
        ctx.redirect("/urls/" + url.getId());
    }

    // check() — использует orElseThrow
    public static void check(Context ctx) throws SQLException {
        long id = Long.parseLong(ctx.pathParam("id"));
        var url = UrlRepository.findById(id)
            .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));

        kong.unirest.HttpResponse<String> response;
        try {
            response = Unirest.get(url.getName())
                .header("User-Agent", "Mozilla/5.0")
                .asString();
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect("/urls/" + id);
            return;
        }
        int statusCode = response.getStatus();
        if (statusCode >= HttpStatus.BAD_REQUEST.getCode()) {
            ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect("/urls/" + id);
            return;
        }
        var doc = Jsoup.parse(response.getBody());
        String title = Utils.truncate(doc.title());
        String h1 = doc.selectFirst("h1") != null
            ? Utils.truncate(doc.selectFirst("h1").text())
            : "";
        String description = doc.selectFirst("meta[name=description]") != null
            ? Utils.truncate(doc.selectFirst("meta[name=description]").attr("content"))
            : "";
        var check = new UrlCheck(id, statusCode, title, h1, description);
        UrlCheckRepository.save(check);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flashType", "success");
        ctx.redirect("/urls/" + id);
    }
}
