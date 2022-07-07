import api.data.SuccessfulUserCreationData;
import api.user.UserCreation;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserCreationTest {

    private Response response;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
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
    @DisplayName("Создание пользователя")
    public void createUser(){
        UserCreation userCreation = new UserCreation();
        response = userCreation.getUserCreationResponse(RandomStringUtils.randomAlphabetic(8) + "@mail.ru", RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(8));
        response.then().statusCode(200);
    }

    @Test
    @DisplayName("Создании двух одинаковых пользователей")
    public void createExistingUser(){
        UserCreation userCreation = new UserCreation();
        response = userCreation.getTwoEqualUsersResponse(RandomStringUtils.randomAlphabetic(8) + "@mail.ru", RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(8));
        response.then().statusCode(403).and().assertThat().body("success", equalTo(false), "message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWithoutMail(){
        UserCreation userCreation = new UserCreation();
        response = userCreation.getTwoEqualUsersResponse("", RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(8));
        response.then().statusCode(403).and().assertThat().body("success", equalTo(false), "message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createUserWithoutPassword(){
        UserCreation userCreation = new UserCreation();
        response = userCreation.getUserCreationResponse(RandomStringUtils.randomAlphabetic(8) + "@mail.ru", "", RandomStringUtils.randomAlphabetic(8));
        response.then().statusCode(403).and().assertThat().body("success", equalTo(false), "message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя ,без имени")
    public void createUserWithoutName(){
        UserCreation userCreation = new UserCreation();
        response = userCreation.getUserCreationResponse(RandomStringUtils.randomAlphabetic(8) + "@mail.ru", RandomStringUtils.randomAlphabetic(8), "");
        response.then().statusCode(403).and().assertThat().body("success", equalTo(false), "message", equalTo("Email, password and name are required fields"));
    }
}
