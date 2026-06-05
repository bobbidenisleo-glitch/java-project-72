package gg.jte.generated.ondemand;
public final class JteshowGenerated {
	public static final String JTE_NAME = "show.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,4,4,4,4,5,5,5,6,6,6,8,8,8,8,25,25,27,27,27,28,28,28,29,29,29,30,30,30,31,31,31,32,32,32,34,34,38};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, hexlet.code.model.Url url, java.util.List<hexlet.code.model.UrlCheck> checks) {
		jteOutput.writeContent("\n<@layout>\n    <h1>Сайт: ");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(url.getName());
		jteOutput.writeContent("</h1>\n    <p>ID: ");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(url.getId());
		jteOutput.writeContent("</p>\n    <p>Создан: ");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(url.getCreatedAt().toString());
		jteOutput.writeContent("</p>\n    \n    <form action=\"/urls/");
		jteOutput.setContext("form", "action");
		jteOutput.writeUserContent(url.getId());
			jteOutput.setContext("form", null);
		jteOutput.writeContent("/checks\" method=\"post\">\n        <input type=\"submit\" value=\"Запустить проверку\">\n    </form>\n    \n    <h2>Проверки</h2>\n    <table border=\"1\">\n        <thead>\n            <tr>\n                <th>ID</th>\n                <th>Код ответа</th>\n                <th>Заголовок</th>\n                <th>h1</th>\n                <th>Описание</th>\n                <th>Дата</th>\n            </tr>\n        </thead>\n        <tbody>\n            ");
		for (var check : checks) {
			jteOutput.writeContent("\n                <tr>\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getId());
			jteOutput.writeContent("</td>\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getStatusCode());
			jteOutput.writeContent("</td>\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getTitle());
			jteOutput.writeContent("</td>\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getH1());
			jteOutput.writeContent("</td>\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getDescription());
			jteOutput.writeContent("</td>\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getCreatedAt().toString());
			jteOutput.writeContent("</td>\n                </tr>\n            ");
		}
		jteOutput.writeContent("\n        </tbody>\n    </table>\n</@layout>\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		hexlet.code.model.Url url = (hexlet.code.model.Url)params.get("url");
		java.util.List<hexlet.code.model.UrlCheck> checks = (java.util.List<hexlet.code.model.UrlCheck>)params.get("checks");
		render(jteOutput, jteHtmlInterceptor, url, checks);
	}
}
