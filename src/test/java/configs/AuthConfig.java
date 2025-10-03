package configs;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties", "classpath:properties/auth.properties"})
public interface AuthConfig extends Config {
    @Key("api_token")
    String api_token();

    @Key("user_id")
    String user_id();

    @Key("username")
    String username();
}
