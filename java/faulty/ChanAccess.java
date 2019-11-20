package faulty;

public class ChanAccess extends Expression{
	
	Channel channel;
	
	public ChanAccess(String name){
		
		this.channel=new Channel(name,false);
	}
	
   public  Channel getChannel(){
		
		return this.channel;
	}

   public  void setChannel(Channel ch){
		
		this.channel = ch;
	}
	
	@Override
	public void accept(FaultyVisitor v){
	     v.visit(this);			
	}

}
