package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import java.util.List;
import java.util.Map;

public class UrlsPage extends BasePage {
    private List<Url> urls;
    private Map<Long, UrlCheck> latestChecks;

    public UrlsPage(List<Url> urls, Map<Long, UrlCheck> latestChecks) {
        this.urls = urls;
        this.latestChecks = latestChecks;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public Map<Long, UrlCheck> getLatestChecks() {
        return latestChecks;
    }
}
