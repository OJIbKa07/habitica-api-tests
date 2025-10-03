package specs;

import configs.BaseConfigProvider;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;


public class BaseSpecs {

    public static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(BaseConfigProvider.baseConfig.baseUri())
            .setBasePath(BaseConfigProvider.baseConfig.basePath())
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
