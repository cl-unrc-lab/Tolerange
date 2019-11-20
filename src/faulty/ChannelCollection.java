package faulty;


import java.util.*;


public class ChannelCollection extends ProgramNode{
	
	
	    LinkedList<Channel> intChannels;
		LinkedList<Channel> boolChannels;
        LinkedList<Channel> enumChannels;
		int maxLengthChannels;
		
		public ChannelCollection(){
			
			intChannels = new LinkedList<Channel>();
			boolChannels = new LinkedList<Channel>();
            enumChannels= new LinkedList<Channel>();
			maxLengthChannels = 0;		
		
		}
		
		
		 /**
	     * Adds a Int channel to the list of channel in the process
	     * @param	chan 	the new channel
	     */
	    public void addIntChannel(Channel chan){
	    	intChannels.add(chan);
	    	if (chan.getSize() > maxLengthChannels){
	    		maxLengthChannels = chan.getSize();
	    	}
	    }
	    
	    
		public void accept(FaultyVisitor v){
		     v.visit(this);			
		}
	    
	    
	    /**
	     * Adds a Bool channel to the list of channel in the process
	     * @param	chan 	the new channel
	     */
	    public void addBoolChannel(Channel chan){
	    	boolChannels.add(chan);
	    	if (chan.getSize() > maxLengthChannels){
	    		maxLengthChannels = chan.getSize();
	    	}
	    }
	    
        /**
        * Adds a Enumerated channel to the list of channel in the process
        * @param	chan 	the new channel
        */
        public void addEnumChannel(Channel chan){
           enumChannels.add(chan);
           if (chan.getSize() > maxLengthChannels){
              maxLengthChannels = chan.getSize();
           }
        }
    
    
    
	    public LinkedList<Channel> getBoolChannels(){
	    	return boolChannels;
	    }

	    public LinkedList<Channel> getIntChannels(){
	    	return intChannels;
	    }
    
        public LinkedList<Channel> getEnumChannels(){
           return enumChannels;
        }


	    public int getNumBoolChannels(){
	    	return boolChannels.size();
	    }
	    
	    public int getNumIntChannels(){
	    	return intChannels.size();
	    }
	    
        public int getNumEnumChannels(){
            return enumChannels.size();
        }
    
       public int getMaxLengthChannels(){
    	   return maxLengthChannels;
       }
	       		
	


}
