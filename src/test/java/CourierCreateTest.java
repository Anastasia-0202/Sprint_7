import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.steps.CourierSteps;
import org.example.pojo.CourierCreateRequest;
import org.example.pojo.CourierLoginRequest;
import org.junit.After;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierCreateTest {


    public static String login = "sychyova";
    public static String password = "asdf123";
    public static String firstName = "Анастасия";

    private CourierSteps courierSteps = new CourierSteps();
    private CourierLoginRequest courierLoginRequest;

    @After
    public void cleanUp() {
        if (courierLoginRequest != null) {
            courierSteps.courierDeleteAfterLogin(courierLoginRequest);
        }
    }

    @Test
    @DisplayName("Создание нового курьера")
    @Description("Проверяем, что курьера можно создать с валидными данными")
    public void createNewCourier() {
        CourierCreateRequest courierCreateRequest = new CourierCreateRequest(login, password, firstName);
        courierLoginRequest = new CourierLoginRequest(login, password);

        courierSteps.courierCreate(courierCreateRequest)
                .assertThat()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("Попытка создать двух курьеров с одинаковым набором данных. Создание второго курьера должно провалиться")
    public void createTwoIdenticalCouriers() {
        CourierCreateRequest courierCreateRequest = new CourierCreateRequest(login, password, firstName);
        courierLoginRequest = new CourierLoginRequest(login, password);

        // Создаём первого курьера
        courierSteps.courierCreate(courierCreateRequest)
                .assertThat()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        // Пытаемся создать второго курьера с теми же данными
        courierSteps.courierCreate(courierCreateRequest)
                .assertThat()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Попытка создать курьера без передачи поля login. Создание курьера должно провалиться")
    public void createCourierWithoutLogin() {
        CourierCreateRequest courierCreateRequest = new CourierCreateRequest(null, password, firstName);

        courierSteps.courierCreate(courierCreateRequest)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Попытка создать курьера без передачи поля password. Создание курьера должно провалиться")
    public void createCourierWithoutPassword() {
        CourierCreateRequest courierCreateRequest = new CourierCreateRequest(login, null, firstName);

        courierSteps.courierCreate(courierCreateRequest)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}