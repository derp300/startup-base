package ru.startupbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static ru.startupbase.JettyServer.startJettyServer;
import ru.startupbase.antlr4.Java8BaseListener;
import ru.startupbase.antlr4.Java8Lexer;
import ru.startupbase.antlr4.Java8Parser;
import ru.startupbase.config.ProdConfig;
import org.antlr.v4.runtime.CharStreams;

import static java.text.MessageFormat.format;

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

//    List.of(1).stream().map(i -> i+1).collect(Collectors.toList());

    String javaClassContent = "import java.util.List;" +
        "import java.util.stream.Collectors;" +
        "public class SampleClass {" +
        "void doSomething(){" +
        "List.of(1).stream().map(i -> i+1).collect(Collectors.toList());" +
        "}" +
        "}";
    Java8Lexer java8Lexer = new Java8Lexer(CharStreams.fromString(javaClassContent));

    CommonTokenStream tokens = new CommonTokenStream(java8Lexer);
    Java8Parser parser = new Java8Parser(tokens);
    ParseTree tree = parser.compilationUnit();

    ParseTreeWalker walker = new ParseTreeWalker();
    UppercaseMethodListener listener = new UppercaseMethodListener();

    walker.walk(listener, tree);

    //System.out.println(listener.getErrors().get(0));

    System.out.println(tree.toStringTree(parser));



















    System.out.println("done");
    System.exit(0);









    try {
      startJettyServer(new AnnotationConfigApplicationContext(ProdConfig.class));
    } catch (Exception e) {
      System.err.println(format("[{0}] Failed to start, shutting down: {1}", new Date(), e.getMessage()));
      System.exit(1);
    }
  }
}