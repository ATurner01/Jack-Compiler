/**
 * A custom exception defined to be used in the Lexer class.
 * @author Adam Turner
 */
public class LexerException extends RuntimeException{

  /**
   * Creates a new LexerException object and passes the input string to the
   * parent class.
   * @param s String containing a message to be displayed by the exception
   */
  public LexerException(String s){
    super(s);
  }
}
