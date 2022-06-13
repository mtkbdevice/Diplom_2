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
    public void successfullLogin(){
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"email\":\"" + userMail + "\","
                        + "\"password\":\"" + userPassword + "\"}")
                .when()
                .post("/api/auth/login");

        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    public void loginWrongPassword(){
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"email\":\"" + userMail + "\","
                        + "\"password\":\"" + 1234 + "\"}")
                .when()
                .post("/api/auth/login");

        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    public void loginWrongEmail(){
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"email\":\"" + "test@mail.ru" + "\","
                        + "\"password\":\"" + userPassword + "\"}")
                .when()
                .post("/api/auth/login");

        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    public void loginNoEmail(){
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"email\":\"" + "" + "\","
                        + "\"password\":\"" + userPassword + "\"}")
                .when()
                .post("/api/auth/login");

        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    public void loginNoPassword(){
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"email\":\"" + userMail + "\","
                        + "\"password\":\"" + "" + "\"}")
                .when()
                .post("/api/auth/login");

        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }
}
