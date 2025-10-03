package configs;

import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;

public class BaseConfigProvider {
    public static final BaseConfig baseConfig = ConfigFactory.create(BaseConfig.class, System.getProperties());
}
