package ru.startupbase;

import java.lang.management.ManagementFactory;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.context.ApplicationContext;
import static ru.startupbase.JerseyHandler.createJerseyHandler;

public class JettyServer {
  static Server createServer(ApplicationContext context) throws Exception {
    final Handler jerseyHandler = createJerseyHandler(context);

    int minThreads = 10;
    int maxThreads = 20;
    int idleTimeoutMs = 60_000;
    QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeoutMs);
    threadPool.start();

    final Server server = new Server(threadPool);
    configureServerConnector(server);
    configureMBeanContainer(server);

    server.setHandler(jerseyHandler);

    server.setStopAtShutdown(true);
    server.setStopTimeout(5_000);

    return server;
  }

  private static void configureServerConnector(Server server) {
    ServerConnector serverConnector = new ServerConnector(server, -1, -1, createHttpConnectionFactory());

    serverConnector.setPort(9999);
    serverConnector.setIdleTimeout(3_000);
    serverConnector.setAcceptQueueSize(50);

    server.addConnector(serverConnector);
  }

  private static HttpConnectionFactory createHttpConnectionFactory() {
    final HttpConfiguration httpConfiguration = new HttpConfiguration();
    httpConfiguration.setSecurePort(8443);
    httpConfiguration.setOutputBufferSize(65536);
    httpConfiguration.setRequestHeaderSize(16384);
    httpConfiguration.setResponseHeaderSize(65536);
    httpConfiguration.setSendServerVersion(false);
    httpConfiguration.setBlockingTimeout(5000);
    return new HttpConnectionFactory(httpConfiguration);
  }

  private static void configureMBeanContainer(Server server) {
    final MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
    server.addEventListener(mbContainer);
    server.addBean(mbContainer);
  }
}
