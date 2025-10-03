package configs;

import org.aeonbits.owner.ConfigFactory;

public class AuthConfigProvider {
    public static final AuthConfig authConfig = ConfigFactory.create(AuthConfig.class, System.getProperties());
}
