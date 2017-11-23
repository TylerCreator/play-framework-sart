package models;

import io.ebean.Finder;
import io.ebean.Model;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.validator.constraints.Email;
import play.api.libs.Crypto;
import play.data.validation.Constraints;


import javax.persistence.Entity;
import javax.persistence.Id;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Entity
public class User extends Model {

    /**
     * Email - электронная почта пользователя
     */
    @Id
    @Email

    public String email;

    /**
     * Хэш пароля
     */
    public  String passwordHash;

    /**
     * Соль, случайная последовательность символов, используется для хэширования пароля ,
     * хранится для каждого пользователя своя.
     */
    public String salt;

    public User(String email, String password) {
        this.email = email;
        setPassword(password);
    }

    /**
     * Объект для связи с СУБД. Для поиска объектов в базе данных
     */
    public static Finder<String, User> find = new Finder<String, User>(User.class);


    /**
     * Генерирует хэш от строки.
     * Определяет текущую версию выбранного алгоритма конкретной криптосистемы
     *
     * @param s строка
     * @return хэш от пароля в соответствии с применяемым алгоритмом
     */
    private String getHash(String s) {
        return SHA256(s);
    }

    /**
     * Возвращает хэш-функцию SHA-256
     *
     * @see <a href="https://ru.wikipedia.org/wiki/SHA-2">SHA-2 в Википедии</a>
     *
     * @param str строка для хэширования
     * @return хэш от строки
     */
    public static String SHA256(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(str.getBytes());
            return Base64.encodeBase64String(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Генерирует случайную строку для использования в качестве соли
     * @return случайная последовательность символов
     */
    public static String genSalt(){
        final Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    /**
     * Подписывает строку СЕКРЕТОМ приложения с помощью встроенной в Play библиотекой Crypto
     * Используется алгоритм SHA-1
     *
     * @link play.api.libs.Crypto.sign
     * @see play.api.libs.Crypto.cookieSigner().sign(String)
     *
     * @param str Строка для подписи (хэширования)
     * @return подпись, хэш для заданной строки
     */
    public static String SHA1(String str){
        //Используется класс Crypto из пакета, встроенного в Play
        return Crypto.cookieSigner().sign(str);
    }
    /**
     * Устанавливаем новый пароль для пользователя.
     *
     * @param password (новый) пароль
     */
    public void setPassword(String password) {
        //Сгенерировать соль
        this.salt = genSalt();
        //установить хэш от пароля (с солью)
        this.passwordHash = getHash(password+salt);
    }

    /**
     * Существует ли пользователь с данным почтовым адресом
     * @param email адрес электронной почты
     * @return false в случае, если пользователь с данным почтовым адресом уже зарегистрирован в системе.
     * Иначе возвращает true
     */
    public static boolean emailAvailable(String email) {
        return (find.byId(email) == null);
    }
}