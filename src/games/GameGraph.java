package games;

import java.util.*;
import model.*;
import java.io.*;

/**
* An explicit implementation of the game graph
* It is an implementation of graph, it uses hashMaps for keeping track of the successors and predecessors of each node,
* it contains useful extra information as the number of nodes and the number of Edges, also it has a distinguished node:
* the error node. 
*  
* @author Luciano Putruele
* @author Pablo Castro
*/
public class GameGraph{

	private HashMap<GameNode, HashSet<GameNode>> succList; // Successors adjacency list
	private HashMap<GameNode, HashSet<GameNode>> preList; // Predecessors adjacency list
	private GameNode initial; // Initial state
	private LinkedList<GameNode> nodes; // States
	private int numNodes;
	private int numEdges;
	private GameNode errState; // Distinguished Error State

	public GameGraph() {
		succList = new HashMap<GameNode, HashSet<GameNode>>();
		preList = new HashMap<GameNode, HashSet<GameNode>>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<GameNode>();
		errState = new GameNode(null,null,new Action("ERR", false, false, false), TPlayer.ERR);
		addNode(errState);

	}

	/**
	 * @return The error state
	 */
	public GameNode getErrState(){		
		return errState;
	}
	
	/**
	 * 
	 * @return the num of nodes
	 */
	public int getNumNodes(){
		return numNodes;
	}

	/**
	 * 
	 * @return the num of edges
	 */
	public int getNumEdges(){
		return numEdges;
	}

	/**
	 * 
	 * @param v the new initial node for the game
	 */
	public void setInitial(GameNode v){
		initial = v;
	}

	/**
	 * 
	 * @return	the initial node
	 */
	public GameNode getInitial(){
		return initial;
	}

	/**
	 * 
	 * @param v	the node to be added as part of the game
	 */
	public void addNode(GameNode v) {
		nodes.add(v);
		succList.put(v, new HashSet<GameNode>());
		preList.put(v, new HashSet<GameNode>());
		numNodes += 1;
	}

	/**
	 * It searches a node in the graph
	 * @param v	the node to be searched
	 * @return	if the node does not belong to the graph it return null otherwise it returns a reference to the node
	 */
	public GameNode search(GameNode v) {
		for (GameNode node : nodes){
			if (node.equals(v))
				return node;
		}
		return null;
	}

	/**
	 * It says if a node belongs to a graph
	 * @param v	the node to be found
	 * @return	true iff the node belongs to the graph
	 */	
	public boolean hasNode(GameNode v) {
		return nodes.contains(v);
	}

	/**
	 * 
	 * @param from	the origin node
	 * @param to	the source node
	 * @return	true iff there is an arc between the nodes
	 */
	public boolean hasEdge(GameNode from, GameNode to) {
		if (!hasNode(from) || !hasNode(to))
			return false;
		return succList.get(from).contains(to);
	}

	/**
	 * Creates a new arc between two given nodes
	 * @param from	the origin of the new node
	 * @param to	the target of the new node
	 */
	public void addEdge(GameNode from, GameNode to) {
		if (to != null){
			if (hasEdge(from, to))
				return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
		}
	}
	
	/**
	 * 
	 * @return	all the node in the graph
	 */
	public LinkedList<GameNode> getNodes(){
		return nodes;
	}

	/**
	 * 
	 * @param v		the node 
	 * @return	all the successors of v
	 */
	public HashSet<GameNode> getSuccessors(GameNode v){
		return succList.get(v);
	}

	/**
	 * 
	 * @param v	the node
	 * @return	all the predecessors of v
	 */
	public HashSet<GameNode> getPredecessors(GameNode v){
		return preList.get(v);
	}

	/**
	 * 
	 * @param name	the name fro the dot
	 * @param debugMode	adds useful information for debugging
	 * @return	a dot representation of the graph
	 */
	public String createDot(String name, boolean debugMode){
		String res = "";
		LinkedList<GameNode> ns;
		if (debugMode){
			ns = new LinkedList<GameNode>();
			ns.add(errState);
			ns.addAll(getPredecessors(errState));
			LinkedList<GameNode> ns_ = new LinkedList<GameNode>();
			for (int i=0; i<ns.size(); i++){
				ns_.addAll(getPredecessors(ns.get(i)));
			}
			ns.addAll(ns_);
		}
		else{
			ns = nodes;
		}
		res = "digraph model {\n\n";
		res += "    node [style=filled];\n";
		for (GameNode v : ns){
			if (v.getPlayer().equals("V"))
				res += "    "+ v.getId() +" [label=\""+v.toStringDot()+"\",color=\"lightblue\"];\n";
			if (v.getPlayer().equals("P"))
				res += "    "+ v.getId() +" [label=\""+v.toStringDot()+"\",color=\"orange\"];\n";
			if (v.getPlayer().equals("R"))
				if (v == initial)
					res += "    "+ v.getId() +" [label=\""+v.toStringDot()+"\",color=\"pink\"];\n";
				else
					res += "    "+ v.getId() +" [label=\""+v.toStringDot()+"\",color=\"grey\"];\n";
			if (v.getPlayer().equals(""))
				res += "    "+v.getId()+" [label=\""+v.toStringDot()+"\",color=\"red\"];\n";
			for (GameNode u : succList.get(v)){
				if (u.getSymbol().isMask())
					res += "    "+v.getId()+" -> "+ u.getId() +" [color=\"green\"]"+";\n";
				else
					if (u.getSymbol().isFaulty())
						res += "    "+v.getId()+" -> "+ u.getId() +" [color=\"red\"]"+";\n";
					else
						if (u.getSymbol().isTau())
							res += "    "+v.getId()+" -> "+ u.getId() + " [style=dashed]"+";\n";
						else
							res += "    "+v.getId()+" -> "+ u.getId() + ";\n";	
			}
		}
		res += "\n}";
		try{
            File file = new File("../out/" + name +".dot");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(res);
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
		return res;
	}
}