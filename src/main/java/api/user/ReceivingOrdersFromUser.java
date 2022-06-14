package api.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ReceivingOrdersFromUser {

    @Step("Получение ответа о заказе пользователя")
    public Response getReceivingOrderResponse(String accessToken){
        Response response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .and()
                .when()
                .get("/api/orders");

        return response;
    }
}
