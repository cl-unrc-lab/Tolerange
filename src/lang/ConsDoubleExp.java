package lang;


/**
 * A class representing an integer constant
 */
public class ConsDoubleExp extends NumExp{
    
	Double value;
    /**
     * Basic constructor for the class, it constructs the number expressed as a BDD
     * @param	i	the integer
     */
    public ConsDoubleExp(Double i){
      super();
      value = i;
    }
    
    @Override
	public void accept(LangVisitor v){
	     v.visit(this);			
	}

    public Double getValue(){
        return value;
    }
}