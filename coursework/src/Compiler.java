import java.io.FileNotFoundException;
import java.util.List;

public class Compiler {

  public static void main(String[] args){

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

    List<String> data = input.getLines();

    for (String line : data){
      System.out.println(line);
    }

  }
}
