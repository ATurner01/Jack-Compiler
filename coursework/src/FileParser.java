import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to parse plain text from a Jack source file.
 *
 * @author Adam Turner
 */
class FileParser {

  private List<String> rawData;
  private List<String> lines;

  /**
   * Create a new FileParser object with no stored data.
   */
  public FileParser(){
    rawData = new ArrayList<>();
    lines = new ArrayList<>();
  }

  /**
   * Returns the current state of the rawData field.
   * @return rawData
   */
  public List<String> getRawData() {
    return rawData;
  }

  /**
   * Returns the current state of the getLines field.
   * @return lines
   */
  public List<String> getLines() {
    return lines;
  }

  /**
   * Reads the data stored within the provided file and removes the comments.
   * @param filename The name of the file to be read
   * @throws FileNotFoundException Throws the checked FileNotFoundException
   * from the IO library.
   */
  public void readData(String filename) throws FileNotFoundException {
    Scanner input = new Scanner(new File(filename));

    while (input.hasNextLine()){
      rawData.add(input.nextLine());
    }

    removeComments(); //Remove all the comments from the file
  }

  private void removeComments() {
    StringBuilder builder = new StringBuilder();
    boolean write = true, singleLineComment = false, multiLineComment = false;

    for (String line : rawData){
      for (int i=0 ; i<line.length()-1 ; ++i){
        if (line.charAt(i) == '/' && line.charAt(i+1) == '/'){
          singleLineComment = true;
        }
        else if (line.charAt(i) == '/' && line.charAt(i+1) == '*'){
          multiLineComment = true;
        }
        else if (line.charAt(i) == '*' && line.charAt(i+1) == '/'){
          multiLineComment = false;
        }

        //If we detect either a single or multi-line comment, do not write chars
        if (write && (singleLineComment || multiLineComment)){
          write = false;
        }

        if (write){
          builder.append(line.charAt(i));

          //Stops the final character from being trimmed from the StringBuilder
          if (i == line.length()-2){
            builder.append(line.charAt(line.length()-1));
          }
        }
      }

      if (builder.length() > 0){
        lines.add(builder.toString());
        builder.delete(0, builder.length()); //Reset the StringBuilder
      }
      else{
        //If the line was entirely a comment, add an empty line to preserve
        // the spacing in the original file
        lines.add("");
      }

      write = true;
      if (singleLineComment) {
        singleLineComment = false;
      }
    }
  }
}
