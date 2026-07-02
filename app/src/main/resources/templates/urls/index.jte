@import java.time.format.DateTimeFormatter
@param hexlet.code.dto.UrlsPage page
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
                    @for(var url : page.getUrls())
                        <tr>
                            <td>${url.getId()}</td>
                            <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                            <td>${url.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}</td>
                            <td>
                                @if(page.getLatestChecks().get(url.getId()) != null)
                                    ${page.getLatestChecks().get(url.getId()).getStatusCode()}
                                @else
                                    &nbsp;
                                @endif
                            </td>
                            <td>
                                @if(page.getLatestChecks().get(url.getId()) != null)
                                    ${page.getLatestChecks().get(url.getId()).getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}
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
