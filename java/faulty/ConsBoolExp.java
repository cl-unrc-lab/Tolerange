package faulty;


import java.util.*;

/**
 * A class that represents a boolean constant, it could be true or false, represented by a 
 * constant BDD
 */
public class ConsBoolExp extends Expression{

    boolean value; // the value
    
    /**
     * Constructor of this class
     * @param value		the value of the constant: true or false
     */
    public ConsBoolExp(boolean value){
    	super();
        this.value = value;
    }
    
    
    /**
     * Returns a list of channels occuring in the expression (empty)
     * return	the empty list
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	return result;
    }
    
    @Override
	public void accept(FaultyVisitor v){
	     v.visit(this);			
	}

    public boolean getValue(){
        return value;
    }
}
