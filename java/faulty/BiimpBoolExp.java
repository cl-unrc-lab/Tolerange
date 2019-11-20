package faulty;


/**
 *  A class to represent the "if and only if" of two boolean expressions
 */
public class BiimpBoolExp extends Expression{
	Expression exp1; // the left expression
	Expression exp2; // the right expression
	

    
    /**
     * Basic constructor for the class
     * @param 	exp1	the left expression
     * @param	epx2	the right expression
     */
    public BiimpBoolExp(Expression exp1, Expression exp2){
    	super();
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    @Override
	public void accept(FaultyVisitor v){
	     v.visit(this);			
	}

    public Expression getExp1(){
        return exp1;
    }

    public Expression getExp2(){
        return exp2;
    }
       
}
