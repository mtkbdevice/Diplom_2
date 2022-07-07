package api.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class LoginUser {

    @Step("Получение ответа о логине")
    public Response getLoginResponse(String userMail, String userPassword){
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"email\":\"" + userMail + "\","
                        + "\"password\":\"" + userPassword + "\"}")
                .when()
                .post("/api/auth/login");

        return response;
    }
}
