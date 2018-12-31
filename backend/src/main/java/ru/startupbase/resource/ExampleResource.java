package ru.startupbase.resource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import ru.startupbase.entity.Example;
import ru.startupbase.service.ExampleService;

@Path("/")
@Singleton
public class ExampleResource {

  private final ExampleService exampleService;

  public ExampleResource(ExampleService exampleService) {
    this.exampleService = exampleService;
  }

  @GET
  @Path("/test")
  public String index() {
    Example test = exampleService.getRecordById(1);

    return test.toString();
  }
}
