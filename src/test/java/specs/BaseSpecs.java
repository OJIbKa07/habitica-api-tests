package specs;

import configs.BaseConfigProvider;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.given;


public class BaseSpecs {

    public static RequestSpecification requestSpec = given()
            .filter(withCustomTemplates())
            .log().all()
            .contentType("application/json")
            .baseUri(BaseConfigProvider.baseConfig.baseUri())
            .basePath(BaseConfigProvider.baseConfig.basePath());

    public static ResponseSpecification responseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(LogDetail.ALL)
                .build();
    }
}
