package models;

import io.ebean.Model;
import org.hibernate.validator.constraints.Email;
import play.data.validation.Constraints;


/**
 * Данный класс необходим для обработки формы регистрации.
 * Используется для валидации двух типов.
 *
 * 1. В качестве валидации отдельных полей используются аннотации
 * @Email - строка соответствует адресу эл. почты
 * @Required - обязательное поле.
 *
 * Для задания осмысленного сообщения при наарушении данного ограничения,
 * используется параметр message.
 * @Email(message = "Некорректный адрес электронной почты")
 *
 *
 * 2. Валидация на уровне формы с помощью метода String validate()
 * Когда нет возможности ограничиться валидацией полей по отдельности,
 * например когда условие валидации зависит от соответствия значений нескольких полей,
 *
 * Валидация форма регистрации проходит тогда, когда пользователь с данным email еще не зарегистрирован
 *
 */

public class Register extends Model {

    @Email(message= "Некорректный адрес электронной почты")
    @Constraints.Required(message = "Обязательное поле")
    public String email;

    @Constraints.Required(message = "Обязательное поле")
    public String password;

    /**
     * Дополнительный метод валидации формы.
     * Возвращает текст ошибки. В случае успеха валидации, возвращает null.
     */
    public String validate() {
        boolean available = User.emailAvailable(email);
        return available ? null : "Пользователь с данным почтовым адресом уже зарегистрирован";
    }
}