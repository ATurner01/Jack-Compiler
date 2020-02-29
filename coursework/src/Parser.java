import java.io.FileNotFoundException;

/**
 * A class that takes tokens from an input stream and checks they are valid
 * in the JACK language
 * @author Adam Turner
 */
public class Parser {

  private Lexer lexer;

  /**
   * Declares a new Parser object that reads input from a file through a
   * parser before checking the grammar of the source code
   * @param file The JACK source code file
   * @throws FileNotFoundException Thrown if the given source file does not
   * exist
   */
  public Parser(String file) throws FileNotFoundException{
    lexer = new Lexer();
    lexer.parseData(file);
    parse();
  }

  /**
   * Returns the Lexer object used to tokenize the input data
   * @return lexer
   */
  public Lexer getLexer() {
    return lexer;
  }

  /**
   * Begins the parsing process from the start variable of the JACK grammar
   */
  public void parse(){

  }

  private void classDeclare(){

  }

  private void memberDeclate(){

  }

  private void classVarDeclare(){

  }

  private void type(){

  }

  private void subroutineDeclare(){

  }

  private void paramList(){

  }

  private void subroutineBody(){

  }

  private void statement(){

  }

  private void varDeclareStatement(){

  }

  private void letStatement(){

  }

  private void ifStatement(){

  }

  private void whileStatement(){

  }

  private void doStatement(){

  }

  private void subroutineCall(){

  }

  private void expressionList(){

  }

  private void returnStatement(){

  }

  private void expression(){

  }

  private void relationalExpression(){

  }

  private void arithmeticExpression(){

  }

  private void term(){

  }

  private void factor(){

  }

  private void operand(){

  }
}
