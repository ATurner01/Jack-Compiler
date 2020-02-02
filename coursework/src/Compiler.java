import java.io.FileNotFoundException;
import java.io.*;

public class Compiler {

  public static void main(String[] args) throws FileNotFoundException{

    if (args.length != 1){
      System.err.println("Error. Input file required.");
      System.exit(1);
    }

    Lexer input = new Lexer();

    try {
      input.parseData(args[0]);
    }
    catch (FileNotFoundException e){
      e.printStackTrace();
    }

    PrintStream out = new PrintStream(new File("tests/tokens.txt"));

    PrintStream console = System.out;

    System.setOut(out);

    Token t;
    while ((t = input.getNextToken()) != null){
      System.out.println(t);
    }

    System.setOut(console);
  }
}
