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

    // Единый метод для установки flash-сообщения
    private static void setFlash(Context ctx, String message, String type) {
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flashType", type);
    }

    private static void prepareModelWithFlash(Context ctx, Map<String, Object> model) {
        model.put("flash", ctx.sessionAttribute("flash"));
        model.put("flashType", ctx.sessionAttribute("flashType"));
        ctx.sessionAttribute("flash", null);
        ctx.sessionAttribute("flashType", null);
    }

    private static void renderInvalidUrlError(Context ctx) {
        ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
        setFlash(ctx, "Некорректный URL", "danger");
        Map<String, Object> model = new HashMap<>();
        prepareModelWithFlash(ctx, model);
        ctx.render("index.jte", model);
    }

    private static void renderCheckError(Context ctx, long id) {
        setFlash(ctx, "Произошла ошибка при проверке", "danger");
        ctx.redirect("/urls/" + id);
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.findAll();
        var latestChecks = UrlCheckRepository.findLatestChecks();
        var page = new UrlsPage(urls, latestChecks);

        Map<String, Object> model = new HashMap<>();
        model.put("page", page);
        prepareModelWithFlash(ctx, model);
        ctx.render("urls/index.jte", model);
    }

    public static void show(Context ctx) throws SQLException {
        long id = Long.parseLong(ctx.pathParam("id"));
        var url = UrlRepository.findById(id)
            .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));

        var checks = UrlCheckRepository.findByUrlId(id);
        Map<String, Object> model = new HashMap<>();
        model.put("url", url);
        model.put("checks", checks);
        prepareModelWithFlash(ctx, model);
        ctx.render("show.jte", model);
    }

    public static void create(Context ctx) throws SQLException {
        String urlName = ctx.formParam("url");

        if (urlName == null || urlName.isBlank()) {
            setFlash(ctx, "URL не может быть пустым", "danger");
            ctx.redirect("/");
            return;
        }

        if (!urlName.startsWith("http://") && !urlName.startsWith("https://")) {
            urlName = "http://" + urlName;
        }

        URI uri;
        try {
            uri = new URI(urlName);
        } catch (URISyntaxException e) {
            renderInvalidUrlError(ctx);
            return;
        }

        String host = uri.getHost();
        if (host == null || host.isBlank() || !host.contains(".")) {
            renderInvalidUrlError(ctx);
            return;
        }

        String normalizedUrl = uri.getScheme() + "://" + uri.getHost();
        if (uri.getPort() != -1) {
            normalizedUrl += ":" + uri.getPort();
        }

        var existing = UrlRepository.findByName(normalizedUrl);
        if (existing.isPresent()) {
            setFlash(ctx, "Страница уже существует", "info");
            ctx.redirect("/urls/" + existing.get().getId());
            return;
        }

        var url = new Url(normalizedUrl);
        UrlRepository.save(url);
        setFlash(ctx, "Страница успешно добавлена", "success");
        ctx.redirect("/urls/" + url.getId());
    }

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
            renderCheckError(ctx, id);
            return;
        }

        int statusCode = response.getStatus();
        if (statusCode >= HttpStatus.BAD_REQUEST.getCode()) {
            renderCheckError(ctx, id);
            return;
        }

        String title;
        String h1;
        String description;
        try {
            var doc = Jsoup.parse(response.getBody());
            title = Utils.truncate(doc.title());
            h1 = doc.selectFirst("h1") != null
                ? Utils.truncate(doc.selectFirst("h1").text())
                : "";
            description = doc.selectFirst("meta[name=description]") != null
                ? Utils.truncate(doc.selectFirst("meta[name=description]").attr("content"))
                : "";
        } catch (Exception e) {
            renderCheckError(ctx, id);
            return;
        }

        var check = new UrlCheck(id, statusCode, title, h1, description);
        UrlCheckRepository.save(check);
        setFlash(ctx, "Страница успешно проверена", "success");
        ctx.redirect("/urls/" + id);
    }
}
