package se.vgregion.ifeed;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:${user.home}/.app/ifeed/application.properties", ignoreResourceNotFound = false)
public class AppConfig {

}
