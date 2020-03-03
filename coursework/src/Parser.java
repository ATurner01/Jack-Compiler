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
