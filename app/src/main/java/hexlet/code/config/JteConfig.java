package hexlet.code.config;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;

public class JteConfig {

    public static TemplateEngine create() {
        ResourceCodeResolver resolver = new ResourceCodeResolver("templates");
        return TemplateEngine.create(resolver, ContentType.Html);
    }
}
