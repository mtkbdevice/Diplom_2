import api.data.SuccessfulOrderCreationData;
import api.data.SuccessfulUserCreationData;
import api.user.MakeOrder;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreationTest {

    private Response response;
    private String userMail;
    private String userPassword;
    private String userName;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userMail = RandomStringUtils.randomAlphabetic(8) + "@mail.ru";
        userPassword = RandomStringUtils.randomAlphabetic(8);
        userName = RandomStringUtils.randomAlphabetic(8);
        String registerRequestBody = "{\"email\":\"" + userMail + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + userName + "\"}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/auth/register");

        response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"email\":\"" + userMail + "\","
                        + "\"password\":\"" + userPassword + "\"}")
                .when()
                .post("/api/auth/login ");
    }

    @After
    public void tearDown(){
        if(response.statusCode() == 200){

            given()
                    .header("Content-type", "application/json")
                    .and()
                    .when()
                    .delete("/api/auth/user/" + response.body().as(SuccessfulUserCreationData.class).getAccessToken());
        }
    }

    @Test
    @DisplayName("Осуществление заказа")
    public void makeOrder(){
        MakeOrder makeOrder = new MakeOrder();
        Response orderResponse = makeOrder.getOrderResponse("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\"]}", response.body().as(SuccessfulUserCreationData.class).getAccessToken());
        orderResponse.then().statusCode(200).and().assertThat().body(orderResponse.body().as(SuccessfulOrderCreationData.class).getName(), equalTo("Флюоресцентный бургер"), orderResponse.body().as(SuccessfulOrderCreationData.class).getOrderNumber(), notNullValue(), orderResponse.body().as(SuccessfulOrderCreationData.class).getSuccess(), true);
    }

    @Test
    @DisplayName("Заказ без ингридиентов")
    public void makeOrderWithoutIngridients(){
        MakeOrder makeOrder = new MakeOrder();
        Response orderResponse = makeOrder.getOrderResponse("{\"ingredients\": []}", response.body().as(SuccessfulUserCreationData.class).getAccessToken());
        orderResponse.then().statusCode(400).and().assertThat().body("success", equalTo(false), "message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ с несоществующим продуктом")
    public void makeOrderWithWrongProductCash(){
        MakeOrder makeOrder = new MakeOrder();
        Response orderResponse = makeOrder.getOrderResponse("{\"ingredients\": [\"61c0c5a71d1f82001bd1zz6d\"]}", response.body().as(SuccessfulUserCreationData.class).getAccessToken());
        orderResponse.then().statusCode(500);
    }

    @Test
    @DisplayName("Заказ без аутификации")
    public void makeOrderWithoutAuth(){
        MakeOrder makeOrder = new MakeOrder();
        Response orderCreationResponse = makeOrder.getOrderResponse("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\"]}", "");
        orderCreationResponse.then().statusCode(403);
    }
}
