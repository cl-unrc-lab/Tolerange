package games;

import java.util.*;
import model.*;

/**
* Explicit Game Graph Node/Vertex, it provides the basic information and behavior corresponding to nodes of a game
*  
* @author Luciano Putruele
*/
public class GameNode implements Comparable<Object>{

	ModelState specState; // Current state of the Specification
	ModelState impState; // Current state of the Implementation
	Action symbol; // The action that lead to this state
	TPlayer player; // The player that has to play from here
	boolean mask; // True if the player has to mask this.symbol
	int id;
	private static int idCounter = 0;
	Double[] values; // for value iteration

	/**
	 * Basic constructor
	 */
	public GameNode(){
		values = new Double[2];
		id = idCounter++;
	}

	/**
	 * Constructor
	 * @param s	the state corresponding to the spec
	 * @param i	the state corresponding to the imp
	 * @param sym	the action played by the las player
	 * @param p	the player that had played
	 */
	public GameNode(ModelState s, ModelState i, Action sym, TPlayer p){
		specState = s;
		impState = i;
		symbol = sym;
		player = p;
		values = new Double[2];
		id = idCounter++;
	}
	/**
	 * 
	 * @return	the spec state
	 */
	public ModelState getSpecState(){
		return specState;
	}

	/**
	 * 
	 * @return	the implementations state
	 */
	public ModelState getImpState(){
		return impState;
	}

	/**
	 * 
	 * @return the symbol in the state
	 */
	public Action getSymbol(){
		return symbol;
	}

	/**
	 * 
	 * @return	the player in the state
	 */
	public TPlayer getPlayer(){
		return player;
	}

	/**
	 * 
	 * @return	if a fault has been masked or not
	 */
	public boolean getMask(){
		return mask;
	}

	/**
	 * 
	 * @return
	 */
	public int getId(){
		return id;
	}

	/**
	 * 
	 * @param m	new value for mask
	 */
	public void setMask(boolean m){
		mask = m;
	}

	/**
	 * 
	 * @return	true iff the node is a Verifier's node
	 */
	public boolean isVerifier(){
		return player.equals(TPlayer.VERIFIER);
	}

	/**
	 * 
	 * @return	true iff the node is a Refuter's node
	 */
	public boolean isRefuter(){
		return player.equals(TPlayer.REFUTER);
	}
	
	/**
	 * 
	 * @return	true iff the node is a Probabilistic node.
	 */
	public boolean isProbabilistic(){
		return player.equals(TPlayer.PROBABILISTIC);
	}
	
	/**
	 * 
	 * @param i	
	 * @param val
	 */
  	public void setValue(int i, double val){
  		values[i] = val;
  	}
  	public Double[] getValues(){
	    return values;
	}

	@Override
	public int compareTo(Object u) {
		if (u instanceof GameNode)
			if (this.equals((GameNode)u))
				return 0;
		return -1;
	}

	@Override
	public int hashCode(){
	    return Objects.hash(specState, impState, symbol, player);
	}


	public String toString(){
		String res;
		if (this.isErrState())
			res = "ERR_STATE";
		else
			res = "SPEC: "+specState.toString()+", SYMBOL: "+(symbol.isFromSpec()?"S":(symbol.getLabel().equals("")?"#":"I"))+symbol.getLabel()+", IMP:"+impState.toString()+", PLAYER: "+player;
		return res;
	}

	public String toString2(){
		String res;
		if (this.isErrState())
			res = "ERR_STATE";
		else
			res = "SPEC: "+", SYMBOL: "+(symbol.isFromSpec()?"S":(symbol.getLabel().equals("")?"#":"I"))+symbol.getLabel()+", IMP:"+", PLAYER: "+player;
		return res;
	}

	
	public String toStringDot(){
		String res;
		if (this.isErrState())
			res = "ERR_STATE";
		else{
			String s = symbol.getLabel().equals("")?"":(symbol.getLabel()+(symbol.isFromSpec()?"_S":"_I"));
			res = "Spec: "+specState.toStringDot()+"\nSymbol: "+s+"\nImp: "+impState.toStringDot()+"\nPlayer: "+player+"\nValue: "+values[0];
		}
		return res;
	}

	/**
	 * 
	 * @return	true iff it is the error state
	 */
	public boolean isErrState(){
		return player.equals(TPlayer.ERR);
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof GameNode){
			GameNode n = (GameNode)o;
			if (this.isErrState() && n.isErrState())
					return true;
			else{
				if (this.isErrState() || n.isErrState())
					return false;
			}
				
			if (specState == n.getSpecState() && impState == n.getImpState() && symbol.equals(n.getSymbol()) && player.equals(n.getPlayer())){
				//System.out.println(this.hashCode()==n.hashCode());
				return true;
			}
			//System.out.println(this.hashCode()==n.hashCode());
		}
		return false;
	}
	
}