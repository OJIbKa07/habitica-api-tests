package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import tests.TestBase;

import static helpers.allure.report.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.given;

public class BaseSpecs {

    public static RequestSpecification requestSpec = given()
            .baseUri(TestBase.baseConfig.baseUri())
            .basePath(TestBase.baseConfig.basePath())
            .filter(withCustomTemplates())
            .log().all()
            .contentType("application/json");

    public static ResponseSpecification responseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(LogDetail.ALL)
                .build();
    }
}
