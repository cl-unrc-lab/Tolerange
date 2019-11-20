package faulty;




// OrBoolClass a class to represent and boolean expressions
public class OrBoolExp extends Expression{
   
    Expression exp1;
    Expression exp2;
    
    // Constructor for the class
    public OrBoolExp(Expression exp1, Expression exp2){
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
