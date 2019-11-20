package faulty;

import java.util.*;

	public class InvkProcess extends ProgramNode {

		String nameProcess;
		LinkedList<Expression> invkValues;
		
		public InvkProcess(String n, LinkedList<Expression> invkV){
			this.nameProcess = n;
			this.invkValues = invkV;
		}
		
        public InvkProcess(String n){
			this.nameProcess = n;
			this.invkValues = new LinkedList<Expression>();
		}
        
		public LinkedList<Expression> getInvkValues(){
			return invkValues;
		}
		
		
	    public String getInstanceName(){
			return nameProcess;
		}
	    
	    
		public void accept(FaultyVisitor v){
		     v.visit(this);			
		}
		
	}
	

