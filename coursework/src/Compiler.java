import java.io.FileNotFoundException;
import java.util.List;

public class Compiler {

  public static void main(String[] args){
    System.out.println("Hello World!!!");

    FileParser input = new FileParser();

    try {
      input.readData("tests/test_data.txt");
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
