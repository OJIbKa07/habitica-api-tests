package specs;

import configs.BaseConfig;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.aeonbits.owner.ConfigFactory;

import static helpers.allure.report.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.given;

public class BaseSpecs {

    public static final BaseConfig baseConfig = ConfigFactory.create(BaseConfig.class, System.getProperties());

    public static RequestSpecification requestSpec = given()
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
