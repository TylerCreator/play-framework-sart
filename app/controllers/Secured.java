package controllers;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import router.Routes;
/**
 * Created by Admin on 24.11.2017.
 */
public class Secured extends Security.Authenticator {
    /**
     * Механизм аутентификации
     *
     * @param ctx контекст запроса.
     * @return строку-email для текущего пользователя, хранящуюся в сессии при аутентификации. В случае ее отсутствия возвращает null
     */
    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get("email");
    }

    /**
     * Перенаправление в случае неуспеха аутентификации. Как правило перенаправляет на форму логина
     *
     * @param ctx контекст
     * @return перенаправление на страницу логина
     */

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Auth.login());
    }
}