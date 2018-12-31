package ru.startupbase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.startupbase.entity.Example;
import ru.startupbase.resource.ExampleResource;
import ru.startupbase.service.ExampleService;

@Configuration
@Import({ExampleResource.class, ExampleService.class})
public class CommonConfig {
  @Bean
  MappingConfig mappingConfig() {
    return new MappingConfig(
        Example.class
    );
  }
}
