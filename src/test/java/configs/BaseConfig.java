package configs;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties", "classpath:properties/base.properties"})
public interface BaseConfig extends Config {
    @Key("baseUri")
    String baseUri();

    @Key("basePath")
    String basePath();
}
