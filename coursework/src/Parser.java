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
    classDeclare();
  }

  private void classDeclare(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("class")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Excepted class keyword, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected identifier, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("{")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected '{', got " + t.getLexeme() + ".");
    }

    Token next = lexer.peekNextToken();
    while (!next.getLexeme().equals("}")){
      memberDeclare();
      next = lexer.peekNextToken();
    }

    t = lexer.getNextToken();
    if (t != null){

    }
    else {
      throw new ParserException("Error. Expected closing bracket at end of " +
              "file, got nothing.");
    }

    if (t.getLexeme().equals("}")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected '}', got" + t.getLexeme() + ".");
    }
  }

  private void memberDeclare(){
    Token t = lexer.peekNextToken();
    if (t.getLexeme().equals("static") || t.getLexeme().equals("field")){
      classVarDeclare();
    }
    else if (t.getLexeme().equals("constructor") || t.getLexeme().equals(
            "function") || t.getLexeme().equals("method")){
      subroutineDeclare();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected member declaration, got " + t.getLexeme() + ".");
    }
  }

  private void classVarDeclare(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("static")){
      type();
    }
    else if (t.getLexeme().equals("field")){
      type();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"static\" or \"field\", got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected identifier, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    while (t.getLexeme().equals(",")){
      lexer.getNextToken();
      t = lexer.getNextToken();

      if (t.getType() == Token.TokenTypes.id){

      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected identifier, got " + t.getLexeme() + ".");
      }

      t = lexer.peekNextToken();
    }
    lexer.getNextToken();

  }

  private void type(){
    Token t = lexer.getNextToken();

    if (t.getLexeme().equals("int")){

    }
    else if (t.getLexeme().equals("char")){

    }
    else if (t.getLexeme().equals("boolean")){

    }
    else if (t.getType() == Token.TokenTypes.id){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected variable type, got " + t.getLexeme() + ". ");
    }
  }

  private void subroutineDeclare(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("constructor")){

    }
    else if (t.getLexeme().equals("function")){

    }
    else if (t.getLexeme().equals("method")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected function decleration, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getLexeme().equals("void")){
      lexer.getNextToken();
    }
    else if (t.getType() == Token.TokenTypes.keyword){
      type();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected type, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected identifier, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("(")){
      t = lexer.peekNextToken();
      while (!(t.getLexeme().equals(")"))){
        lexer.getNextToken();
        paramList();
        t = lexer.peekNextToken();
      }
      lexer.getNextToken();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"(\", got " + t.getLexeme() + ".");
    }

    subroutineBody();
  }

  private void paramList(){
    Token t = lexer.peekNextToken();
    if (t.getType() == Token.TokenTypes.keyword){
      type();

      t = lexer.getNextToken();
      if (t.getType() == Token.TokenTypes.id){

      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected identifier, got " + t.getLexeme() + ".");
      }

      t = lexer.peekNextToken();
      while (!(t.getLexeme().equals(","))){
        lexer.getNextToken();
        type();

        t = lexer.getNextToken();
        if (t.getType() == Token.TokenTypes.id){

        }
        else {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Expected identifier, got " + t.getLexeme() + ".");
        }

        t = lexer.peekNextToken();
      }
      lexer.getNextToken();
    }

  }

  private void subroutineBody(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("{")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"{\", got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    while (!(t.getLexeme().equals("}"))){
      statement();
      t = lexer.peekNextToken();
    }
    lexer.getNextToken();
  }

  private void statement(){
    Token t = lexer.peekNextToken();
    if (t.getLexeme().equals("var")){
      varDeclareStatement();
    }
    else if (t.getLexeme().equals("let")){
      letStatement();
    }
    else if (t.getLexeme().equals("if")){
      ifStatement();
    }
    else if (t.getLexeme().equals("while")){
      whileStatement();
    }
    else if (t.getLexeme().equals("do")){
      doStatement();
    }
    else if (t.getLexeme().equals("return")){
      returnStatement();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected statement declaration, got " + t.getLexeme() + ".");
    }
  }

  private void varDeclareStatement(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("var")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected var, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getType() == Token.TokenTypes.keyword){
      type();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected type, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected identifier, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    while (t.getLexeme().equals(",")){
      lexer.getNextToken();

      t = lexer.getNextToken();
      if (t.getType() == Token.TokenTypes.id){

      }
      else {
        throw new ParserException("Error on line "+ t.getLineNum() + ". " +
                "Expected identifier, got " + t.getLexeme() + ".");
      }

      t = lexer.peekNextToken();
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals(";")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \";\", got " + t.getLexeme() + ".");
    }
  }

  private void letStatement(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("let")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected let, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected identifier, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getLexeme().equals("[")){
      lexer.getNextToken();
      t = lexer.peekNextToken();
      while (!(t.getLexeme().equals("]"))){
        expression();
        t = lexer.peekNextToken();
      }
      lexer.getNextToken();
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("=")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected =, got " + t.getLexeme() + ".");
    }

    expression();

    t = lexer.getNextToken();
    if (t.getLexeme().equals(";")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected ; , got " + t.getLexeme() + ".");
    }
  }

  private void ifStatement(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("if")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected if, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("(")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"(\", got " + t.getLexeme() + ".");
    }

    expression();

    t = lexer.getNextToken();
    if (t.getLexeme().equals(")")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \")\", got" + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("{")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"{\", got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    while (!(t.getLexeme().equals("}"))){
      statement();
      t = lexer.peekNextToken();
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("}")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"}\", got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getLexeme().equals("else")){
      lexer.getNextToken();

      t = lexer.peekNextToken();
      if (t.getLexeme().equals("{")){

      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected \"{\", got" + t.getLexeme() + ".");
      }

      t = lexer.peekNextToken();
      while (!(t.getLexeme().equals("}"))){
        statement();
        t = lexer.peekNextToken();
      }

      t = lexer.getNextToken();
      if (t.getLexeme().equals("}")){

      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected \"}\", got " + t.getLexeme() + ".");
      }
    }

  }

  private void whileStatement(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("while")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected while, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("(")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"(\", got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    while (!(t.getLexeme().equals(")"))){
      lexer.getNextToken();
      expression();
      t = lexer.peekNextToken();
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals(")")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \")\", got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("{")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"{\", got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    while (!(t.getLexeme().equals("}"))){
      statement();
      t = lexer.peekNextToken();
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("}")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"}\", got " + t.getLexeme() + ".");
    }
  }

  private void doStatement(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("do")){
      subroutineCall();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected do, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals(";")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \";\", got " + t.getLexeme() + ".");
    }
  }

  private void subroutineCall(){
    Token t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected identifier, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getLexeme().equals(".")){
      lexer.getNextToken();

      t = lexer.getNextToken();
      if (t.getType() == Token.TokenTypes.id){

      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected identifier, got " + t.getLexeme() + ".");
      }
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("(")){
      expressionList();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"(\", got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals(")")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \")\", got " + t.getLexeme() + ".");
    }
  }

  private void expressionList(){
    expression();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals(",")){
      lexer.getNextToken();
      expression();
      t = lexer.peekNextToken();
    }
  }

  private void returnStatement(){
    Token t = lexer.getNextToken();
    if (t.getLexeme().equals("return")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected return, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getLexeme().equals("-") || t.getLexeme().equals("~") ||
            t.getType() == Token.TokenTypes.num || t.getType() == Token.TokenTypes.id ||
            t.getType() == Token.TokenTypes.string || t.getType() == Token.TokenTypes.nullReference) {

      expression();
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals(";")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \";\", got " + t.getLexeme() + ".");
    }
  }

  private void expression(){
    relationalExpression();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals("&") || t.getLexeme().equals("|")){
      lexer.getNextToken();
      relationalExpression();
      t = lexer.peekNextToken();
    }
  }

  private void relationalExpression(){
    arithmeticExpression();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals("=") || t.getLexeme().equals("<") || t.getLexeme().equals(">")){
      lexer.getNextToken();
      arithmeticExpression();
      t = lexer.peekNextToken();
    }
  }

  private void arithmeticExpression(){
    term();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals("+") || t.getLexeme().equals("-")){
      lexer.getNextToken();
      term();
      t = lexer.peekNextToken();
    }
  }

  private void term(){
    factor();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals("*") || t.getLexeme().equals("/")){
      lexer.getNextToken();
      factor();
      t = lexer.peekNextToken();
    }
  }

  private void factor(){
    Token t = lexer.peekNextToken();
    if (t.getLexeme().equals("-")){
      lexer.getNextToken();
    }
    else if (t.getLexeme().equals("~")){
      lexer.getNextToken();
    }

    operand();
  }

  private void operand(){
    Token t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.num){

    }
    else if (t.getType() == Token.TokenTypes.id){
      t = lexer.peekNextToken();
      if (t.getLexeme().equals(".")){
        lexer.getNextToken();

        t = lexer.getNextToken();
        if (t.getType() == Token.TokenTypes.id){
          t = lexer.peekNextToken();
          if (t.getLexeme().equals("[")){
            lexer.getNextToken();
            expression();

            t = lexer.getNextToken();
            if (t.getLexeme().equals("]")){

            }
            else {
              throw new ParserException("Error on line " + t.getLineNum() + ". " +
                      "Expected \"]\", got " + t.getLexeme() + ".");
            }
          }
          else if (t.getLexeme().equals("(")){
            lexer.getNextToken();
            expressionList();

            t = lexer.getNextToken();
            if (t.getLexeme().equals(")")){

            }
            else {
              throw new ParserException("Error on line " + t.getLineNum() +
                      ". Expected \")\", got " + t.getLexeme() + ".");
            }
          }
        }
        else {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Expected identifier, got " + t.getLexeme() + ".");
        }
      }
    }
    else if (t.getLexeme().equals("(")){
      expression();

      t = lexer.getNextToken();
      if (t.getLexeme().equals(")")){

      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected \")\", got " + t.getLexeme() + ".");
      }
    }
    else if (t.getType() == Token.TokenTypes.string){

    }
    else if (t.getLexeme().equals("true")){

    }
    else if (t.getLexeme().equals("false")){

    }
    else if (t.getType() == Token.TokenTypes.nullReference){

    }
    else if (t.getLexeme().equals("this")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected operator, got " + t.getLexeme() + ".");
    }
  }
}
