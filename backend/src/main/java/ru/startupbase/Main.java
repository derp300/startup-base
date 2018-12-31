package ru.startupbase;

import static java.text.MessageFormat.format;
import java.util.Arrays;
import java.util.Date;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static ru.startupbase.JettyServer.createServer;
import ru.startupbase.config.ProdConfig;

public class Main {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProdConfig.class);

    try {
      final Server jettyServer = createServer(context);
      final int port = startJettyServer(jettyServer);
      System.out.println("listening to port " + port);
    } catch (Exception e) {
      System.err.println(format("[{0}] Failed to start, shutting down: {1}", new Date(), e.getMessage()));
      System.exit(1);
    }
  }

  private static int startJettyServer(Server jettyServer) throws Exception {
    jettyServer.start();
    return ((ServerConnector) Arrays.stream(jettyServer.getConnectors())
        .filter(a -> a instanceof ServerConnector).findFirst().get())
        .getLocalPort();
  }
}