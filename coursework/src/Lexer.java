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
   */
  enum  TokenTypes {
    keyword, id, assignop, operator, num, bool, string, character, punctuator
    , equals, leq, geq, neq, not, arrayIndex, separator, terminator,
    nullReference, membership
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
    return "<" + lexeme + ", " + type + ">";
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

  private static final String[] KEYWORDS = {
          "function", "do", "let", "class", "int", "boolean", "char",
          "constructor", "method", "void", "var", "static", "field", "if",
          "else", "while", "return", "true", "false", "this"
  };

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
          write = true;

          //Advance the input to skip over the comment symbols.
          //If the new value of i is greater or equal to the length of the line,
          // then move onto the next line
          i = i+2;
          if (i>=line.length()){
            continue;
          }
          builder.append(" "); //Add whitespace to preserve the spacing
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

      if (line.length() == 1 && !(Character.isSpaceChar(line.charAt(0)))){
        builder.append(line.charAt(0)); //Adds lines that are a single character
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
          i--; //Decrement position so we dont consume next token by accident

          String value = lexeme.toString();
          boolean key = false;
          for (String keyword : KEYWORDS){
            if (value.equals(keyword)){
              key = true;
              break; //We've found our key value so terminate the loop
            }
          }

          if (key){
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.keyword));
          }
          else if (value.equals("true") || value.equals("false")){
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.bool));
          }
          else if (value.equals("null")){
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.nullReference));
          }
          else{
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.id));
          }
        }
        else if (firstChar == '(' || firstChar == ')' || firstChar == '{' || firstChar == '}') {
          lexeme.append(firstChar);
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.punctuator));
        }
        else if (firstChar == ';'){
          lexeme.append(firstChar);
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.terminator));
        }
        else if (firstChar == '[' || firstChar == ']'){
          lexeme.append(firstChar);
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.arrayIndex));
        }
        else if (firstChar == '.'){
          lexeme.append(firstChar);
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.membership));
        }
        else if (firstChar == '='){
          i++;
          lexeme.append(firstChar);
          //Check to see if token is assign operator or comparison operator
          if (line.charAt(i) == '='){
            lexeme.append(line.charAt(i));
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.equals));
          }
          else {
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.assignop));
          }
        }
        else if (firstChar == ','){
          lexeme.append(firstChar);
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.separator));
        }
        else if (firstChar == '+' || firstChar == '-' || firstChar == '/' ||
                firstChar == '*' || firstChar == '<' || firstChar == '>' ||
                firstChar == '~' || firstChar == '|' || firstChar == '&'){
          lexeme.append(firstChar);

          //Check to see if operator is followed by an equals. If so, and if
          // starting operator was '<' or '>', token must be comparison operator
          if ((firstChar == '<' && line.charAt(i+1) == '=')){
            lexeme.append(line.charAt(i+1));
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.leq));
            i++;
          }
          else if (firstChar == '>' && line.charAt(i+1) == '='){
            lexeme.append(line.charAt(i+1));
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.geq));
            i++;
          }
          else {
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.operator));
          }
        }
        else if (firstChar == '!'){
          lexeme.append(firstChar);

          //Determine if we have obtained the 'Not' operator or comparison
          if (line.charAt(i+1) == '='){
            lexeme.append(line.charAt(i+1));
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.neq));
            i++;
          }
          else {
            tokens.add(new Token(lexeme.toString(), Token.TokenTypes.not));
          }
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
        else if (firstChar == '\''){
          lexeme.append(firstChar);
          i++;
          while (line.charAt(i) != '\''){
            lexeme.append(line.charAt(i));
            i++;
          }
          lexeme.append(line.charAt(i));
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.character));
        }
        else if (Character.isDigit(firstChar)){
          while (i < line.length() && (Character.isDigit(line.charAt(i)))){
            lexeme.append(line.charAt(i));
            i++;
          }
          i--; //Decrement position so we dont consume next token by accident
          tokens.add(new Token(lexeme.toString(), Token.TokenTypes.num));
        }
        lexeme.delete(0, lexeme.length()); //Empty the string for the lexeme
        i++;
      }
    }
  }
}
