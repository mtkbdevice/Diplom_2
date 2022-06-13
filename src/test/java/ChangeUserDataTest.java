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
    public void changeUserData(){
        Response changeResponse;
        String newUserMail = RandomStringUtils.randomAlphabetic(8) + "@mail.ru";
        String newUserName = RandomStringUtils.randomAlphabetic(8);
        String accesToken = response.body().as(SuccessfulUserCreationData.class).getAccessToken();
        changeResponse = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accesToken)
                .and()
                .body("{\"email\":\"" + newUserMail + "\","
                        + "\"name\":\"" + newUserName + "\"}")
                .when()
                .patch("/api/auth/user");
        changeResponse.then().statusCode(200);
    }

    @Test
    public void changeUserDataWithoutAuth(){
        Response changeResponse;
        String newUserMail = RandomStringUtils.randomAlphabetic(8) + "@mail.ru";
        String newUserName = RandomStringUtils.randomAlphabetic(8);
        changeResponse = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"email\":\"" + newUserMail + "\","
                        + "\"name\":\"" + newUserName + "\"}")
                .when()
                .patch("/api/auth/user");
        changeResponse.then().assertThat().body("success", equalTo(false), "message", equalTo("You should be authorised")).and().statusCode(401);
    }

    @Test
    public void changeUserDataWithExistedEmail(){
        Response changeResponse;
        String newUserName = RandomStringUtils.randomAlphabetic(8);
        String accesToken = response.body().as(SuccessfulUserCreationData.class).getAccessToken();

        changeResponse = given()
                .header("Content-type", "application/json")
                .and()
                .auth().oauth2(accesToken)
                .body("{\"email\":\"" + userMail + "\","
                        + "\"name\":\"" + newUserName + "\"}")
                .when()
                .patch("/api/auth/user");
        changeResponse.then().assertThat().body("success", equalTo(false), "message", equalTo("User with such email already exists")).and().statusCode(403);
    }

}
