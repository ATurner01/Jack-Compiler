import java.io.FileNotFoundException;
import java.io.*;
import java.util.List;

public class Compiler {

  /**
   * Function to run the Lexer (For testing purposes)
   * @param file The input file containing the source code
   * @throws FileNotFoundException Thrown if the input file cannot be located
   */
  private static void runLexer(String file) throws FileNotFoundException{

    Lexer input = new Lexer();
    input.parseData(file);

    PrintStream out = new PrintStream(new File("tests/tokens.txt"));

    PrintStream console = System.out;

    System.setOut(out);

    Token t;
    while ((t = input.getNextToken()) != null){
      System.out.println(t);
    }

    System.setOut(console);
  }

  public static void main(String[] args) throws FileNotFoundException{

    if (args.length != 1){
      System.err.println("Error. Input file required.");
      System.exit(1);
    }

    Parser parse = new Parser(args[0]);
    Lexer input = parse.getLexer();

    List<String> lines = input.getLines();
    for (String line : lines){
      System.out.println(line);
    }

  }
}
