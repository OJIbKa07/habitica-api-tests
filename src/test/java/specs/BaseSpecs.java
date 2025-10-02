package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;


public class BaseSpecs {

    public static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://habitica.com")
            .setBasePath("/api/v3")
            .setContentType("application/json")
            .log(LogDetail.ALL)
            .build();

    public static ResponseSpecification responseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(LogDetail.ALL)
                .build();
    }
}
