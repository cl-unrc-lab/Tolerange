package faulty;


import java.util.*;

/**
 * A class representing an integer constant
 */
// Intcons it represents a constant of type Int
public class ConsIntExp extends IntExp{
    
	Integer value;
    /**
     * Basic constructor for the class, it constructs the number expressed as a BDD
     * @param	i	the integer
     */
    public ConsIntExp(Integer i){
      super();
      value = i;
    }
    

    /**
     * Returns a list of channels occurring in the expression.
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	return result;
    }
    
    @Override
	public void accept(FaultyVisitor v){
	     v.visit(this);			
	}

    public Integer getValue(){
        return value;
    }
}