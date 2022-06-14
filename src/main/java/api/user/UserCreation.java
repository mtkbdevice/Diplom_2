package api.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserCreation {

    @Step("Получение ответа о создании пользователя")
    public Response getUserCreationResponse(String email, String password, String name){
        String registerRequestBody = "{\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\","
                + "\"name\":\"" + name + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/auth/register");

        return response;
    }

    @Step("Получение ответа о создании одинаковых пользователей")
    public Response getTwoEqualUsersResponse(String email, String password, String name){
        String registerRequestBody = "{\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\","
                + "\"name\":\"" + name + "\"}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/auth/register");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/auth/register");

        return response;
    }
}
