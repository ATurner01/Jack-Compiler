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
  private int offset;

  /**
   * Creates a new symbol object with the supplied parameters
   * @param n the name of the symbol
   * @param t the type that the symbol represents
   * @param k the kind that the symbol represents
   * @param o the offset of the symbol in memory
   */
  public Symbol(String n, String t, String k, int o){
    symbolName = n;
    type = t;
    kind = k;
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
    return symbolName + ", offset = " + offset;
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

  /**
   * Creates a new SymbolTable object initialising an emptry list of symbols
   * and setting the global offset to 0
   */
  public SymbolTable(){
    table = new ArrayList<>();
    offsetCount = 0;
  }

  /**
   * Inserts a new symbol into the symbol table
   * @param name the name of the symbol
   * @param type the type of the symbol
   * @param kind the kind of the symbol
   */
  public void insert(String name, String type, String kind){
    Symbol symbol = new Symbol(name, type, kind, offsetCount);
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
   * Prints all the symbols currently contained within the symbol table
   */
  public void printTable(){
    for (Symbol s : table){
      System.out.println(s.toString());
    }
  }
}
