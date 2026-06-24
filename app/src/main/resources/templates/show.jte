@import java.time.format.DateTimeFormatter
@param hexlet.code.model.Url url
@param java.util.List<hexlet.code.model.UrlCheck> checks
@param java.lang.String flash
@param java.lang.String flashType

@template.tag.layout(flash = flash, flashType = flashType, 
    content = @`

        <h1>Сайт: ${url.getName()}</h1>

        <table class="table table-bordered" data-test="url">
            <tbody>
                <tr>
                    <th>ID</th>
                    <td>${url.getId()}</td>
                </tr>
                <tr>
                    <th>Имя</th>
                    <td>${url.getName()}</td>
                </tr>
                <tr>
                    <th>Дата создания</th>
                    <td>${url.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}</td>
                </tr>
                @if(!checks.isEmpty())
                <tr>
                    <th>Код ответа</th>
                    <td>${checks.get(0).getStatusCode()}</td>
                </tr>
                @endif
            </tbody>
        </table>

        <form action="/urls/${url.getId()}/checks" method="post">
            <input type="submit" class="btn btn-primary" value="Запустить проверку">
        </form>

        <h2 class="mt-4">Проверки</h2>
        <div class="table-responsive">
            <table class="table table-bordered table-hover" data-test="checks">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Код ответа</th>
                        <th>h1</th>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Дата</th>
                    </tr>
                </thead>
                <tbody>
                    @for(var check : checks)
                        <tr>
                            <td>${check.getId()}</td>
                            <td>${check.getStatusCode()}</td>
                            <td>${check.getH1()}</td>
                            <td>${check.getTitle()}</td>
                            <td>${check.getDescription()}</td>
                            <td>${check.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}</td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        </div>
    `
)
