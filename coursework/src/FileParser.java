import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class FileParser {

  private List<String> lines;

  public FileParser(){
    lines = new ArrayList<>();
  }

  public List<String> getLines() {
    return lines;
  }

  public void readData(String filename) throws FileNotFoundException {
    Scanner input = new Scanner(new File(filename));

    while (input.hasNextLine()){
      lines.add(input.nextLine());
    }
  }
}
