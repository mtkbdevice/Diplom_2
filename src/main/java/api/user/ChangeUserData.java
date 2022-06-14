package api.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ChangeUserData {

    @Step("Получение ответа об измнении данных пользователя")
    public Response getChangeDataResponse(String newUserMail, String newUserName, String accesToken){
        Response changeResponse = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accesToken)
                .and()
                .body("{\"email\":\"" + newUserMail + "\","
                        + "\"name\":\"" + newUserName + "\"}")
                .when()
                .patch("/api/auth/user");

        return changeResponse;
    }
}
