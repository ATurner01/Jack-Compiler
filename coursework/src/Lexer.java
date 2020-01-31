import javax.naming.directory.InvalidAttributesException;
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

  /** Different token types used by the lexer. To clarify certain abbreviations:
   * leq - Less than or Equal to
   * geq - Greater than or Equal to
   * neq - Not equal to
   * assignop - Assignment operator
   * addop - Addition operator
   * mulop - Multiplication operator
   * divop - Division operator
   */
  enum  TokenTypes {
    keyword, id, assignop, addop, mulop, divop, subop, num, bool, string,
    character, punctuator, lessthan, greaterthan, leq, geq, neq, equals, not
  }

  private String lexeme;
  private TokenTypes type;

  /**
   * Creates a new token object with the provided lexeme and token name/type
   * @param l the value of the lexeme (also called the token value/expression)
   * @param t the type of the token (e.g. identifier, operator, keyword, etc.)
   */
  public Token(String l, TokenTypes t){
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
  public TokenTypes getType() {
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
  public void setType(TokenTypes t){
    type = t;
  }

  @Override
  public String toString(){
    return "(" + lexeme + ", " + type + ")";
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
   * Returns the value of the next token in the token stream, then removes
   * said token from the stream itself.
   * @return token
   */
  public Token getNextToken() {
    if (tokens.size() == 0){
      return null;
    }

    Token toReturn = tokens.get(0);
    tokens.remove(0);
    return toReturn;
  }

  /**
   * Returns the next token in the token stream without removing the token
   * from the stream itself.
   * @return token
   */
  public Token peekNextToken() {
    if (tokens.size() == 0){
      return null;
    }

    return tokens.get(0);
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
    tokenizer();
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

  private void tokenizer() {
    StringBuilder lexeme = new StringBuilder();

    for (String line : lines){
      int i=0;
      while (i<line.length()) {
        char firstChar = line.charAt(i);
        if (Character.isLetter(firstChar)) {
          while (i < line.length() && (Character.isLetter(line.charAt(i)) || Character.isDigit(line.charAt(i)))) {
            lexeme.append(line.charAt(i));
            i++;
          }
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.id));
        }
        else if (firstChar == '(' || firstChar == ')' || firstChar == '{' || firstChar == '}' ||
                firstChar == ';') {
          lexeme.append(firstChar);
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.punctuator));
        }
        else if (firstChar == '"') {
          lexeme.append(firstChar);
          i++;
          while (line.charAt(i) != '"') {
            lexeme.append(line.charAt(i));
            i++;
          }
          lexeme.append(line.charAt(i));
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.string));
        }
        lexeme.delete(0, lexeme.length());
        i++;
      }
    }
  }
}
