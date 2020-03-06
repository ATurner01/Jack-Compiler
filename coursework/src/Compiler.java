import java.io.FileNotFoundException;
import java.io.*;
import java.util.ArrayList;
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

  /**
   * Checks to see whether the provided file has the .jack extension
   * @param file The path to the source code file
   * @return True if the file has the .jack extension, otherwise false
   */
  public static boolean checkExtension(String file){
    int extensionStart = file.lastIndexOf('.');
    if (extensionStart <= 0){
      System.out.println("Error. File has no extension. Skipping file...");
      return false;
    }

    String extension = file.substring(extensionStart + 1);
    if (!(extension.equals("jack"))){
      System.out.println("Error. File must have the extension '.jack'. " +
              "Skipping file...");
      return false;
    }

    return true;
  }

  public static List<String> getFiles(String dirName){

    File dir = new File(dirName);
    if (!(dir.isDirectory())){
      System.err.println("Error. Input is not a valid directory.");
      System.exit(2);
    }

    File[] listOfFiles = dir.listFiles();
    ArrayList<String> files = new ArrayList<>();

    for (File file : listOfFiles){
      files.add(file.getPath());
    }

    return files;
  }

  public static void main(String[] args) throws FileNotFoundException{

    if (args.length != 1){
      System.err.println("Error. Input file required.");
      System.exit(1);
    }

    System.out.println("Reading in files...");
    List<String> files = getFiles(args[0]);

    for (String file : files) {
      boolean isCorrectExtension = checkExtension(file);

      if (!isCorrectExtension){
        continue;
      }

      System.out.println("Parsing file " + file + "...");
      Parser parse = new Parser(file);
//      Lexer input = parse.getLexer();
//
//      List<String> lines = input.getLines();
//      for (String line : lines) {
//        System.out.println(line);
//      }
      System.out.println("Done.");
    }

    System.out.println("Finished.");

  }
}
