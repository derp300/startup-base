package ru.startupbase;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.context.ApplicationContext;

class JerseyHandler {

  static Handler createJerseyHandler(ApplicationContext context) {
    final ServletContainer servletContainer = new ServletContainer(createJerseyServletContainer(context));
    final ServletHolder servletHolder = new ServletHolder("mainServlet", servletContainer);

    final ServletHandler servletHandler = new ServletHandler();
    servletHandler.addServletWithMapping(servletHolder, "/*");
    ServletContextHandler servletContextHandler;

    servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    servletContextHandler.setServletHandler(servletHandler);
    return servletContextHandler;
  }

  private static ResourceConfig createJerseyServletContainer(ApplicationContext context) {
    ResourceConfig resourceConfig = new ResourceConfig();
    context.getBeansWithAnnotation(javax.ws.rs.Path.class)
        .forEach((name, resource) -> resourceConfig.register(resource));

    resourceConfig.register(MultiPartFeature.class);
    return resourceConfig;
  }
}
