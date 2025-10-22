package tests;

import configs.BaseConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;


public class TestBase {
    public static BaseConfig baseConfig = ConfigFactory.create(BaseConfig.class, System.getProperties());

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = baseConfig.baseUri();
        RestAssured.basePath = baseConfig.basePath();
    }
}
