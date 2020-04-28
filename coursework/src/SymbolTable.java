import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent a symbol within the symbol table
 *
 * @author Adam Turner
 */
class Symbol{

  private String symbolName;
  private String type;
  private String kind;
  private String belongs;
  private int offset;

  /**
   * Creates a new symbol object with the supplied parameters
   * @param n the name of the symbol
   * @param t the type that the symbol represents
   * @param k the kind that the symbol represents
   * @param o the offset of the symbol in memory
   */
  public Symbol(String n, String t, String k, String b, int o){
    symbolName = n;
    type = t;
    kind = k;
    belongs = b;
    offset = o;
  }

  /**
   * Returns the name of the symbol
   * @return symbolName
   */
  public String getSymbolName() {
    return symbolName;
  }

  /**
   * Returns the type of the symbol
   * @return type
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the kind of the symbol
   * @return kind
   */
  public String getKind() {
    return kind;
  }

  /**
   * Returns the class which the symbol belongs to
   * @return belongs
   */
  public String getBelongs() {
    return belongs;
  }

  /**
   * Returns the offset of the symbol in memory
   * @return offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Returns a string representation of a symbol object
   * @return the name and offset of the symbol
   */
  @Override
  public String toString(){
    return symbolName + ", type: " + type + ", kind: " + kind + ", offset = " + offset + ", class = " + belongs;
  }
}


/**
 * A class to represent a symbol table in the for the JACK language
 *
 * @author Adam Turner
 */
public class SymbolTable {

  private List<Symbol> table;
  private int offsetCount;
  private String scope;

  /**
   * Creates a new SymbolTable object initialising an emptry list of symbols
   * and setting the global offset to 0
   */
  public SymbolTable(String s){
    table = new ArrayList<>();
    offsetCount = 0;
    scope = s;
  }

  /**
   * Inserts a new symbol into the symbol table
   * @param name the name of the symbol
   * @param type the type of the symbol
   * @param kind the kind of the symbol
   */
  public void insert(String name, String type, String kind, String  belong){
    Symbol symbol = new Symbol(name, type, kind, belong, offsetCount);
    table.add(symbol);
    offsetCount++;
  }

  /**
   * Checks whether a given symbol is contained within the symbol table
   * @param name the name of the symbol to be found
   * @return true if the given symbol is within the symbol table, otherwise
   * false
   */
  public boolean lookUp(String name){
    for (Symbol s : table){
      if (s.getSymbolName().equals(name)){
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the symbol that possesses the given name if it exists in the
   * symbol table
   * @param name the name of the symbol to be retrieved
   * @return the requested symbol if it exists, else returns null
   */
  public Symbol getSymbol(String name){
    for (Symbol s : table){
      if (s.getSymbolName().equals(name)){
        return s;
      }
    }

    return null;
  }

  /**
   * Checks to see whether the given symbol already exists within the scope
   * of the current symbol table
   * @param name the name of the symbol to be checked
   * @param belongsTo the class which the symbol belongs to
   * @return true if the symbol already exists in this class, else false
   */
  public boolean checkDuplicate(String name, String belongsTo){
    if (this.lookUp(name)){
      Symbol s = this.getSymbol(name);

      if (belongsTo.equals(s.getBelongs())){
        return true;
      }

      return false;
    }

    return false;
  }

  /**
   * A helper function that will add a new table to the a list of symbol
   * tables, and return the index of this new table in the list (i.e. the
   * last element of the list)
   * @param tables a list of symbol table objects
   * @param s the scope of the symbol table to be added
   * @return the index of the last element of the list
   */
  public static int addTable(List<SymbolTable> tables, String s){
    tables.add(new SymbolTable(s));
    return tables.size() - 1;
  }

  /**
   * Returns the list of symbols in the table
   * @return table
   */
  public List<Symbol> getTable() {
    return table;
  }

  /**
   * Returns the offset of the last symbol in the table
   * @return offsetCount
   */
  public int getOffsetCount() {
    return offsetCount;
  }

  /**
   * Returns the scope of the symbol table
   * @return scope
   */
  public String getScope() {
    return scope;
  }

  /**
   * Prints all the symbols currently contained within the symbol table
   */
  public void printTable(){
    for (Symbol s : table){
      System.out.println(s.toString());
    }
  }
}
