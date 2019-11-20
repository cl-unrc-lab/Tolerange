package faulty;



public class Channel extends ProgramNode {

	
	String name;
	int size;
	Type typeChannel;
	boolean isDeclaration;
    String enumName;
	
	public Channel(String name,int size, Type t){
		this.name = name;
		this.size = size;
		this.typeChannel = t;
		this.isDeclaration= true;
        Type tChan;
        if(t.isEnumerated()){
            this.enumName = new String(t.getStringValue());
            
            tChan= Type.ENUMERATED;
            tChan.setStringValue(t.getStringValue());
            this.typeChannel = tChan;
            //System.out.println("in Channel ---->  declaration " + this.name + " type "+ this.typeChannel.toString() + " size " + this.size+ " -- enumName: " + this.enumName);
            
        }else{
            this.typeChannel = t;
        }
	}
	
	public Channel(String name){
		this.name = name;
		this.size = 0;
		this.typeChannel = Type.UNDEFINED;
		this.isDeclaration=false;
	}
	
	public Channel(String name,boolean isDecl){
		this.name = name;
		this.size = 0;
		this.typeChannel = Type.UNDEFINED;
		this.isDeclaration=isDecl;
	}
	
	
	public Type getType(){
		
		return this.typeChannel;
	}
	
	
    public String getName(){
		
		return this.name;
	}
    
    public int getSize(){
		
		return this.size;
	}
    
    /**
	 *
	 * Set the enumerated name of the variable.
	 */
    public void setEnumName(String n){
		
		this.enumName = n;
	}
    
    /**
     *
     * @return Return the enumerated name of the variable.
     */
    public String getEnumName(){
        return enumName;
    }
    
	
    
    public boolean isDeclaration(){
    	return this.isDeclaration;
    }

    
	public void accept(FaultyVisitor v){
	     v.visit(this);			
	}
	
}
