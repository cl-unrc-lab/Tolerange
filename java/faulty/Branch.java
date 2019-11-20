package faulty;

import java.util.*;

public class Branch extends ProgramNode {
	    //BoolExp guard; // the guard of the branch
	    Expression guard; // the guard of the branch
	    LinkedList<Code> assignList;
	    //Code code; // the code of the branch
	    boolean isFaulty; // is the branch faulty?
	    boolean isTau;  // is the branch internal?
	    String label;
	    
	    public Branch(Expression guard,  LinkedList<Code> assignList, boolean isFaulty, boolean isTau){
	    	
	    	this.guard=guard;
	    	this.assignList = assignList;
	    	this.isFaulty = isFaulty;
	    	this.isTau = isTau;
	    	this.label = "";
	    	
	    }

	    public Branch(Expression guard,  LinkedList<Code> assignList, boolean isFaulty, boolean isTau, String label){
	    	
	    	this.guard = guard;
	    	this.assignList = assignList;
	    	this.isFaulty = isFaulty;
	    	this.isTau = isTau;
	    	this.label = label;
	    	
	    }
	    
	    public Expression getGuard(){
	    	return this.guard;
	    }
	    
	    public LinkedList<Code> getAssignList(){
	    	return this.assignList;
	    }
	
	    public boolean getIsFaulty(){
	    	return this.isFaulty;
	    }

	    public boolean getIsTau(){
	    	return this.isTau;
	    }

	    public String getLabel(){
	    	return this.label;
	    }
	    
		public void accept(FaultyVisitor v){
		     v.visit(this);			
		}
}
