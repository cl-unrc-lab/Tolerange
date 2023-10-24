package model;

import java.util.*;
import lang.*;
import java.io.*;

/**
 * Represents a model corresponding to a program, it has the basic data for a model, variables, procedures, constants, initial
 * states, etc. After parsing the source code, the obtained programs are translated to MDPs which are captured by this class
 * 
 * @author Luciano Putruele
 *
 */
public class Model {
	private HashMap<ModelState, HashSet<ModelState>> succList; // Succesors adjacency list
	private HashMap<ModelState, HashSet<ModelState>> preList; // Predecessors adjacency list
	private HashMap<Pair, LinkedList<Action>> actions; // Edge actions
	private ModelState initial; // Initial State
	private LinkedList<Var> sharedVars; // Global variables
	private LinkedList<ModelState> nodes; // Global states
	private int numNodes;	// the number of nodes
	private int numEdges;	// the number of edges
	private LinkedList<Proc> procs;	// the processes in the Model
	private LinkedList<String> procDecls;	
	private boolean isWeak;	// if the Model has weak transitions
	private boolean isSpec;	// if it is a specification or not
	private HashMap<String,Object> constants;	// the constants in the model

	//EXPANSION SET
	private HashMap<Triple,Double> probTransitions;

	/**
	 * 
	 * @param svs	the collection of global vars
	 * @param isS	if it is a spec or implementation
	 */
	public Model(GlobalVarCollection svs, boolean isS) {
		sharedVars = svs.getBoolVars();
		sharedVars.addAll(svs.getEnumVars());
		sharedVars.addAll(svs.getIntVars());
		succList = new HashMap<ModelState, HashSet<ModelState>>();
		preList = new HashMap<ModelState, HashSet<ModelState>>();
		actions = new HashMap<Pair, LinkedList<Action>>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<ModelState>();
		procs = new LinkedList<Proc>();
		procDecls = new LinkedList<String>();
		isWeak = false;
		isSpec = isS;
		//EXPANSION SET
		constants = new HashMap<String,Object>();
		probTransitions = new HashMap<Triple,Double>();
	}

	/**
	 * Sets a new initial state
	 * @param v	the initial state
	 */
	public void setInitial(ModelState v){
		initial = v;
	}

	/**
	 * Sets the value for isWeak
	 * @param b the new value for isWeak
	 */
	public void setIsWeak(boolean b){
		isWeak = b;
	}

	/**
	 * 
	 * @return	the initial state
	 */
	public ModelState getInitial(){
		return initial;
	}

	/**
	 * 
	 * @return	the shared vars
	 */
	public LinkedList<Var> getSharedVars(){
		return sharedVars;
	}

	/**
	 * 
	 * @return the constants in saved in a map
	 */
	public HashMap<String,Object> getConstants(){
		return constants;
	}

	/**
	 * 
	 * @return	the actions between two nodes
	 */
	public HashMap<Pair, LinkedList<Action>> getActions(){
		return actions;
	}

	/**
	 * 
	 * @return the list of procedures
	 */
	public LinkedList<Proc> getProcs(){
		return procs;
	}

	/**
	 * 
	 * @return	the list of the declaration of the procedures
	 */
	public LinkedList<String> getProcDecls(){
		return procDecls;
	}

	/**
	 * 
	 * @return	the number of nodes 
	 */
	public int getNumNodes(){
		return numNodes;
	}

	/**
	 * 
	 * @return	the number of edges
	 */
	public int getNumEdges(){
		return numEdges;
	}

	/**
	 * Adds one node to the model	
	 * @param v	the node to be added
	 */
	public void addNode(ModelState v) {
		nodes.add(v);
		succList.put(v, new HashSet<ModelState>());
		preList.put(v, new HashSet<ModelState>());
		numNodes += 1;
	}

	/**
	 * Searchs a node in the model
	 * @param v	the node to be searched
	 * @return	null if the node there is not in the model, the node otherwise
	 */
	public ModelState search(ModelState v) {
		for (ModelState node : nodes){
			if (node.equals(v))
				return node;
		};
		return null;
	}

	/**
	 * It says if a node belongs to the model
	 * @param v	the node to be checked
	 * @return	true iff the node is in the model
	 */
	public boolean hasNode(ModelState v) {
		return nodes.contains(v);
	}
	
	/**
	 * Says if there is an edge between two nodes
	 * @param from	the origin
	 * @param to	the target
	 * @param a		the action in the edge
	 * @return		true iff there is an edge of the corresponding type
	 */
	public boolean hasEdge(ModelState from, ModelState to, Action a) {

		if (!hasNode(from) || !hasNode(to))
			return false;
		Pair transition = new Pair(from,to);
		if (actions.get(transition) == null){
			//System.out.println("a");
			return false;
		}
		

		/*for (Action a_ : actions.get(transition)){
			if (a.getLabel().equals(a_.getLabel())){
				return true;
			}
		}
		System.out.println(actions.get(transition));
		
		return false;*/
		return actions.get(transition).contains(a);
	}

	
	/**
	 * Adds an edge with the provided information
	 * @param from	the origin
	 * @param to	the target
	 * @param a		the action in the edge
	 * @return	true iff the edegewas added to the graph
	 */
	public boolean addEdge(ModelState from, ModelState to, Action a) {
		if (to != null){
			if (a.isTau() && a.getLabel().charAt(0)!='&')
				a.setLabel("&"+a.getLabel());
			if (hasEdge(from, to, a))
				return false;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
			Pair transition = new Pair(from,to);
			if (actions.get(transition) == null){
				actions.put(transition,new LinkedList<Action>());
			}
			actions.get(transition).add(a);
			return true;
		}
		return false;
	}

	//EXPANSION SET
	/**
	 * Adds a probability edge in the model (part of a distribution)
	 * @param from	the origin	
	 * @param to	the target
	 * @param a		the action
	 * @param prob	the probaility of the edge
	 * @return	true iff the edge was added
	 */
	public boolean addProbEdge(ModelState from, ModelState to, Action a, Double prob) {
		if (addEdge(from,to,a)){
			Triple t = new Triple(from,to,a);
			probTransitions.put(t,prob);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param from	 the origin
	 * @param to	 the target
	 * @param a		 the action in the edge
	 * @return	the probability associated to an edge
	 */
	public Double getProb(ModelState from, ModelState to, Action a){
		Double t = probTransitions.get(new Triple(from,to,a));
		return t==null?0.0:probTransitions.get(new Triple(from,to,a));
	}


	/**
	 * 
	 * @return	the nodes in the model
	 */
	public LinkedList<ModelState> getNodes(){
		return nodes;
	}

	/**
	 * 
	 * @param v	a state in the model
	 * @return	the successor of the state
	 */
	public HashSet<ModelState> getSuccessors(ModelState v){
		return succList.get(v);
	}

	/**
	 * 
	 * @param v	the state
	 * @return	the predecessors of the state
	 */
	public HashSet<ModelState> getPredecessors(ModelState v){
		return preList.get(v);
	}

	/**
	 * 
	 * @param isImp	if it is an implementation or a specification
	 * @return	a dot representation of the model
	 */
	public String createDot(boolean isImp){
		String res = "digraph model {\n\n";
		for (ModelState v : nodes){
			//if (v.getIsFaulty())
			//	res += "    STATE"+v.toStringDot()+" [color=\"red\"];\n";
			for (ModelState u : succList.get(v)){
				Pair edge = new Pair(v,u);
				if (actions.get(edge) != null)
					for (int i=0; i < actions.get(edge).size(); i++){
						if (actions.get(edge).get(i).isFaulty())
							res += "    STATE"+v.toStringDot()+" -> STATE"+ u.toStringDot() +" [color=\"red\",label = \""+actions.get(edge).get(i).getLabel()+"\"]"+";\n";
						else
							if (actions.get(edge).get(i).isTau())
								res += "    STATE"+v.toStringDot()+" -> STATE"+ u.toStringDot() +" [style=dashed,label = \""+actions.get(edge).get(i).getLabel()+"\"]"+";\n";
							else
								res += "    STATE"+v.toStringDot()+" -> STATE"+ u.toStringDot() +" [label = \""+actions.get(edge).get(i).getLabel()+"\"]"+";\n";
					}
			}
		}
		res += "\n}";
		try{
			String path = "";
			if (isImp)
            	path ="../out/" + "ImpModel" +".dot";
            else
            	path ="../out/" + "SpecModel" +".dot";
            File file = new File(path);
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

	/**
	 * Saturates the model using the silent/tau transitions, the obtained model does not have silent transitions and
	 * can be used to check bisimulation, for example.
	 */
	public void saturate(){
		//Add tau self-loops
		//if (!isWeak)
		//	return;
		for (ModelState p : nodes){
			addEdge(p,p,new Action("&",false,true,isSpec)); // p -> p is internal
		}
		if (!isWeak)
			return;
		boolean change = true;
		//this lists will share the same size
		LinkedList<ModelState> fsts;
		LinkedList<ModelState> snds;
		LinkedList<String> lbls;
		//LinkedList<Boolean> isFs;
		LinkedList<Boolean> isTaus;


		//Saturate graph
		while (change){
			change = false;
			fsts = new LinkedList<ModelState>();
			snds = new LinkedList<ModelState>();
			lbls = new LinkedList<String>();
			//isFs = new LinkedList<Boolean>();
			isTaus = new LinkedList<Boolean>();

			for (ModelState p : nodes){
				for (ModelState p_ : succList.get(p)){
					Pair t0 = new Pair(p,p_);
					if (actions.get(t0) != null){
						for (int i = 0; i < actions.get(t0).size(); i++){
							if (actions.get(t0).get(i).isTau()){ // p -> p_ is internal
								for (ModelState q_ : succList.get(p_)){
									Pair t1 = new Pair(p_,q_);
									for (int j = 0; j < actions.get(t1).size(); j++){
										String lbl = actions.get(t1).get(j).getLabel();
										Boolean isF = actions.get(t1).get(j).isFaulty();
										Boolean isTau = actions.get(t1).get(j).isTau();
										if (!isF){ //don't saturate faulty actions
											for (ModelState q : succList.get(q_)){
												Pair t2 = new Pair(q_,q);
												if (actions.get(t2) != null){
													for (int k = 0; k < actions.get(t2).size(); k++){
														if (actions.get(t2).get(k).isTau()){ // q_ -> q is internal
															//add transition for later update
															if (!hasEdge(p,q,actions.get(t1).get(j))){
																fsts.add(p);
																snds.add(q);
																lbls.add(lbl);
																isTaus.add(isTau);
																//isFs.add(isF);
																change = true;		
															}	
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			//update transition system
			for (int i = 0; i < fsts.size(); i++){
				//System.out.println(fsts.get(i) + "\n" + snds.get(i) + "\n" + lbls.get(i)+ "\n" + isTaus.get(i) + "\n=========================\n");
				addEdge(fsts.get(i), snds.get(i), new Action(lbls.get(i), false, isTaus.get(i), isSpec));
			}
		}
	
	}

	
}