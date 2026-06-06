@param java.util.List urls
@param java.lang.String flash
@param java.lang.String flashType

@template.tag.layout(
    content = @`
        @if(flash != null && !flash.isEmpty())
            <div class="alert alert-${flashType}" role="alert">
                ${flash}
            </div>
        @endif

        <h1>Сайты</h1>
        <div class="table-responsive">
            <table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Имя</th>
                        <th>Дата добавления</th>
                    </tr>
                </thead>
                <tbody>
                    @for(var url : urls)
                        <tr>
                            <td>${url.getId()}</td>
                            <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                            <td>${url.getCreatedAt().toString()}</td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        </div>
    `
)
