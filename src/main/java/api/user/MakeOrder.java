package api.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class MakeOrder {

    @Step("Получение ответа о о создании заказа")
    public Response getOrderResponse(String body, String accessToken){
        Response orderCreationResponse = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .and()
                .body(body)
                .when()
                .post("/api/orders");

        return orderCreationResponse;
    }
}
