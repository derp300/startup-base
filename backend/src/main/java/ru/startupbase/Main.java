package ru.startupbase;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.startupbase.antlr4.Java8BaseListener;
import ru.startupbase.antlr4.Java8Lexer;
import ru.startupbase.antlr4.Java8Parser;
import ru.startupbase.config.ProdConfig;
import org.antlr.v4.runtime.CharStreams;

import java.util.*;

import static java.text.MessageFormat.format;
import static ru.startupbase.JettyServer.createServer;

public class Main {

  public static void main(String[] args) {

    class UppercaseMethodListener extends Java8BaseListener {

      private List<String> errors = new ArrayList<>();

      // ... getter for errors
      public List<String> getErrors(){
        return Collections.unmodifiableList(errors);
      }

      @Override
      public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        TerminalNode node = ctx.Identifier();
        String methodName = node.getText();

        if (Character.isUpperCase(methodName.charAt(0))) {
          String error = String.format("Method %s is uppercased!", methodName);
          errors.add(error);
        }
      }
    }

    String javaClassContent = "public class SampleClass { void doSomething(){int a = 4; int b = 5; int c = a + b;} }";
    Java8Lexer java8Lexer = new Java8Lexer(CharStreams.fromString(javaClassContent));

    CommonTokenStream tokens = new CommonTokenStream(java8Lexer);
    Java8Parser parser = new Java8Parser(tokens);
    ParseTree tree = parser.compilationUnit();

    ParseTreeWalker walker = new ParseTreeWalker();
    UppercaseMethodListener listener= new UppercaseMethodListener();

    walker.walk(listener, tree);

    //System.out.println(listener.getErrors().get(0));

    System.out.println(tree.toStringTree(parser));



















    System.out.println("done");
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