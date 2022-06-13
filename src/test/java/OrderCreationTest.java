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
    public void makeOrder(){
        String accesToken = response.body().as(SuccessfulUserCreationData.class).getAccessToken();
        Response orderCreationResponse = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accesToken)
                .and()
                .body("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\"]}")
                .when()
                .post("/api/orders");

        orderCreationResponse.then().assertThat().body(orderCreationResponse.body().as(SuccessfulOrderCreationData.class).getName(), equalTo("Флюоресцентный бургер"), orderCreationResponse.body().as(SuccessfulOrderCreationData.class).getOrderNumber(), notNullValue(), orderCreationResponse.body().as(SuccessfulOrderCreationData.class).getSuccess(), true).and().statusCode(200);
    }

    @Test
    public void makeOrderWithoutIngridients(){
        String accesToken = response.body().as(SuccessfulUserCreationData.class).getAccessToken();
        Response orderCreationResponse = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accesToken)
                .and()
                .body("{\"ingredients\": []}")
                .when()
                .post("/api/orders");

        orderCreationResponse.then().assertThat().body("success", equalTo(false), "message", equalTo("Ingredient ids must be provided")).and().statusCode(400);
    }

    @Test
    public void makeOrderWithWrongProductCash(){
        String accesToken = response.body().as(SuccessfulUserCreationData.class).getAccessToken();
        Response orderCreationResponse = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accesToken)
                .and()
                .body("{\"ingredients\": [\"61c0c5a71d1f82001bd1zz6d\"]}")
                .when()
                .post("/api/orders");

        orderCreationResponse.then().statusCode(500);
    }

    @Test
    public void makeOrderWithoutAuth(){
        Response orderCreationResponse = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\"]}")
                .when()
                .post("/api/orders");

        orderCreationResponse.then().statusCode(403);
    }
}
