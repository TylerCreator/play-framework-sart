package controllers;

import router.Routes;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import models.Register;
import views.html.*;
import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class Auth extends Controller {

    private final FormFactory formFactory;

    @Inject
    public Auth(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    //добавлять методы-контроллеры будем сюда
    // МАРШРУТЫ В conf/routes  !!!

    /**
     * Страница выдает пустую форму регистрации.
     * Если осуществлен вход(логин) перенаправляет на гл. страницу
     * @return пустая форма регистрации.
     */
    public Result signup() {
        if (session("email") != null) return redirect(routes.HomeController.index());
        else return ok(register.render(formFactory.form(Register.class)));
    }

    /**
     * Обработка формы регистрации.
     * Производится валидация формы. Штатными средствами класса Register
     *
     * Проверяется, что данный email еще не зарегистрирован в системе.
     *
     *
     * @return В случае успеха создает пользоваетеля в базе, аутентифицирует и перенаправляет на гл. страницу.
     */
    public Result register() {
        Form<Register> regForm = formFactory.form(Register.class).bindFromRequest();
        if (regForm.hasErrors()) return badRequest(register.render(regForm));  // валидация формы не прошла
        else {
            //Из формы достаем объект класса Register, нашу модель формы регистрации с заполненными полями
            Register reg = regForm.get();
            // В конструкторе пользователя производится установка хэша от пароля с солью
            User user = new User(reg.email, reg.password);
            // Сохраняем в БД
            user.save();
            // аутентифицируем пользоваетеля, устанавливаем ему ключ в сессии
            session("email", reg.email);
            // перенаправляем куда нам нужно после успешной аутентификации
            return redirect(routes.HomeController.index());
        }
    }
}
