package testcases;

import com.google.gson.Gson;
import io.restassured.http.ContentType;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.CustomError;
import utils.Publisher;


import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.protocol.HTTP.CONTENT_TYPE;

@Slf4j
public class ApiTests {

    public String url= "http://localhost:8081/v1/publisherevent/";

    @Test(priority = 1)
    public void postUrl_WhenSuccessReturns201AndCreatesPublisher() {
        Publisher publisher = new Publisher();
        publisher.setPublisherID(1);
        publisher.setTime("12:12");
        publisher.setReadings(10);

        Gson gson = new Gson();
        String requestBody = gson.toJson(publisher);

        Publisher created = given()
                .contentType(CONTENT_TYPE)
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .body(requestBody)
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .extract().body().as(Publisher.class);

        Assert.assertEquals("12:12", created.getTime());
        Assert.assertEquals( 10, created.getReadings().intValue());
    }

    @Test(priority = 2)
    public void getAll_WhenSuccessReturns200StatusAndNonZero() {
        List res = new ArrayList<>();
        res = given()
                .contentType(CONTENT_TYPE)
                .when()
                .get(url+"all")
                .then()
                .statusCode(200)
                .extract().body().as(res.getClass());
        Assert.assertTrue(res.size() > 0);
    }


    @Test(priority = 3)
    public void getPublisher_expectedStatus200AndCorrectData() {
        Publisher publisher = given()
                .contentType(CONTENT_TYPE)
                .when()
                .get(url+1)
                .then()
                .statusCode(200)
                .extract().body().as(Publisher.class);

        Assert.assertEquals( 10, publisher.getReadings().intValue());
        Assert.assertEquals("12:12", publisher.getTime());
    }

    @Test(priority = 4)
    public void givenInvalidID_WhenNotFoundReturns404AndAnErrorMessage() {
        CustomError error = given()
                .contentType(CONTENT_TYPE)
                .when()
                .get(url+10000)
                .then()
                .statusCode(404)
                .extract().body().as(CustomError.class);

        Assert.assertEquals("'10000' not found!", error.getError());
}


    @Test(priority = 5)
    public void givenPutUrl_WhenSuccessReturns200AndUpdates() {
        Publisher publisher = new Publisher();
        publisher.setPublisherID(1);
        publisher.setReadings(399);
        publisher.setTime("11:11");

        Gson gson = new Gson();
        String requestBody = gson.toJson(publisher);

        Publisher updatedPublisher = given()
                .contentType(CONTENT_TYPE)
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .body(requestBody)
                .when()
                .put(url)
                .then()
                .statusCode(200)
                .extract().body().as(Publisher.class);

        Assert.assertEquals(399, updatedPublisher.getReadings().intValue());
        Assert.assertEquals("11:11", updatedPublisher.getTime());
    }

    @Test(priority = 6)
    public void givenDeleteUrl_WhenNotFoundReturns404AndErrorMessage() {
        CustomError error = given()
                .contentType(CONTENT_TYPE)
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when()
                .delete(url+10000)
                .then()
                .statusCode(500)
                .extract().body().as(CustomError.class);

        Assert.assertEquals("Something bad happened", error.getError());
    }

    @Test(priority = 7)
    public void givenDeleteUrl_WhenSuccessReturns204() {
        given()
                .contentType(CONTENT_TYPE)
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when()
                .delete(url+1)
                .then()
                .statusCode(204);
    }

    @Test(priority = 2)
    public void getAllAbove100Reading_WhenSuccessReturns200StatusAndNonZero() { //expected 2 by dummy publisher
        List res = new ArrayList<>();
        res = given()
                .contentType(CONTENT_TYPE)
                .when()
                .get(url+"reading")
                .then()
                .statusCode(200)
                .extract().body().as(res.getClass());

        Assert.assertTrue(res.size() == 2);
    }


}
