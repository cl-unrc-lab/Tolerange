package core;

import java.util.*;
import model.*;
import games.*;
import lang.*;

public class MaskingDistance{

    private GameGraph g; // The masking distance game graph, undefined until buildGraph is called
    private boolean noBisimulation = false;
    private Program pSpec;
    private Program pImp;
    private boolean verbose;

    public MaskingDistance(Program specProgram, Program impProgram, boolean deadlockIsError, boolean noBisim, boolean verb) throws InterruptedException{
        noBisimulation = noBisim;
        pSpec = specProgram;
        pImp = impProgram;
        verbose = verb;
        buildGraph(deadlockIsError);
    }

    public GameGraph getG(){
        return g;
    }

    public void buildGraph(boolean deadlockIsError) throws InterruptedException{
        //This method builds a game graph for the Masking Distance Game, there are two players: the Refuter(R) and the Verifier(V)
        //The refuter plays with the implementation(imp), this means choosing any action available (faulty or not)
        //and the verifier plays with the specification(spec), he tries to match the action played by the refuter, if he can't then an error state is reached.
        Model spec,imp;
        System.out.println("Building Models...");
        /*GraphBuilder r1 = new GraphBuilder(specProgram, true);
        GraphBuilder r2 = new GraphBuilder(impProgram, false);
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        t2.start();
        t1.join();
        t2.join();*/
        spec = pSpec.toLTS(true);
        imp = pImp.toLTS(false);
        //spec = r1.getProg();
        //imp = r2.getProg();
        
        //System.out.println("Saturating Models...");
        spec.saturate();
        imp.saturate();
        if (verbose){
            System.out.println("Spec states: "+spec.getNumNodes());
            System.out.println("Spec edges: "+spec.getNumEdges());
            System.out.println("Imp states: "+imp.getNumNodes());
            System.out.println("Imp edges: "+imp.getNumEdges());
        }
        spec.createDot(false);
        imp.createDot(true);
        System.out.println("Building Game Graph...");
        g = new GameGraph();

        //calculate initial state
        GameNode init = new GameNode(spec.getInitial(), imp.getInitial(),new Action("", false, false, false) ,"R");
        g.addNode(init);
        g.setInitial(init);

        TreeSet<GameNode> iterSet = new TreeSet<GameNode>();
        iterSet.add(g.getInitial());

        //build the game graph
        while(!iterSet.isEmpty()){
            GameNode curr = iterSet.pollFirst();
            /*if (deadlockIsError && imp.getSuccessors(curr.getImpState()).size() == 1 && curr.getPlayer() == "V" && !curr.getMask()){ // this is a special deadlock case
                g.addEdge(curr,g.getErrState(),new Action("ERR", false, false, false));
            }*/
            
            if (deadlockIsError && imp.getSuccessors(curr.getImpState()).size() == 1 && curr.getPlayer() == "R"){ // this is a special deadlock case
                    Action preErr = new Action("DEADLOCK_ERR", false, false, false);
                    GameNode curr_ = new GameNode(curr.getSpecState(),curr.getImpState(),preErr,"V");
                    g.addNode(curr_);
                    g.addEdge(curr,curr_,preErr);
                    iterSet.add(curr_);
            }
            if (curr.getPlayer() == "R"){ //if player is refuter we add its possible moves from current state
                //IMP MOVES
                for (ModelState succ : imp.getSuccessors(curr.getImpState())){
                    Pair p = new Pair(curr.getImpState(),succ);
                    if (imp.getActions().get(p) != null){
                        for (int i=0; i < imp.getActions().get(p).size(); i++){
                            GameNode curr_ = new GameNode(curr.getSpecState(),succ,imp.getActions().get(p).get(i), "V");
                            GameNode toOld = g.search(curr_);
                            if (toOld == null){
                                g.addNode(curr_);
                                if (curr_.getSymbol().isFaulty())
                                    curr_.setMask(true); //ahora quedo medio irrelevante el mask
                                g.addEdge(curr,curr_, curr_.getSymbol()); 
                                iterSet.add(curr_);
                            }
                            else{
                                g.addEdge(curr,toOld, toOld.getSymbol());
                            }
                        }
                    }
                }
                //SPEC MOVES
                if (!noBisimulation){
                    for (ModelState succ : spec.getSuccessors(curr.getSpecState())){
                        Pair p = new Pair(curr.getSpecState(),succ);
                        if (spec.getActions().get(p) != null){
                            for (int i=0; i < spec.getActions().get(p).size(); i++){
                                GameNode curr_ = new GameNode(succ,curr.getImpState(),spec.getActions().get(p).get(i), "V");
                                GameNode toOld = g.search(curr_);
                                if (toOld == null){
                                    g.addNode(curr_);
                                    g.addEdge(curr,curr_, curr_.getSymbol()); 
                                    iterSet.add(curr_);
                                }
                                else{
                                    g.addEdge(curr,toOld,toOld.getSymbol()); 
                                }
                            }
                        }
                    }
                }
            }
            else{ //if player is verifier we add its matching move from current state or err transition if can't match
                /*if (spec.getSuccessors(curr.getSpecState()).size() == 1 && spec.getSuccessors(curr.getSpecState()).first() == curr.getSpecState()
                    && curr.getSpecState().getIsFaulty()){
                    g.addEdge(curr,g.getErrState(),"ERR", false);
                }*/
                boolean foundSucc = false;
                //SPEC MOVES
                if (!curr.getSymbol().isFromSpec()){
                    if (curr.getMask()){ //this means the state has to mask a previous fault
                        GameNode curr_ = new GameNode(curr.getSpecState(),curr.getImpState(),new Action("",false,false,false), "R");
                        GameNode toOld = g.search(curr_);
                        if (toOld == null){
                            g.addNode(curr_);
                            g.addEdge(curr,curr_,new Action("M"+curr.getSymbol().getLabel(),false,false,true,true));
                            iterSet.add(curr_);
                        }
                        else{
                            g.addEdge(curr,toOld,new Action("M"+curr.getSymbol().getLabel(),false,false,true,true));
                        }
                        foundSucc = true;
                    }
                    else{
                        for (ModelState succ : spec.getSuccessors(curr.getSpecState())){
                            Pair p = new Pair(curr.getSpecState(),succ);
                            if (spec.getActions().get(p) != null){
                                for (int i=0; i < spec.getActions().get(p).size(); i++){
                                    Action lblImp = curr.getSymbol();
                                    Action lblSpec = spec.getActions().get(p).get(i);
                                    if (lblImp.getLabel().equals(lblSpec.getLabel()) || (lblImp.isTau() && lblSpec.isTau())){
                                        GameNode curr_ = new GameNode(succ,curr.getImpState(), new Action("",false,false,false), "R");
                                        GameNode toOld = g.search(curr_);
                                        if (toOld == null){
                                            g.addNode(curr_);
                                            g.addEdge(curr,curr_, lblSpec); //add label may not be necessary
                                            iterSet.add(curr_);
                                        }
                                        else{
                                            g.addEdge(curr,toOld, lblSpec);
                                        }
                                        foundSucc = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                else{//IMP MOVES
                    if (!noBisimulation){
                        for (ModelState succ : imp.getSuccessors(curr.getImpState())){
                            Pair p = new Pair(curr.getImpState(),succ);
                            if (imp.getActions().get(p) != null){
                                for (int i=0; i < imp.getActions().get(p).size(); i++){
                                    Action lblSpec = curr.getSymbol();
                                    Action lblImp = imp.getActions().get(p).get(i);
                                    if (lblImp.getLabel().equals(lblSpec.getLabel()) || (lblImp.isTau() && lblSpec.isTau())){
                                        GameNode curr_ = new GameNode(curr.getSpecState(),succ, new Action("",false,false,false), "R");
                                        GameNode toOld = g.search(curr_);
                                        if (toOld == null){
                                            g.addNode(curr_);
                                            g.addEdge(curr,curr_, lblImp); //add label may not be necessary
                                            iterSet.add(curr_);
                                        }
                                        else{
                                            g.addEdge(curr,toOld, lblImp);
                                        }
                                        foundSucc = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (!foundSucc){
                    g.addEdge(curr,g.getErrState(),new Action("ERR", false, false, false));
                }
            }
        }
        if (verbose)
            System.out.println("Game graph states: "+g.getNumNodes());
    }

    

   

    public double calculateDistance(){
        System.out.println("Calculating Distance...");

        g.getErrState().setDistanceValue(1);
        g.getErrState().setNumbered(true);
        calculateTempDistance(g.getErrState());

        int minDistance = g.getInitial().getDistanceValue();        
        double res = Math.round((double)1/(minDistance) * Math.pow(10, 3)) / Math.pow(10, 3);
        return res;
    }

    private void calculateTempDistance(GameNode n){
        LinkedList<GameNode> q = new LinkedList<GameNode>();
        q.addFirst(n);
        n.setVisited(true);
        boolean change;
        while (!q.isEmpty()) {
            GameNode v = q.getFirst();
            q.removeFirst();
            for (GameNode adj : g.getPredecessors(v)) {
                change = false;
                int addedCost;
                int m = adj.isVerifier()?maxD(adj):minD(adj);
                addedCost =  (m==Integer.MAX_VALUE?0:(adj.getSymbol().isFaulty()?1:0));
                if (adj.getDistanceValue() != m + addedCost){
                    change = true;
                    adj.setDistanceValue(m + addedCost);
                }
                if (change){
                    adj.setVisited(true);
                    q.addLast(adj);
                }
            }
        }
    }

    private int minD (GameNode n){
        int min = Integer.MAX_VALUE;
        for (GameNode m : g.getSuccessors(n)){
            if (m.getDistanceValue() < min)
                min = m.getDistanceValue();
        }
        return min;
    }

    private int maxD (GameNode n){
        int max = Integer.MIN_VALUE;
        for (GameNode m : g.getSuccessors(n)){
            if (m.getDistanceValue() > max)
                max = m.getDistanceValue();
        }
        return max;
    }

    /*public void calculateDistance3(){
        System.out.println("Calculating Distance...");

        g.getErrState().setDistanceValue(1);
        g.getErrState().setNumbered(true);
        LinkedList<GameNode> q = new LinkedList<GameNode>();
        q.addFirst(g.getErrState());
        g.getErrState().setVisited(true);
        while (!q.isEmpty()) {
            GameNode v = q.getFirst();
            q.removeFirst();
            if (v.equals(g.getInitial())){
                continue;
            }
            for (GameNode adj : g.getPredecessors(v)) {
                int addedCost = (adj.getSymbol().isFaulty()?1:0);
                if (adj.isVerifier()){
                    if (v.getDistanceValue() > adj.getDistanceValue() - addedCost || !adj.isNumbered()){
                        adj.setDistanceValue(v.getDistanceValue() + addedCost);
                        adj.setNumbered(true);
                    }
                }
                else{
                    if (v.getDistanceValue() < adj.getDistanceValue() - addedCost || !adj.isNumbered()){
                        adj.setDistanceValue(v.getDistanceValue() + addedCost);
                        adj.setNumbered(true);
                    }
                }
                if (!adj.getVisited()){
                    adj.setVisited(true);
                    q.addLast(adj);
                }
                //System.out.println(adj.getSymbol().isFaulty());
            }
        }
    }*/
    
    
    public double calculateDistanceBFS(){
        System.out.println("Calculating Distance...");

        g.getErrState().setDistanceValue(1);
        
        LinkedList<GameNode> q = new LinkedList<GameNode>() ;
        q.addFirst(g.getErrState());
        while (!q.isEmpty()) {
            GameNode v = q.getFirst();
            q.removeFirst();
            for (GameNode adj : g.getPredecessors(v)) {
                int w = adj.getSymbol().isFaulty()?1:0;
                if (v.getDistanceValue() + w < adj.getDistanceValue()) {
                    adj.setDistanceValue(v.getDistanceValue() + w);
                    if (w == 1 && !adj.getVisited()){
                        adj.setVisited(true);
                        q.addLast(adj);
                    }
                    else{
                        if (!adj.getVisited()){
                            adj.setVisited(true);
                            q.addFirst(adj);
                        }
                    }
                }
            }
        }

        int minDistance = g.getInitial().getDistanceValue();
        
        double res= Math.round((double)1/(minDistance) * Math.pow(10, 3)) / Math.pow(10, 3);
        return res;
    }

    
    public double calculateDistanceDijsktra(){
        // We use dijsktra's algorithm to find the shortest path to an error state
        System.out.println("Calculating Distance...");

        g.getInitial().setDistanceValue(0);

        for (int count = 0; count < g.getNodes().size(); count++){
            int min = Integer.MAX_VALUE;
            int minIndex = 0;
            GameNode from;
            for (int i = 0;i<g.getNodes().size();i++){
                if (!g.getNodes().get(i).getVisited() && g.getNodes().get(i).getDistanceValue() < min){
                    min = g.getNodes().get(i).getDistanceValue();
                    minIndex = i;
                }
            }
            from = g.getNodes().get(minIndex);
            from.setVisited(true);
            for (GameNode to : g.getSuccessors(from)){
                if (!to.getVisited()){
                    Pair p = new Pair(from,to);
                    int addedCost = 0;
                    for (int i=0; i < g.getActions().get(p).size(); i++)
                        if (g.getActions().get(p).get(i).isFaulty())
                            addedCost = 1;
                    if (from.getDistanceValue()+addedCost < to.getDistanceValue()){
                        to.setDistanceValue(from.getDistanceValue() + addedCost);
                        to.setPreviousNodeInPath(from);
                    }
                }
            }
        }

        int minDistance = g.getErrState().getDistanceValue();
        
        double res= Math.round((double)1/(1+minDistance) * Math.pow(10, 3)) / Math.pow(10, 3);
        return res;
    }
    

    //Only works together with -det
    public void printTraceToError(){
        System.out.println("Masking Distance: "+calculateDistanceDijsktra());
        System.out.println("\n·····ERROR PATH·····\n");
        GameNode curr = g.getErrState();
        int i = 0;
        while (curr != null){
            System.out.println(i+"."+curr.toString());
            curr = curr.getPreviousNodeInPath();
            i++;
        }
    }

    public void printUnmatches(){
        System.out.println("\n·····ACTIONS LEADING TO ERROR·····\n");
        for (GameNode n : g.getPredecessors(g.getErrState())){
            System.out.println(n);
            //System.out.println((n.getSymbol().isFromSpec()?"S":"I")+n.getSymbol().getLabel());
        }
    }

    public void createDot(int lineLimit){
        g.createDot(lineLimit, (pSpec.getName()+"---"+pImp.getName()), true);
    }

    public void simulateGame(){
        GameNode curr;
        Stack<GameNode> track = new Stack<GameNode>();
        track.push(g.getInitial());
        try (Scanner sc = new Scanner(System.in)) {
			String c = "";
			System.out.println("\n·····SIMULATION·····\n");
			while (!c.equals("X") && !c.equals("x")){
			    curr = track.peek();
			    System.out.println("\n\nCURRENT STATE: ["+curr+"]\nChoose an action... (action : [nextstate])\n");
			    Integer i = 0;
			    for (GameNode succ : g.getSuccessors(curr)){
			        Pair p = new Pair(curr,succ);
			        if (g.getActions().get(p) != null){
			            for (int j=0; j < g.getActions().get(p).size(); j++){
			                System.out.println(i+". "+g.getActions().get(p).get(j).getLabel()+": "+"["+succ+"]");
			                i++;
			            }
			        }
			    }
			    if (track.size()>1){
			        System.out.println("Z. BACKTRACK");
			    }
			    System.out.println("X. EXIT");
			    c = sc.next();

			    i = 0;
			    for (GameNode succ : g.getSuccessors(curr)){
			        Pair p = new Pair(curr,succ);
			        if (g.getActions().get(p) != null){
			            for (int j=0; j < g.getActions().get(p).size(); j++){
			                if (c.equals(i.toString()))
			                    track.push(succ);
			                i++;
			            }
			        }
			    }

			    if (c.equals("Z") || c.equals("z")){
			        if (track.size()>1)
			        track.pop();
			    }
			}
		}
    }
}