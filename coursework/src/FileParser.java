import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class FileParser {

  private List<String> rawData;
  private List<String> lines;

  public FileParser(){
    rawData = new ArrayList<>();
    lines = new ArrayList<>();
  }

  public List<String> getRawData() {
    return rawData;
  }

  public List<String> getLines() {
    return lines;
  }

  public void readData(String filename) throws FileNotFoundException {
    Scanner input = new Scanner(new File(filename));

    while (input.hasNextLine()){
      rawData.add(input.nextLine());
    }

    removeSingleLineComments();
  }

  private void removeSingleLineComments() {
    StringBuilder builder = new StringBuilder();
    boolean write = true;

    for (String line : rawData){
      for (int i=0 ; i<line.length() ; ++i){
        if (line.charAt(i) == '/' && line.charAt(i+1) == '/'){
          write = false;
        }

        if (write){
          builder.append(line.charAt(i));
        }
      }

      if (builder.length() > 0){
        lines.add(builder.toString());
        builder.delete(0, builder.length());
      }
      else{
        lines.add("");
      }

      write = true;
    }
  }

  private void removeMultiLineComments() {
    StringBuilder builder = new StringBuilder();
    boolean write = true;
  }
}
