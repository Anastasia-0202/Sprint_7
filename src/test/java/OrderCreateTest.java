import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.pojo.OrderCreateRequest;
import org.example.steps.OrderSteps;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private List<String> color;
    private OrderSteps orderSteps = new OrderSteps();
    private Integer trackId;

    private boolean orderCreated = false;

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет самоката - {0}")
    public static Object[][] dataGen() {
        return new Object[][] {
                {List.of("BLACK", "GREY")},
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of()}
        };
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с самокатами разных цветов через параметризованный тест")
    public void orderCreate() {
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(color);

        var response = orderSteps.orderCreate(orderCreateRequest)
                .assertThat()
                .statusCode(SC_CREATED)
                .body("track", instanceOf(Integer.class))
                .extract().response();

        trackId = response.path("track");
        orderCreated = true;
    }

    @After
    public void cleanUp() {
        if (orderCreated && trackId != null) {
            orderSteps.cancelOrder(trackId);
            orderCreated = false;
            trackId = null;
        }
    }
}