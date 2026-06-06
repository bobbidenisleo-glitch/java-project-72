package gg.jte.generated.ondemand;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,3,3,5,5,6,6,7,7,7,7,8,8,8,10,10,31,31,31,32};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.lang.String flash, java.lang.String flashType) {
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
				jteOutput.writeContent("\n\n        <div class=\"row\">\n            <div class=\"col-12 col-md-10 col-lg-8 mx-auto border rounded-3 bg-light p-5\">\n                <h1 class=\"display-3\">Анализатор страниц</h1>\n                <p class=\"lead\">Бесплатно проверяйте сайты на SEO пригодность</p>\n                <form action=\"/urls\" method=\"post\" class=\"row\">\n                    <div class=\"col-8\">\n                        <input\n                            type=\"text\"\n                            name=\"url\"\n                            class=\"form-control form-control-lg\"\n                            placeholder=\"https://www.example.com\"\n                        >\n                    </div>\n                    <div class=\"col-2\">\n                        <input type=\"submit\" class=\"btn btn-primary btn-lg\" value=\"Проверить\">\n                    </div>\n                </form>\n            </div>\n        </div>\n    ");
			}
		});
		jteOutput.writeContent("\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		java.lang.String flash = (java.lang.String)params.get("flash");
		java.lang.String flashType = (java.lang.String)params.get("flashType");
		render(jteOutput, jteHtmlInterceptor, flash, flashType);
	}
}
