import api.data.SuccessfulUserCreationData;
import api.user.LoginUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {

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
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/auth/register");
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
    @DisplayName("Успешный логин пользователя")
    public void successfulLogin(){
        LoginUser loginUser = new LoginUser();
        Response successfulLoginResponse = loginUser.getLoginResponse(userMail, userPassword);
        successfulLoginResponse.then().statusCode(200).and().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин с не правильным паролем")
    public void loginWrongPassword(){
        LoginUser loginUser = new LoginUser();
        Response badLoginResponse = loginUser.getLoginResponse(userMail, "1234");
        badLoginResponse.then().statusCode(401).and().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Логин с не правильным email")
    public void loginWrongEmail(){
        LoginUser loginUser = new LoginUser();
        Response badLoginResponse = loginUser.getLoginResponse("test@mail.ru", userPassword);
        badLoginResponse.then().statusCode(401).and().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Логин без email")
    public void loginNoEmail(){
        LoginUser loginUser = new LoginUser();
        Response badLoginResponse = loginUser.getLoginResponse("", userPassword);
        badLoginResponse.then().statusCode(401).and().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Логин без пароля")
    public void loginNoPassword(){
        LoginUser loginUser = new LoginUser();
        Response badLoginResponse = loginUser.getLoginResponse(userMail, "");
        badLoginResponse.then().statusCode(401).and().assertThat().body("success", equalTo(false));
    }
}
