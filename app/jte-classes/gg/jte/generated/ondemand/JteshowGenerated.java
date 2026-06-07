package gg.jte.generated.ondemand;
public final class JteshowGenerated {
	public static final String JTE_NAME = "show.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,5,5,7,7,8,8,9,9,9,9,10,10,10,12,12,14,14,14,20,20,20,24,24,24,28,28,28,30,30,33,33,33,35,35,39,39,39,39,57,57,59,59,59,60,60,60,61,61,61,62,62,62,63,63,63,64,64,64,66,66,70,70,70,71};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, hexlet.code.model.Url url, java.util.List<hexlet.code.model.UrlCheck> checks, java.lang.String flash, java.lang.String flashType) {
		jteOutput.writeContent("\n");
		gg.jte.generated.ondemand.tag.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n        ");
				if (flash != null && !flash.isEmpty()) {
					jteOutput.writeContent("\n            <div class=\"alert alert-");
					jteOutput.setContext("div", "class");
					jteOutput.writeUserContent(flashType);
						jteOutput.setContext("div", null);
					jteOutput.writeContent("\" role=\"alert\">\n                ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(flash);
					jteOutput.writeContent("\n            </div>\n        ");
				}
				jteOutput.writeContent("\n\n        <h1>Сайт: ");
				jteOutput.setContext("h1", null);
				jteOutput.writeUserContent(url.getName());
				jteOutput.writeContent("</h1>\n\n        <table class=\"table table-bordered\" data-test=\"url\">\n            <tbody>\n                <tr>\n                    <th>ID</th>\n                    <td>");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(url.getId());
				jteOutput.writeContent("</td>\n                </tr>\n                <tr>\n                    <th>Имя</th>\n                    <td>");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(url.getName());
				jteOutput.writeContent("</td>\n                </tr>\n                <tr>\n                    <th>Дата создания</th>\n                    <td>");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(url.getCreatedAt().toString());
				jteOutput.writeContent("</td>\n                </tr>\n                ");
				if (!checks.isEmpty()) {
					jteOutput.writeContent("\n                <tr>\n                    <th>Код ответа</th>\n                    <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(checks.get(0).getStatusCode());
					jteOutput.writeContent("</td>\n                </tr>\n                ");
				}
				jteOutput.writeContent("\n            </tbody>\n        </table>\n\n        <form action=\"/urls/");
				jteOutput.setContext("form", "action");
				jteOutput.writeUserContent(url.getId());
					jteOutput.setContext("form", null);
				jteOutput.writeContent("/checks\" method=\"post\">\n            <input type=\"submit\" class=\"btn btn-primary\" value=\"Запустить проверку\">\n        </form>\n\n        <h2 class=\"mt-4\">Проверки</h2>\n        <div class=\"table-responsive\">\n            <table class=\"table table-bordered table-hover\">\n                <thead>\n                    <tr>\n                        <th>ID</th>\n                        <th>Код ответа</th>\n                        <th>Заголовок</th>\n                        <th>h1</th>\n                        <th>Описание</th>\n                        <th>Дата</th>\n                    </tr>\n                </thead>\n                <tbody>\n                    ");
				for (var check : checks) {
					jteOutput.writeContent("\n                        <tr>\n                            <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(check.getId());
					jteOutput.writeContent("</td>\n                            <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(check.getStatusCode());
					jteOutput.writeContent("</td>\n                            <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(check.getTitle());
					jteOutput.writeContent("</td>\n                            <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(check.getH1());
					jteOutput.writeContent("</td>\n                            <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(check.getDescription());
					jteOutput.writeContent("</td>\n                            <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(check.getCreatedAt().toString());
					jteOutput.writeContent("</td>\n                        </tr>\n                    ");
				}
				jteOutput.writeContent("\n                </tbody>\n            </table>\n        </div>\n    ");
			}
		});
		jteOutput.writeContent("\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		hexlet.code.model.Url url = (hexlet.code.model.Url)params.get("url");
		java.util.List<hexlet.code.model.UrlCheck> checks = (java.util.List<hexlet.code.model.UrlCheck>)params.get("checks");
		java.lang.String flash = (java.lang.String)params.get("flash");
		java.lang.String flashType = (java.lang.String)params.get("flashType");
		render(jteOutput, jteHtmlInterceptor, url, checks, flash, flashType);
	}
}
