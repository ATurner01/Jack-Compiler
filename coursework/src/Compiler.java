import java.io.FileNotFoundException;
import java.util.List;

public class Compiler {

  public static void main(String[] args){
    System.out.println("Hello World!!!");

    Lexer input = new Lexer();

    try {
      input.parseData("tests/test_data.txt");
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
