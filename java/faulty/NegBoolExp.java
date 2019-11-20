package faulty;



/**
 * A class that represent the negation of a boolean expression
 */
public class NegBoolExp extends Expression{
   
	Expression exp; // the expresison to be negated
    
    /**
     * Constructor of the class
     * @param	exp	the expression in the negation
     */
    public NegBoolExp(Expression exp){
    
        this.exp = exp;
    }
    
       
    @Override
	public void accept(FaultyVisitor v){
	     v.visit(this);			
	}

    public Expression getExp(){
        return exp;
    }
}
