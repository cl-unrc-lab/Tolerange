package faulty;


	public class ProcessDecl extends ProgramNode {

		String name;
		String type;
		
		public ProcessDecl(String n, String t){
			this.name = n;
			this.type = t;
		}
		
		public String getType(){
			
			return type;
		}
		
		
	    public String getName(){
			
			return name;
		}
	    
	    
		public void accept(FaultyVisitor v){
		     v.visit(this);			
		}
		
	}
	

