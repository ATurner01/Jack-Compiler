import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A class that takes tokens from an input stream and checks they are valid
 * in the JACK language
 * @author Adam Turner
 */
public class Parser {

  private Lexer lexer;
  private List<SymbolTable> symbolTables;
  private int currSymbolTable;
  private String className;
  private String currScope;
  private String operandType;
  private String identifierOperand = null;
  private int numLocalVars;
  private boolean isFunctionWritten;
  private String returnType;
  private List<String> vmCode;


  /**
   * Declares a new Parser object that reads input from a file through a
   * parser before checking the grammar of the source code
   * @param file The JACK source code file
   * @throws FileNotFoundException Thrown if the given source file does not
   * exist
   */
  public Parser(String file) throws FileNotFoundException{
    lexer = new Lexer();
    vmCode = new ArrayList<>();

    symbolTables = new ArrayList<>();
    symbolTables.add(new SymbolTable("global")); // Initialise global symbol
    // table
    currSymbolTable = 0; // Set current symbol table as global symbol table

    initialiseGlobalTable();

    lexer.parseData(file);
    parse();


    int fileExtensionIndex = file.lastIndexOf(".");
    String filename = file.substring(0, fileExtensionIndex);

    try {
      writeCode(filename);
    }
    catch (IOException e){
      System.out.println("Error. Could not open VM file.");
      e.printStackTrace();
    }
  }

  /**
   * Initialises the global symbol table with the JACK libraries
   * @throws FileNotFoundException thrown if the file containing all the JACK
   * library declarations is missing
   */
  private void initialiseGlobalTable() throws FileNotFoundException{
    File libs = new File("libs.txt");
    Scanner input = new Scanner(libs);

    while(input.hasNextLine()){
      String line = input.nextLine();
      String[] entry = line.split(",");
      if (entry[3].equals("null")){
        symbolTables.get(0).insert(entry[0], entry[1], entry[2], null);
      }
      else {
        symbolTables.get(0).insert(entry[0], entry[1], entry[2], entry[3]);
      }
    }
  }

  /**
   * Adds the generated VM code to an array list
   * @param code the line of VM code to be added
   */
  private void storeCode(String code){
    vmCode.add(code);
  }

  /**
   * Writes the VM code contained within the vmCode array to a VM file
   * @param filename The name of the VM file being written to
   * @throws IOException thrown in the event that the VM file cannot be
   * created or accessed
   */
  private void writeCode(String filename) throws IOException {
    PrintWriter vmFile = new PrintWriter(new FileWriter(new File(filename +
            ".vm")));

    for (String line : vmCode){
      vmFile.println(line);
    }

    vmFile.close();
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
      if (symbolTables.get(currSymbolTable).lookUp(t.getLexeme())){
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Class " + t.getLexeme() + " is already defined.");
      }
      className = t.getLexeme();
      symbolTables.get(currSymbolTable).insert(t.getLexeme(), "None",
              "class", null);
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
    String kind;
    String type;
    if (t.getLexeme().equals("static")){
      type = type();
      kind = "static";
    }
    else if (t.getLexeme().equals("field")){
      type = type();
      kind = "field";
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \"static\" or \"field\", got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){
      if (symbolTables.get(currSymbolTable).lookUp(t.getLexeme())){
        if (symbolTables.get(currSymbolTable).checkDuplicate(t.getLexeme(),
                className)) {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Class Variable " + t.getLexeme() + " is already defined.");
        }
      }
      symbolTables.get(currSymbolTable).insert(t.getLexeme(), type, kind,
              className);
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
        if (symbolTables.get(currSymbolTable).lookUp(t.getLexeme())){
          if (symbolTables.get(currSymbolTable).checkDuplicate(t.getLexeme(),
                  className)) {
            throw new ParserException("Error on line " + t.getLineNum() + ". " +
                    "Class Variable " + t.getLexeme() + " is already defined.");
          }
        }
        symbolTables.get(currSymbolTable).insert(t.getLexeme(), type, kind,
                className);
      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected identifier, got " + t.getLexeme() + ".");
      }

      t = lexer.peekNextToken();
    }
    lexer.getNextToken();

  }

  private String type(){
    Token t = lexer.getNextToken();

    if (t.getLexeme().equals("int")){
      return "int";
    }
    else if (t.getLexeme().equals("char")){
      return "char";
    }
    else if (t.getLexeme().equals("boolean")){
      return "boolean";
    }
    else if (t.getType() == Token.TokenTypes.id){
      return t.getLexeme();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected variable type, got " + t.getLexeme() + ". ");
    }
  }

  private void subroutineDeclare(){
    Token t = lexer.getNextToken();
    String kind;
    String type;
    returnType = null;
    if (t.getLexeme().equals("constructor")){
      kind = "constructor";
    }
    else if (t.getLexeme().equals("function")){
      kind = "function";
    }
    else if (t.getLexeme().equals("method")){
      kind = "method";
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected function declaration, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getLexeme().equals("void")){
      lexer.getNextToken();
      type = "void";
    }
    else if (t.getType() == Token.TokenTypes.keyword || t.getType() == Token.TokenTypes.id){
      type = type();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected type, got " + t.getLexeme() + ".");
    }

    returnType = type;
    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){
      if (symbolTables.get(currSymbolTable).lookUp(t.getLexeme())){
        if (symbolTables.get(currSymbolTable).checkDuplicate(t.getLexeme(),
                className)) {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Function or method " + t.getLexeme() + " is already defined.");
        }
      }
      symbolTables.get(currSymbolTable).insert(t.getLexeme(), type, kind,
              className);
      currScope = t.getLexeme();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected identifier, got " + t.getLexeme() + ".");
    }

    // Creates a new symbol table for the current function/method
    currSymbolTable = SymbolTable.addTable(symbolTables, currScope);
    // Adds the 'this' argument that is present in every JACK method/function
    symbolTables.get(currSymbolTable).insert("this", className, "argument",
            className);

    t = lexer.getNextToken();
    if (t.getLexeme().equals("(")){
      t = lexer.peekNextToken();
      while (!(t.getLexeme().equals(")"))){
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

    // Return to the global symbol table
    currSymbolTable = 0;
  }

  private void paramList(){
    Token t = lexer.peekNextToken();
    String type;
    if (t.getType() == Token.TokenTypes.keyword || t.getType() == Token.TokenTypes.id){
      type = type();

      t = lexer.getNextToken();
      if (t.getType() == Token.TokenTypes.id){
        if (symbolTables.get(currSymbolTable).lookUp(t.getLexeme())){
          if (symbolTables.get(currSymbolTable).checkDuplicate(t.getLexeme(),
                  className)) {
            throw new ParserException("Error on line " + t.getLineNum() + ". " +
                    "Argument " + t.getLexeme() + " is already defined.");
          }
        }
        symbolTables.get(currSymbolTable).insert(t.getLexeme(), type,
                "argument", className);
      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected identifier, got " + t.getLexeme() + ".");
      }

      t = lexer.peekNextToken();
      while (t.getLexeme().equals(",")){
        lexer.getNextToken();
        type = type();

        t = lexer.getNextToken();
        if (t.getType() == Token.TokenTypes.id){
          if (symbolTables.get(currSymbolTable).lookUp(t.getLexeme())){
            if (symbolTables.get(currSymbolTable).checkDuplicate(t.getLexeme(),
                    className)) {
              throw new ParserException("Error on line " + t.getLineNum() + ". " +
                      "Argument " + t.getLexeme() + " is already defined.");
            }
          }
          symbolTables.get(currSymbolTable).insert(t.getLexeme(), type,
                  "argument", className);
        }
        else {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Expected identifier, got " + t.getLexeme() + ".");
        }

        t = lexer.peekNextToken();
      }
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

    numLocalVars = 0;
    isFunctionWritten = false;
    t = lexer.peekNextToken();
    while (!(t.getLexeme().equals("}"))){
      statement();
      t = lexer.peekNextToken();
    }
    lexer.getNextToken();
  }

  private void statement(){
    Token t = lexer.peekNextToken();

    if (!(t.getLexeme().equals("var"))) {
      if (!isFunctionWritten) {
        storeCode("function " + currScope + " " + numLocalVars);
        isFunctionWritten = true;
      }
    }

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
    String type;
    if (t.getLexeme().equals("var")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected var, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getType() == Token.TokenTypes.keyword || t.getType() == Token.TokenTypes.id){
      type = type();
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected type, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){
      if (symbolTables.get(currSymbolTable).lookUp(t.getLexeme())){
        if (symbolTables.get(currSymbolTable).checkDuplicate(t.getLexeme(),
                className)) {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Local variable " + t.getLexeme() + " is already defined.");
        }
      }
      symbolTables.get(currSymbolTable).insert(t.getLexeme(), type,
              "var", className);
      numLocalVars++;
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
        if (symbolTables.get(currSymbolTable).lookUp(t.getLexeme())){
          if (symbolTables.get(currSymbolTable).checkDuplicate(t.getLexeme(),
                  className)) {
            throw new ParserException("Error on line " + t.getLineNum() + ". " +
                    "Local variable " + t.getLexeme() + " is already defined.");
          }
        }
        symbolTables.get(currSymbolTable).insert(t.getLexeme(), type,
                "var", className);
        numLocalVars++;
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
    Symbol lhs;
    boolean isArray = false;
    if (t.getLexeme().equals("let")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected let, got " + t.getLexeme() + ".");
    }

    t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.id){
      if (!(symbolTables.get(currSymbolTable).lookUp(t.getLexeme()))){
        if (!(symbolTables.get(0).lookUp(t.getLexeme())))
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Local variable " + t.getLexeme() + " is not defined.");
      }
      lhs = symbolTables.get(currSymbolTable).getSymbol(t.getLexeme());
      if (lhs == null){
        lhs = symbolTables.get(0).getSymbol(t.getLexeme());
      }
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected identifier, got " + t.getLexeme() + ".");
    }

    t = lexer.peekNextToken();
    if (t.getLexeme().equals("[")){
      isArray = true;

      if (lhs.getKind().equals("var")){
        storeCode("push local " + lhs.getOffset());
      }
      else if (lhs.getKind().equals("argument")){
        storeCode("push argument " + lhs.getOffset());
      }
      else if (lhs.getKind().equals("field")){
        storeCode("push static " + lhs.getOffset());
      }

      lexer.getNextToken();
      t = lexer.peekNextToken();
      while (!(t.getLexeme().equals("]"))){
        expression();
        if (identifierOperand != null){
          Symbol s =
                  symbolTables.get(currSymbolTable).getSymbol(identifierOperand);
          if (!(s.getType().equals("int"))){
            throw new ParserException("Error on line " + t.getLineNum() + ". " +
                    "Array indices must be an integer.");
          }
        }
        else if (!(operandType.equals("int"))){
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Array indices must be integers.");
        }
        t = lexer.peekNextToken();
      }
      storeCode("add");
      storeCode("pop pointer 1");
      lexer.getNextToken();
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("=")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected =, got " + t.getLexeme() + ".");
    }

    identifierOperand = null;
    expression();

    //This section implements type checking for let statements
    Symbol rhs;
    if (identifierOperand != null){
      rhs = symbolTables.get(currSymbolTable).getSymbol(identifierOperand);
      if (rhs == null){
        rhs = symbolTables.get(0).getSymbol(identifierOperand);
      }

      // Checks to see if the type of the local variable is a class (such as
      // an array) that has already been defined in the symbol table
      if (symbolTables.get(0).lookUp(lhs.getType())){

      }
      else if ((rhs != null) && (rhs.getKind().equals("class"))){

      }
      else if ((rhs != null) && !(lhs.getType().equals(rhs.getType()))){
        System.out.println(lhs.toString());
        System.out.println(rhs.toString());
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Mismatched types for " + lhs.getType() + " and " + rhs.getType());
      }
      else if (rhs == null) {
        if (!(lhs.getType().equals(identifierOperand))){
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Mismatched types for " + lhs.getType() + " and " + identifierOperand);
        }
      }

    }
    else {
      if (symbolTables.get(0).lookUp(lhs.getType())){

      }
      else if (!(lhs.getType().equals(operandType))) {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Mismatched types for " + lhs.getType() + " and " + operandType);
      }
    }


    t = lexer.getNextToken();
    if (t.getLexeme().equals(";")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected ; , got " + t.getLexeme() + ".");
    }

    if (isArray){
      storeCode("pop that 0");
    }
    else {
      if (lhs.getKind().equals("var")) {
        storeCode("pop local " + lhs.getOffset());
      } else if (lhs.getKind().equals("field")) {
        storeCode("pop static " + lhs.getOffset());
      } else if (lhs.getKind().equals("argument")) {
        storeCode("pop argument " + lhs.getOffset());
      }
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

    storeCode("if-goto jump");
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

    storeCode("label jump");
    t = lexer.peekNextToken();
    if (t.getLexeme().equals("else")){
      lexer.getNextToken();

      t = lexer.getNextToken();
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
      storeCode("label loop");
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

    expression();

    t = lexer.getNextToken();
    if (t.getLexeme().equals(")")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \")\", got " + t.getLexeme() + ".");
    }

    storeCode("if-goto end");
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

    storeCode("goto loop");
    storeCode("label end");
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
    String subroutine = null;
    int args = 0;
    if (t.getType() == Token.TokenTypes.id){
      subroutine = t.getLexeme();
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
        subroutine = subroutine + "." + t.getLexeme();
      }
      else {
        throw new ParserException("Error on line " + t.getLineNum() + ". " +
                "Expected identifier, got " + t.getLexeme() + ".");
      }
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals("(")){
      args = expressionList();
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

    storeCode("call " + subroutine + " " + args);
  }

  private int expressionList(){
    Token t = lexer.peekNextToken();
    int args = 0;
    if (t.getType() == Token.TokenTypes.num || t.getType() == Token.TokenTypes.id ||
        t.getLexeme().equals("(") || t.getType() == Token.TokenTypes.string ||
        t.getLexeme().equals("true") || t.getLexeme().equals("false") || t.getType() == Token.TokenTypes.nullReference ||
        t.getLexeme().equals("this") || t.getLexeme().equals("-") || t.getLexeme().equals("~")) {

      expression();
      args++;

      t = lexer.peekNextToken();
      while (t.getLexeme().equals(",")) {
        lexer.getNextToken();
        expression();
        args++;
        t = lexer.peekNextToken();
      }
    }

    return args;
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
    if (t.getLexeme().equals("-") || t.getLexeme().equals("~") || t.getLexeme().equals("this") ||
            t.getType() == Token.TokenTypes.num || t.getType() == Token.TokenTypes.id ||
            t.getType() == Token.TokenTypes.string || t.getType() == Token.TokenTypes.nullReference ||
            t.getType() == Token.TokenTypes.bool) {

      identifierOperand = null;
      expression();

      //Check whether the value being returned matches the return type of the
      // subroutine
      if (identifierOperand != null){
        //Lookup the identifier in the local symbol table first before
        // checking whether it exists in the global scope
        Symbol value =
                symbolTables.get(currSymbolTable).getSymbol(identifierOperand);
        if (value == null){
          value = symbolTables.get(0).getSymbol(identifierOperand);
        }

        if (value != null){
          if (value.getType().equals(returnType)){

          }
          else {
            throw new ParserException("Error on line " + t.getLineNum() + ". " +
                    "Return type should be " + returnType + ", not " + identifierOperand + ".");
          }
        }
        else{
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Variable " + identifierOperand + " is not defined.");
        }
      }
      else {
        if (!(returnType.equals(operandType))){
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Return type should be " + returnType + ", not " + operandType + ".");
        }
      }
    }
    else {
      storeCode("push constant 0");
    }

    t = lexer.getNextToken();
    if (t.getLexeme().equals(";")){

    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected \";\", got " + t.getLexeme() + ".");
    }

    //Checks to see if there is any unreachable code after a return statement
    // by taking advantage of the fact the next token should be a "}"
    t = lexer.peekNextToken();
    if (t != null){
      if (!(t.getLexeme().equals("}"))){
        throw new ParserException("Error on line " + t.getLineNum() + ". Code" +
                " after return statement is unreachable.");
      }
    }

    storeCode("return");
  }

  private void expression(){
    relationalExpression();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals("&") || t.getLexeme().equals("|")){
      t = lexer.getNextToken();
      relationalExpression();

      if (t.getLexeme().equals("&")){
        storeCode("and");
      }
      else {
        storeCode("or");
      }

      t = lexer.peekNextToken();
    }
  }

  private void relationalExpression(){
    arithmeticExpression();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals("=") || t.getLexeme().equals("<") || t.getLexeme().equals(">")){
      t = lexer.getNextToken();
      arithmeticExpression();

      if (t.getLexeme().equals("=")){
        storeCode("eq");
      }
      else if (t.getLexeme().equals("<")){
        storeCode("lt");
      }
      else {
        storeCode("gt");
      }

      t = lexer.peekNextToken();
    }
  }

  private void arithmeticExpression(){
    term();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals("+") || t.getLexeme().equals("-")){
      t = lexer.getNextToken();
      term();

      if (t.getLexeme().equals("+")){
        storeCode("add");
      }
      else{
        storeCode("sub");
      }

      t = lexer.peekNextToken();
    }
  }

  private void term(){
    factor();

    Token t = lexer.peekNextToken();
    while (t.getLexeme().equals("*") || t.getLexeme().equals("/")){
      t = lexer.getNextToken();
      factor();

      if (t.getLexeme().equals("*")){
        storeCode("call Math.multiply 2");
      }
      else {
        storeCode("call Math.divide 2");
      }

      t = lexer.peekNextToken();
    }
  }

  private void factor(){
    Token t = lexer.peekNextToken();
    boolean negative = false;
    boolean not = false;
    if (t.getLexeme().equals("-")){
      t = lexer.getNextToken();
      negative = true;
    }
    else if (t.getLexeme().equals("~")){
      t =lexer.getNextToken();
      not = true;
    }

    operand();

    // Turns the last integer constant pushed into a negative value or
    // applied the not operation to a boolean expression
    if (negative){
      storeCode("neg");
    }
    else if (not){
      storeCode("not");
    }
  }

  private void operand(){
    Token t = lexer.getNextToken();
    if (t.getType() == Token.TokenTypes.num){
      operandType = "int";
      storeCode("push constant " + t.getLexeme());
    }
    else if (t.getType() == Token.TokenTypes.id){
      identifierOperand = t.getLexeme();
      String functionName = identifierOperand;

      t = lexer.peekNextToken();
      if (t.getLexeme().equals(".")){
        lexer.getNextToken();
        t = lexer.peekNextToken();
        if (t.getType() == Token.TokenTypes.id){
          t = lexer.getNextToken();
          functionName = functionName + "." + t.getLexeme();
        }
        else {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Expected identifier, got " + t.getLexeme() + ".");
        }
      }

      t = lexer.peekNextToken();
      if (t.getLexeme().equals("[")){

        if (symbolTables.get(currSymbolTable).lookUp(identifierOperand)){
          Symbol s = symbolTables.get(currSymbolTable).getSymbol(identifierOperand);
          if (s.getKind().equals("argument")){
            storeCode("push argument " + s.getOffset());
          }
          else if (s.getKind().equals("var")){
            storeCode("push local " + s.getOffset());
          }
        }
        else if (symbolTables.get(0).lookUp(identifierOperand)){
          Symbol s = symbolTables.get(0).getSymbol(identifierOperand);
          storeCode("push static " + s.getOffset());
        }
        else {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Array is not defined.");
        }

        lexer.getNextToken();
        expression();

        t = lexer.getNextToken();
        if (t.getLexeme().equals("]")){
          storeCode("add");
          return;
        }
        else {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Expected \"]\", got " + t.getLexeme() + ".");
        }
      }

      t = lexer.peekNextToken();
      if (t.getLexeme().equals("(")){
        lexer.getNextToken();
        int args = expressionList();

        t = lexer.getNextToken();
        if (t.getLexeme().equals(")")){
          storeCode("call " + functionName + " " + args);
          return;
        }
        else {
          throw new ParserException("Error on line " + t.getLineNum() + ". " +
                  "Expected \")\", got " + t.getLexeme() + ".");
        }
      }

      if (symbolTables.get(currSymbolTable).lookUp(identifierOperand)){
        Symbol s = symbolTables.get(currSymbolTable).getSymbol(identifierOperand);
        int offset = s.getOffset();
        if (s.getKind().equals("argument")){
          storeCode("push argument " + offset);
        }
        else if (s.getKind().equals("var")){
          storeCode("push local " + offset);
        }
        else if (s.getKind().equals("field")){
          storeCode("push static " + offset);
        }
      }
      else if (symbolTables.get(0).lookUp(identifierOperand)){
        Symbol s = symbolTables.get(0).getSymbol(identifierOperand);
        int offset = s.getOffset();
        storeCode("push static " + offset);
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
    else if (t.getType() == Token.TokenTypes.string || t.getType() == Token.TokenTypes.character){
      operandType = "string";
      int stringLen = t.getLexeme().length();
      //Original lexeme includes the start and end quotation mark of the
      // string, so the actual string length is length of lexeme - 2
      storeCode("push constant " + (stringLen-2));
      storeCode("call String.new 1");

      //Start from the first char after the opening quotation mark and end at
      // the char before the closing quotation mark
      for (int i = 1 ; i < t.getLexeme().length()-1; ++i){
        int charValue = (int) t.getLexeme().charAt(i);
        storeCode("push constant " + charValue);
        storeCode("call String.appendChar 1");
      }

    }
    else if (t.getLexeme().equals("true")){
      operandType = "boolean";
      storeCode("push constant 1");
      storeCode("neg");
    }
    else if (t.getLexeme().equals("false")){
      operandType = "boolean";
      storeCode("push constant 0");
    }
    else if (t.getType() == Token.TokenTypes.nullReference){
      operandType = "null";
      storeCode("push constant 0");
    }
    else if (t.getLexeme().equals("this")){
      operandType = className;
      storeCode("push pointer 0");
    }
    else {
      throw new ParserException("Error on line " + t.getLineNum() + ". " +
              "Expected operator, got " + t.getLexeme() + ".");
    }
  }
}
