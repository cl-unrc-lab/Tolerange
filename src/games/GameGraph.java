package games;

import java.util.*;
import model.*;
import java.io.*;

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


	public GameNode getErrState(){		
		return errState;
	}

	public int getNumNodes(){
		return numNodes;
	}

	public int getNumEdges(){
		return numEdges;
	}

	public void setInitial(GameNode v){
		initial = v;
	}

	public GameNode getInitial(){
		return initial;
	}

	public void addNode(GameNode v) {
		nodes.add(v);
		succList.put(v, new HashSet<GameNode>());
		preList.put(v, new HashSet<GameNode>());
		numNodes += 1;
	}

	public GameNode search(GameNode v) {
		for (GameNode node : nodes){
			if (node.equals(v))
				return node;
		}
		return null;
	}

	public boolean hasNode(GameNode v) {
		return nodes.contains(v);
	}


	public boolean hasEdge(GameNode from, GameNode to) {
		if (!hasNode(from) || !hasNode(to))
			return false;
		return succList.get(from).contains(to);
	}


	public void addEdge(GameNode from, GameNode to) {
		if (to != null){
			if (hasEdge(from, to))
				return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
		}
	}

	public LinkedList<GameNode> getNodes(){
		return nodes;
	}

	public HashSet<GameNode> getSuccessors(GameNode v){
		return succList.get(v);
	}

	public HashSet<GameNode> getPredecessors(GameNode v){
		return preList.get(v);
	}

	public String createDot(int lineLimit, String name, boolean debugMode){
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
		int lineCount = 0;
		for (GameNode v : ns){
			//if (lineCount > lineLimit)
			//	break;
			lineCount++;
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