import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple class to represent a token.
 *
 * @author Adam Turner
 */
class Token{

  private String lexeme;
  private String type;

  /**
   * Creates a new token object with the provided lexeme and token name/type
   * @param l the value of the lexeme (also called the token value/expression)
   * @param t the type of the token (e.g. identifier, operator, keyword, etc.)
   */
  public Token(String l, String t){
    lexeme = l;
    type = t;
  }

  /**
   * Returns the current value of the lexeme field.
   * @return lexeme
   */
  public String getLexeme() {
    return lexeme;
  }

  /**
   * Returns the current value of the type field.
   * @return type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value of the lexeme field to the given value l.
   * @param l The new value of lexeme
   */
  public void setLexeme(String l){
    lexeme = l;
  }

  /**
   * Sets the value of type to the given value t.
   * @param t The new value of type
   */
  public void setType(String t){
    type = t;
  }
}




/**
 * A class to parse plain text from a Jack source file.
 *
 * @author Adam Turner
 */
public class Lexer {

  private List<String> rawData;
  private List<String> lines;
  private List<Token> tokens;

  /**
   * Create a new Lexer object with no stored data.
   */
  public Lexer(){
    rawData = new ArrayList<>();
    lines = new ArrayList<>();
    tokens = new ArrayList<>();
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
   * Returns the current state of the tokens field.
   * @return tokens
   */
  public List<Token> getTokens() {
    return tokens;
  }

  /**
   * Reads the data stored within the provided file and removes the comments.
   * @param filename The name of the file to be read
   * @throws FileNotFoundException Throws the checked FileNotFoundException
   * from the IO library.
   */
  public void parseData(String filename) throws FileNotFoundException {
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

  private void tokenizer(String expression) {

  }
}
