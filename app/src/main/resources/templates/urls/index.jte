@import java.time.format.DateTimeFormatter
@param java.util.List<hexlet.code.model.Url> urls
@param java.lang.String flash
@param java.lang.String flashType

@template.tag.layout(flash = flash, flashType = flashType, 
    content = @`

        <h1>Сайты</h1>
        <div class="table-responsive">
            <table class="table table-bordered table-hover" data-test="urls">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Имя</th>
                        <th>Дата создания</th>
                        <th>Код ответа</th>
                        <th>Дата проверки</th>
                    </tr>
                </thead>
                <tbody>
                    @for(var url : urls)
                        <tr>
                            <td>${url.getId()}</td>
                            <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                            <td>${url.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}</td>
                            <td>
                                @if(url.getLastCheck() != null)
                                    ${url.getLastCheck().getStatusCode()}
                                @else
                                    &nbsp;
                                @endif
                            </td>
                            <td>
                                @if(url.getLastCheck() != null)
                                    ${url.getLastCheck().getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}
                                @else
                                    &nbsp;
                                @endif
                            </td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        </div>
    `
)
