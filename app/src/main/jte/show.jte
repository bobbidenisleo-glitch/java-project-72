@param hexlet.code.model.Url url
@param java.util.List<hexlet.code.model.UrlCheck> checks

<@layout>
    <h1>Сайт: ${url.getName()}</h1>
    <p>ID: ${url.getId()}</p>
    <p>Создан: ${url.getCreatedAt().toString()}</p>

    <form action="/urls/${url.getId()}/checks" method="post">
        <input type="submit" class="btn btn-primary" value="Запустить проверку">
    </form>

    <h2>Проверки</h2>
    <div class="table-responsive">
        <table class="table table-bordered table-hover">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Код ответа</th>
                    <th>Заголовок</th>
                    <th>h1</th>
                    <th>Описание</th>
                    <th>Дата</th>
                </tr>
            </thead>
            <tbody>
                @for(var check : checks)
                    <tr>
                        <td>${check.getId()}</td>
                        <td>${check.getStatusCode()}</td>
                        <td>${check.getTitle()}</td>
                        <td>${check.getH1()}</td>
                        <td>${check.getDescription()}</td>
                        <td>${check.getCreatedAt().toString()}</td>
                    </tr>
                @endfor
            </tbody>
        </table>
    </div>
</@layout>
