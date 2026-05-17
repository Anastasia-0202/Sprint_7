import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.steps.CourierSteps;
import org.example.pojo.CourierCreateRequest;
import org.example.pojo.CourierLoginRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;

public class CourierLoginTest {

    public static String login = "sychyova";
    public static String password = "asdf123";
    public static String firstName = "Анастасия";

    private CourierSteps courierSteps = new CourierSteps();
    private CourierCreateRequest courierCreateRequest;
    private CourierLoginRequest courierLoginRequest;


    @Before
    public void setUp() {
        courierCreateRequest = new CourierCreateRequest(login, password, firstName);
        courierLoginRequest = new CourierLoginRequest(login, password);
        // Создаём курьера перед каждым тестом, где это нужно
        courierSteps.courierCreate(courierCreateRequest);
    }

    @After
    public void cleanUp() {
        // Удаляем курьера после каждого теста
        courierSteps.courierDeleteAfterLogin(courierLoginRequest);
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться с набором валидных данных")
    public void loginCourier() {
        courierSteps.courierLogin(courierLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .body("id", instanceOf(Integer.class));
    }

    @Test
    @DisplayName("Авторизация курьера без логина")
    @Description("Проверка, что курьер НЕ может авторизоваться без передачи поля login")
    public void loginCourierWithoutLogin() {
        CourierLoginRequest courierLoginRequestWithoutLogin = new CourierLoginRequest(null, password);

        courierSteps.courierLogin(courierLoginRequestWithoutLogin)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация курьера без пароля")
    @Description("Проверка, что курьер НЕ может авторизоваться без передачи поля password")
    public void loginCourierWithoutPassword() {
        CourierLoginRequest courierLoginRequestWithoutPassword = new CourierLoginRequest(login, null);


        courierSteps.courierLogin(courierLoginRequestWithoutPassword)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация курьера с неверным логином")
    @Description("Проверка, что курьер НЕ может авторизоваться, используя неверный логин")
    public void loginCourierWithWrongLogin() {
        CourierLoginRequest courierLoginRequestWithWrongLogin = new CourierLoginRequest("wrong_login", password);

        courierSteps.courierLogin(courierLoginRequestWithWrongLogin)
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация курьера с неверным паролем")
    @Description("Проверка, что курьер НЕ может авторизоваться, используя неверный пароль")
    public void loginCourierWithWrongPassword() {
        CourierLoginRequest courierLoginRequestWithWrongPassword = new CourierLoginRequest(login, "wrong_password");

        courierSteps.courierLogin(courierLoginRequestWithWrongPassword)
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}