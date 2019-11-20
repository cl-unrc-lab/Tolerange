package faulty;

public class ChanAssign extends Code{
	
	
    Channel chanName; //  channel name
    Expression exp; 
    
    
    /**
     * Basic constructor.
     * @param	channel 	the channel in which the value is inserted
     * @param	exp			the expression whose value is put into de channel
     */
    public ChanAssign(String chanName, Expression exp){
    	this.chanName = new Channel(chanName);
    	this.exp = exp;
    	
    }
    

    @Override
	public void accept(FaultyVisitor v){
	     v.visit(this);			
	}
}
