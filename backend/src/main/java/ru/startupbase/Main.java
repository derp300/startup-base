package ru.startupbase;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import static java.text.MessageFormat.format;
import java.util.Arrays;
import java.util.Date;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static ru.startupbase.JettyServer.createServer;
import ru.startupbase.config.ProdConfig;

public class Main {

  public static void main(String[] args) throws IOException {


    DockerClient dockerClient = DockerClientBuilder.getInstance().build();
    CreateContainerResponse container = dockerClient.createContainerCmd("my-python-app")
        .withStdinOpen(true)
        .withTty(true)
        .exec();



    AttachContainerResultCallback attachCallback = new AttachContainerResultCallback() {
      @Override
      public void onNext(Frame frame) {
        System.out.println(frame.toString());
        super.onNext(frame);
      }
    };

    dockerClient.startContainerCmd(container.getId()).exec();






    dockerClient.attachContainerCmd(container.getId())
        .withStdOut(true)
        .withStdErr(true)
        .withLogs(true)
        .withFollowStream(true)
        .exec(attachCallback);




    dockerClient.waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
    //dockerClient.removeContainerCmd(container.getId()).exec();


    System.out.println(" --- programm done ---");
    System.exit(0);
































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