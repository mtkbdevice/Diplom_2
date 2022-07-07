import api.data.SuccessfulUserCreationData;
import api.user.ReceivingOrdersFromUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class ReceivingOrdersFromSpecificUserTest {

    private Response response;
    private String userMail;
    private String userPassword;
    private String userName;

    @Before
    public void setUp() {
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
    @DisplayName("Получение заказов пользователя")
    public void getOrders(){
        ReceivingOrdersFromUser receivingOrdersFromUser = new ReceivingOrdersFromUser();
        Response responseOrderInfo = receivingOrdersFromUser.getReceivingOrderResponse(response.body().as(SuccessfulUserCreationData.class).getAccessToken());
        responseOrderInfo.then().statusCode(200);
    }

    @Test
    @DisplayName("Получение заказов пользователя без аутинтификации")
    public void getOrdersWithoutAuth(){
        ReceivingOrdersFromUser receivingOrdersFromUser = new ReceivingOrdersFromUser();
        Response responseOrderInfo = receivingOrdersFromUser.getReceivingOrderResponse("");
        responseOrderInfo.then().statusCode(401);
    }
}
