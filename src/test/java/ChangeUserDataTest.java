import api.data.SuccessfulUserCreationData;
import api.user.ChangeUserData;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;

public class ChangeUserDataTest {

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
    @DisplayName("Успешное изменение данных пользователя")
    public void changeUserData(){
        ChangeUserData changeUserData = new ChangeUserData();
        Response сhangeUserDataResponse = changeUserData.getChangeDataResponse(RandomStringUtils.randomAlphabetic(8) + "@mail.ru",RandomStringUtils.randomAlphabetic(8), response.body().as(SuccessfulUserCreationData.class).getAccessToken());
        сhangeUserDataResponse.then().statusCode(200);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void changeUserDataWithoutAuth(){
        ChangeUserData changeUserData = new ChangeUserData();
        Response сhangeUserDataResponse = changeUserData.getChangeDataResponse(RandomStringUtils.randomAlphabetic(8) + "@mail.ru",RandomStringUtils.randomAlphabetic(8), "");
        сhangeUserDataResponse.then().statusCode(401).and().assertThat().body("success", equalTo(false), "message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение почты на уже существующую почту")
    public void changeUserDataWithExistedEmail(){
        ChangeUserData changeUserData = new ChangeUserData();
        Response сhangeUserDataResponse = changeUserData.getChangeDataResponse(userMail,RandomStringUtils.randomAlphabetic(8), response.body().as(SuccessfulUserCreationData.class).getAccessToken());
        сhangeUserDataResponse.then().statusCode(403).and().assertThat().body("success", equalTo(false), "message", equalTo("data.User with such email already exists"));
    }

}
