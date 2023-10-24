package core;

import java.util.*;

import lang.*;
import model.*;
import games.*;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.util.LPThreadsNumber;

import gurobi.*;

/**
* Provides the Core functionality of Tolerange: methods for building the game graph, checking whether a game 
* is stopping under fairness, and all the machinery for computing the rewards of the games by using Gurobi or SCC.
* @author Luciano Putruele
*/
public class AlmostSureMaskingDistance {

    private GameGraph g;	// the game graph
    private Program pSpec;	// the spec
    private Program pImp;	// the implementation
    private boolean verbose; // a flag indicating if Tolerange is running in verbose mode
    private boolean useGurobi; // a flag indicating if Gurobi is used for solving LP problems (much faster)
    private GRBEnv env;

    /**
    * Constructor for the class, calls buildGraph and sets up the lineal programming libraries.
    *
    * @param  specProgram  the nominal model
    * @param  impProgram  the fault-tolerant model
    * @param  verb is verbosity on?
    * @param  gurobi use Gurobi?
    */
    public AlmostSureMaskingDistance(Program specProgram, Program impProgram, boolean verb, boolean gurobi) throws InterruptedException, GRBException {
        pSpec = specProgram;
        pImp = impProgram;
        verbose = verb;
        useGurobi = gurobi;
        buildGraph();
        // Create empty environment , set options , and start
        if (useGurobi) {
            env = new GRBEnv(true);
            env.set(GRB.IntParam.OutputFlag, 0);
            //env.set("logFile", "mip1 .log");
            env.start();
        } else {
            SscLogger.getLogger().setLevel(Level.OFF);
        }

    }

    /**
    * Builds Symbolic Game Graph, the main behavior is explained in the paper 
    */
    private void buildGraph() throws InterruptedException {
        Model spec, imp;
        System.out.println("Building Models...");
        spec = pSpec.toMDP(true);
        imp = pImp.toMDP(false);
        if (verbose) {
            System.out.println("Spec states: " + spec.getNumNodes());
            System.out.println("Spec edges: " + spec.getNumEdges());
            System.out.println("Imp states: " + imp.getNumNodes());
            System.out.println("Imp edges: " + imp.getNumEdges());
        }
        spec.createDot(false);
        imp.createDot(true);
        System.out.println("Building Game Graph...");
        g = new GameGraph();

        //calculate initial state
        GameNode init = new GameNode(spec.getInitial(), imp.getInitial(), new Action("", false, false, false), TPlayer.REFUTER);
        g.addNode(init);
        g.setInitial(init);

        TreeSet<GameNode> iterSet = new TreeSet<GameNode>();
        iterSet.add(init);

        //build the game graph
        while (!iterSet.isEmpty()) {
            GameNode curr = iterSet.pollFirst();

            if (curr.isRefuter()) { //if player is refuter we add its possible moves from current state
                //IMP MOVES
                for (ModelState succ : imp.getSuccessors(curr.getImpState())) {
                    Pair p = new Pair(curr.getImpState(), succ);
                    if (imp.getActions().get(p) != null) {
                        for (int i = 0; i < imp.getActions().get(p).size(); i++) {
                            GameNode curr_ = new GameNode(curr.getSpecState(), curr.getImpState(), imp.getActions().get(p).get(i), TPlayer.VERIFIER);
                            GameNode toOld = g.search(curr_);
                            if (toOld == null) {
                                g.addNode(curr_);
                                if (curr_.getSymbol().isFaulty())
                                    curr_.setMask(true); //ahora quedo medio irrelevante el mask
                                g.addEdge(curr, curr_);
                                iterSet.add(curr_);
                            } else {
                                g.addEdge(curr, toOld);
                            }
                        }
                    }
                }
                //SPEC MOVES
                for (ModelState succ : spec.getSuccessors(curr.getSpecState())) {
                    Pair p = new Pair(curr.getSpecState(), succ);
                    if (spec.getActions().get(p) != null) {
                        for (int i = 0; i < spec.getActions().get(p).size(); i++) {
                            GameNode curr_ = new GameNode(curr.getSpecState(), curr.getImpState(), spec.getActions().get(p).get(i), TPlayer.VERIFIER);
                            GameNode toOld = g.search(curr_);
                            if (toOld == null) {
                                g.addNode(curr_);
                                g.addEdge(curr, curr_);
                                iterSet.add(curr_);
                            } else {
                                g.addEdge(curr, toOld);
                            }
                        }
                    }
                }
            }

            if (curr.isVerifier()) { //if player is verifier we add its matching move from current state or err transition if can't match
                boolean foundSucc = false;
                //SPEC MOVES
                if (!curr.getSymbol().isFromSpec()) {
                    if (curr.getMask()) { //this means the state has to mask a previous fault
                        GameNode curr_ = new GameNode(curr.getSpecState(), curr.getImpState(), curr.getSymbol(), TPlayer.PROBABILISTIC);
                        GameNode toOld = g.search(curr_);
                        if (toOld == null) {
                            g.addNode(curr_);
                            g.addEdge(curr, curr_);
                            iterSet.add(curr_);
                        } else {
                            g.addEdge(curr, toOld);
                        }
                        foundSucc = true;
                    } else {
                        for (ModelState succ : spec.getSuccessors(curr.getSpecState())) {
                            Pair p = new Pair(curr.getSpecState(), succ);
                            if (spec.getActions().get(p) != null) {
                                for (int i = 0; i < spec.getActions().get(p).size(); i++) {
                                    Action lblImp = curr.getSymbol();
                                    Action lblSpec = spec.getActions().get(p).get(i);
                                    if (lblImp.getLabel().equals(lblSpec.getLabel()) || (lblImp.isTau() && lblSpec.isTau())) {
                                        GameNode curr_ = new GameNode(curr.getSpecState(), curr.getImpState(), curr.getSymbol(), TPlayer.PROBABILISTIC);
                                        GameNode toOld = g.search(curr_);
                                        if (toOld == null) {
                                            g.addNode(curr_);
                                            g.addEdge(curr, curr_); //add label may not be necessary
                                            iterSet.add(curr_);
                                        } else {
                                            g.addEdge(curr, toOld);
                                        }
                                        foundSucc = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {//IMP MOVES
                    for (ModelState succ : imp.getSuccessors(curr.getImpState())) {
                        Pair p = new Pair(curr.getImpState(), succ);
                        if (imp.getActions().get(p) != null) {
                            for (int i = 0; i < imp.getActions().get(p).size(); i++) {
                                Action lblSpec = curr.getSymbol();
                                Action lblImp = imp.getActions().get(p).get(i);
                                if (lblImp.getLabel().equals(lblSpec.getLabel()) || (lblImp.isTau() && lblSpec.isTau())) {
                                    GameNode curr_ = new GameNode(curr.getSpecState(), curr.getImpState(), curr.getSymbol(), TPlayer.PROBABILISTIC);
                                    GameNode toOld = g.search(curr_);
                                    if (toOld == null) {
                                        g.addNode(curr_);
                                        g.addEdge(curr, curr_); //add label may not be necessary
                                        iterSet.add(curr_);
                                    } else {
                                        g.addEdge(curr, toOld);
                                    }
                                    foundSucc = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!foundSucc) {
                    g.addEdge(curr, g.getErrState());
                }
            }

            if (curr.isProbabilistic()) {
                Action symSpec = curr.getSymbol().cloneForSpec(true);
                Action symImp = curr.getSymbol().cloneForSpec(false);
                for (ModelState succImp : imp.getSuccessors(curr.getImpState())) {
                    for (ModelState succSpec : spec.getSuccessors(curr.getSpecState())) {
                        if (curr.getSpecState().getModel().getProb(curr.getSpecState(), succSpec, symSpec) != null || curr.getSymbol().isFaulty()) {
                            if (curr.getImpState().getModel().getProb(curr.getImpState(), succImp, symImp) != null) {
                                GameNode curr_;
                                if (curr.getSymbol().isFaulty())
                                    curr_ = new GameNode(curr.getSpecState(), succImp, new Action("", false, false, false), TPlayer.REFUTER);
                                else
                                    curr_ = new GameNode(succSpec, succImp, new Action("", false, false, false), TPlayer.REFUTER);
                                GameNode toOld = g.search(curr_);
                                if (toOld == null) {
                                    g.addNode(curr_);
                                    g.addEdge(curr, curr_);
                                    iterSet.add(curr_);
                                } else {
                                    g.addEdge(curr, toOld);
                                }
                            }
                        }
                    }
                }
            }

        }
        if (verbose) {
            System.out.println("Game graph states: " + g.getNumNodes());
            System.out.println("Game graph edges: " + g.getNumEdges());
        }
    }

    /**
    * Solve equations of a probabilistic vertex of the game with SSC or Gurobi
    *
    * @param  vars  the successors of current state
    * @param  current current state, it is required to be probabilistic
    * @param  banned the set of states that reach the error state with >0 probability, if
    * we are not checking for almost sure stopping under fairness then its null
    * @return      solution to the equations
    */
    private double solve(Set<GameNode> vars, GameNode current, Set<GameNode> banned) throws Exception {
        return useGurobi ? solveGurobi(vars, current, banned) : solveSSC(vars, current, banned);
    }

    /**
     * Solve the equations using gurobi see {@code solve}
     * @param vars	vars  the successors of current state
     * @param current	current current state, it is required to be probabilistic
     * @param banned	 the set of states that reach the error state with >0 probability, if
     * we are not checking for almost sure stopping under fairness then its null
     * @return solution to the equations
     * @throws Exception
     */
    private double solveGurobi(Set<GameNode> vars, GameNode current, Set<GameNode> banned) throws Exception {
        Action symSpec = current.getSymbol().cloneForSpec(true);
        Action symImp = current.getSymbol().cloneForSpec(false);
        LinkedList<GameNode> vs = new LinkedList<GameNode>(vars);
        
        //defining the objective
        ArrayList<Double> lastColProbs = new ArrayList<Double>();
        ArrayList<Double> lastColProbs2 = new ArrayList<Double>();
        //only used if banned != null
        boolean[] markedBanned = new boolean[vs.size()];

        for (int i = 0; i < vs.size(); i++) {
            if (banned != null)
                markedBanned[i] = banned.contains(vs.get(i));
        }
        
        //defining coupling constraints
        //initialization
        int[] markedStates = new int[vs.size()]; //colors based on first component of v 
        int[] markedStates2 = new int[vs.size()]; //colors based on second component of v
        for (int i = 0; i < vs.size(); i++) {
            markedStates[i] = 0;
            markedStates2[i] = 0;
        }
        int colorSpec = 0;
        int colorImp = 0;
        //identify rows
        for (int i = 0; i < vs.size(); i++) {
            if (markedStates[i] == 0) {
                if (current.getSymbol().isFaulty())
                    lastColProbs.add(1.0);
                else
                    lastColProbs.add(current.getSpecState().getModel().getProb(current.getSpecState(), vs.get(i).getSpecState(), symSpec));
                colorSpec++;
                for (int j = 0; j < vs.size(); j++) {
                    if (vs.get(j).getSpecState().equals(vs.get(i).getSpecState())) {
                        markedStates[j] = colorSpec; //assign same color to every (s,-) for a fixed s
                    }
                }
            }
            if (markedStates2[i] == 0) {
                lastColProbs2.add(current.getImpState().getModel().getProb(current.getImpState(), vs.get(i).getImpState(), symImp));
                colorImp++;
                for (int j = 0; j < vs.size(); j++) {
                    if (vs.get(j).getImpState().equals(vs.get(i).getImpState())) {
                        markedStates2[j] = colorImp; //assign same color to every (-,t) for a fixed t
                    }
                }
            }
        }
        int rowSize = colorSpec + colorImp;
        //if (banned != null) // add one last special row
        //    rowSize++;
        boolean matrix[][] = new boolean[rowSize][vs.size()];
        double[] lastCol = new double[rowSize];
        //define rows
        for (int i = 0; i < colorSpec; i++) {
            boolean[] row = new boolean[vs.size()];
            for (int j = 0; j < row.length; j++) {
                row[j] = markedStates[j] == (i + 1) ? true : false;
            }
            matrix[i] = row;
            lastCol[i] = lastColProbs.get(i);
        }
        for (int i = colorSpec; i < colorSpec + colorImp; i++) {
            boolean[] row = new boolean[vs.size()];
            for (int j = 0; j < row.length; j++) {
                row[j] = markedStates2[j] == (i - colorSpec + 1) ? true : false;
            }
            matrix[i] = row;
            lastCol[i] = lastColProbs2.get(i - colorSpec);
        }
        //if (banned != null){
        //    matrix[rowSize-1] = markedBanned;
        //    lastCol[rowSize-1] = 0;
        //}

        // Create empty model
        GRBModel model = new GRBModel(env);

        // Create variables
        GRBVar c[] = new GRBVar[vs.size()]; // variables representing probabilities
        GRBLinExpr obj = new GRBLinExpr(); // objective expression to maximize / minimize
        for (int i = 0; i < vs.size(); i++) {
            c[i] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "x" + i);
            if (banned == null)
                obj.addTerm(vs.get(i).getValues()[0], c[i]); // objective is composed of terms (rew(v_i) * probability variable)
            else { // if we want to check almost-sure under fairness (banned != null)
                if (markedBanned[i])
                    obj.addTerm(1.0, c[i]); // objective is composed of terms (probability variable)
            }

        }

        // Set objective
        if (banned == null)
            model.setObjective(obj, GRB.MAXIMIZE);
        else
            model.setObjective(obj, GRB.MINIMIZE);
        // Add constraints
        ArrayList<GRBLinExpr> constraints = new ArrayList<GRBLinExpr>();
        for (int i = 0; i < matrix.length; i++) {
            GRBLinExpr constraint = new GRBLinExpr();
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j])
                    constraint.addTerm(1.0, c[j]);
                //constraint.addTerm(matrix[i][j]?1.0:0.0, c[j]); // matrix[i,j] es el coeficiente (1 o 0) 
            }
            model.addConstr(constraint, GRB.EQUAL, lastCol[i], "c" + i);
        }

        // Optimize model
        model.optimize();
        double result = model.get(GRB.DoubleAttr.ObjVal);

        // Dispose of model and environment
        model.dispose();

        return result;
    }

    /**
     * Solve the game using SSC
     * @param vars
     * @param current
     * @param banned
     * @return
     * @throws Exception
     */
    private double solveSSC(Set<GameNode> vars, GameNode current, Set<GameNode> banned) throws Exception {
        Action symSpec = current.getSymbol().cloneForSpec(true);
        Action symImp = current.getSymbol().cloneForSpec(false);
        LinkedList<GameNode> vs = new LinkedList<GameNode>(vars);
        //defining the objective
        double[] objVariables = new double[vs.size()];
        ArrayList<Double> lastColProbs = new ArrayList<Double>();
        ArrayList<Double> lastColProbs2 = new ArrayList<Double>();
        //only used if banned != null
        //if (banned != null){
        //for (int i=0;i<vs.size();i++){
        //            markedBanned[i] = banned.contains(vs.get(i))?1.0:0.0;
        //    }
        //}
        for (int i = 0; i < vs.size(); i++) {
            if (banned == null) {
                objVariables[i] = vs.get(i).getValues()[0];
            } else {
                objVariables[i] = banned.contains(vs.get(i)) ? 1.0 : 0.0;
            }
        }
        //defining constraints
        //initialization
        int[] markedStates = new int[vs.size()]; //colors based on first component of v
        int[] markedStates2 = new int[vs.size()]; //colors based on second component of v
        for (int i = 0; i < vs.size(); i++) {
            markedStates[i] = 0;
            markedStates2[i] = 0;
        }
        int colorSpec = 0;
        int colorImp = 0;
        //identify rows
        for (int i = 0; i < vs.size(); i++) {
            if (markedStates[i] == 0) {
                if (current.getSymbol().isFaulty())
                    lastColProbs.add(1.0);
                else
                    lastColProbs.add(current.getSpecState().getModel().getProb(current.getSpecState(), vs.get(i).getSpecState(), symSpec));
                colorSpec++;
                for (int j = 0; j < vs.size(); j++) {
                    if (vs.get(j).getSpecState().equals(vs.get(i).getSpecState())) {
                        markedStates[j] = colorSpec; //assign same color to every (s,-) for a fixed s
                    }
                }
            }
            if (markedStates2[i] == 0) {
                lastColProbs2.add(current.getImpState().getModel().getProb(current.getImpState(), vs.get(i).getImpState(), symImp));
                colorImp++;
                for (int j = 0; j < vs.size(); j++) {
                    if (vs.get(j).getImpState().equals(vs.get(i).getImpState())) {
                        markedStates2[j] = colorImp; //assign same color to every (-,t) for a fixed t
                    }
                }
            }
        }
        double matrix[][] = new double[colorSpec + colorImp + 2 * vs.size()][vs.size()];
        double[] lastCol = new double[colorSpec + colorImp + 2 * vs.size()];
        //define rows
        for (int i = 0; i < colorSpec; i++) {
            double[] row = new double[vs.size()];
            for (int j = 0; j < row.length; j++) {
                row[j] = markedStates[j] == (i + 1) ? 1.0 : 0.0;
            }
            matrix[i] = row;
            lastCol[i] = lastColProbs.get(i);
        }
        for (int i = colorSpec; i < colorSpec + colorImp; i++) {
            double[] row = new double[vs.size()];
            for (int j = 0; j < row.length; j++) {
                row[j] = markedStates2[j] == (i - colorSpec + 1) ? 1.0 : 0.0;
            }
            matrix[i] = row;
            lastCol[i] = lastColProbs2.get(i - colorSpec);
        }
        //constraint: all variables must be between 0 and 1
        for (int i = colorSpec + colorImp; i < colorSpec + colorImp + vs.size(); i++) {
            double[] row = new double[vs.size()];
            for (int j = 0; j < row.length; j++) {
                row[j] = j == (i - colorSpec - colorImp) ? 1.0 : 0.0;
            }
            matrix[i] = row;
            matrix[i + vs.size()] = row;
            lastCol[i] = 0.0;
            lastCol[i + vs.size()] = 1.0;
        }

        //double A[][]= matrix;
        //double b[]= lastCol;
        //double c[]= objVariables;
        ConsType[] rel = new ConsType[colorSpec + colorImp + 2 * vs.size()];
        try {
            for (int i = 0; i < colorSpec + colorImp; i++) {
                rel[i] = ConsType.EQ;
            }
            for (int i = colorSpec + colorImp; i < colorSpec + colorImp + vs.size(); i++) {
                rel[i] = ConsType.GE;
            }
            for (int i = colorSpec + colorImp + vs.size(); i < colorSpec + colorImp + 2 * vs.size(); i++) {
                rel[i] = ConsType.LE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LinearObjectiveFunction fo;
        if (banned == null)
            fo = new LinearObjectiveFunction(objVariables, GoalType.MAX);
        else
            fo = new LinearObjectiveFunction(objVariables, GoalType.MIN);

        ArrayList<Constraint> constraints = new ArrayList<Constraint>();
        for (int i = 0; i < matrix.length; i++) {
            constraints.add(new Constraint(matrix[i], rel[i], lastCol[i]));
        }
        LP lp = new LP(fo, constraints);
        lp.resolve();
        double res = lp.getSolution().getOptimumValue();
        return res;
    }

    // Deprecated
    private void showMatrix(double[][] a, double[] b, double[] c, int startGE, int startLE) {
        String res = "Maximize: ";
        for (int i = 0; i < c.length; i++) {
            res += c[i] == 0.0 ? "" : ("x" + i + "*" + c[i] + " + ");
        }
        res += "\n subject to:\n";
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                res += a[i][j] == 0.0 ? "" : ("x" + j + " + ");
            }
            if (i < startGE)
                res += " = " + b[i] + "\n";
            if (i >= startGE && i < startLE)
                res += " >= " + b[i] + "\n";
            if (i >= startLE)
                res += " <= " + b[i] + "\n";
        }
        System.out.println(res);
    }

    /**
    * Solves the game through value iteration, if the game is NOT almost-sure failing under fairness the result will probably be wrong.
    *
    * @param  precision used by the stopping criterion
    * @param  upperBound the upper bound value for which all states (except the error state) are initialized
    * @throws Exception if the linear programming fails
    * @return      value of the game at initial state
    */
    public double expectedMilestones(int precision, double upperBound) throws Exception {
        System.out.println("Computing value of the game...");
        if (verbose) System.out.println("Upper Bound is:"+upperBound);
        GameNode init = g.getInitial();
        boolean forceExit = false;
        int i = 0;
        for (GameNode v : g.getNodes()) {
            if (v.isErrState()) {
                v.setValue(1, 0);
            } else {
                v.setValue(1, upperBound);
            }
        }
        do {
            i++;
            for (GameNode v : g.getNodes()) {
                v.setValue(0, v.getValues()[1]);
            }
            for (GameNode v : g.getNodes()) {
                if (forceExit)
                    break;
                double val = 0;
                switch (v.getPlayer()) {
                    case REFUTER:
                        val = Math.min(upperBound, minValue(g.getSuccessors(v)));
                        break;
                    case VERIFIER:
                        val = Math.min(upperBound, v.getSymbol().getReward() + maxValue(g.getSuccessors(v)));
                        break;
                    case PROBABILISTIC:
                        try {
                            val = Math.min(upperBound, solve(g.getSuccessors(v), v, null));
                        } catch (Exception e) {
                            System.out.println("ERROR: Unexpected problem while solving linear programming");
                            forceExit = true;
                        }
                        break;
                    default:
                        break;
                }
                v.setValue(1, val);
                if (v.equals(init) && verbose) {
                    System.out.println("Initial state value at iteration "+i+": "+v.getValues()[1]);
                }
            }
        } while (!stopingCriterion(precision) && !forceExit);
        if (useGurobi)
            env.dispose();
        System.out.println("Total Number of Iterations:"+i);
        return init.getValues()[1];
    }

    /**
    * Checks if the stopping criterion (for value iteration) is met
    *
    * @param  precision how many decimals are considered for epsilon 'e'
    * @return      returns true iff there is no state whose value differs more than a certain 'e' from its previous value
    */
    private boolean stopingCriterion(int precision) {
        String decimalFormat = "#.";
        double diff = 0;
        String eFormat = "";
        double e = 0;
        for (int i = 0; i < precision; i++) {
            decimalFormat += "#";
            eFormat += i == 0 ? "0." : "0";
        }
        eFormat += "1";
        e = Double.parseDouble(eFormat);
        DecimalFormat newFormat = new DecimalFormat(decimalFormat);
        for (GameNode v : g.getNodes()) {
            try {
                double val = v.getValues()[1] > e ? v.getValues()[1] : e;
                diff = (v.getValues()[0] - v.getValues()[1]) / val;
                diff = Double.valueOf(newFormat.format(diff));
                if (diff > e) {
                    return false;
                }
            } catch (NumberFormatException ex) {
                System.out.println("ERROR: Number Format Exception");
                System.out.println("Node " + v + " has value: " + v.getValues()[0]);
            }
        }
        return true;
    }


    /**
    * Calculates the min value of a set of states
    *
    * @param  vs set of states
    * @return      min current value of vs
    */
    private double minValue(Set<GameNode> vs) {
        double min = Double.POSITIVE_INFINITY;
        for (GameNode v : vs) {
            double val = v.getValues()[0];
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    /**
    * Calculates the max value of a set of states
    *
    * @param  vs set of states
    * @return      max current value of vs
    */
    private double maxValue(Set<GameNode> vs) {
        double max = Double.NEGATIVE_INFINITY;
        for (GameNode v : vs) {
            double val = v.getValues()[0];
            if (val > max) {
                max = val; // we take into account the bound
            }
        }
        return max;
    }

    /**
    * Generates a dot file
    */
    public void createDot() {
        g.createDot((pSpec.getName() + "---" + pImp.getName()), false);
    }

    /**
    * Checks if the game is almost-sure failing under fairness
    *
    * @return     true iff the game is almost-sure failing under fairness
    */
    public boolean almostSureFailingUnderFairness() {
        System.out.println("Checking if game is almost sure failing under fairness...");
        // compute closure of All Pre (Error)
        Set<GameNode> s1 = new HashSet<GameNode>();
        s1.add(g.getErrState());
        boolean change = true;
        boolean found;
        int oldSize;
        while (change) {
            List<GameNode> added = new ArrayList<GameNode>();
            change = false;
            oldSize = s1.size();
            for (GameNode v : s1) {
                for (GameNode v_ : g.getPredecessors(v)) {
                    switch (v_.getPlayer()) {
                        case REFUTER:
                            added.add(v_);
                            break;
                        case VERIFIER:
                            found = false;
                            for (GameNode v_s : g.getSuccessors(v_)) {
                                if (!s1.contains(v_s)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found)
                                added.add(v_);
                            break;
                        case PROBABILISTIC:
                            try {
                                if (solve(g.getSuccessors(v_), v_, s1) > 0)
                                    added.add(v_);
                            } catch (Exception e) {
                                System.out.println("Error during almostSureFailingUnderFairness check:" + e);
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            s1.addAll(added);
            if (oldSize != s1.size()) {
                change = true;
            }
        }


        // compute closure of Exist Pre (V / s1)
        Set<GameNode> s2 = new HashSet<GameNode>();
        for (GameNode v : g.getNodes()) {
            if (!s1.contains(v))
                s2.add(v);
        }
        change = true;
        while (change) {
            List<GameNode> added = new ArrayList<GameNode>();
            change = false;
            oldSize = s2.size();
            for (GameNode v : s2) {
                for (GameNode v_ : g.getPredecessors(v)) {
                    switch (v_.getPlayer()) {
                        case REFUTER:
                            added.add(v_);
                            break;
                        case VERIFIER:
                            added.add(v_);
                            break;
                        case PROBABILISTIC:
                            found = false;
                            for (GameNode v_s : g.getSuccessors(v_)) {
                                Double p1 = v.getSpecState().getModel().getProb(v.getSpecState(), v_s.getSpecState(), v.getSymbol());
                                Double p2 = v.getImpState().getModel().getProb(v.getImpState(), v_s.getImpState(), v.getSymbol());
                                if (s2.contains(v_s) && p1 > 0 && p2 > 0) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found)
                                added.add(v_);
                            break;
                        default:
                            break;
                    }
                }
            }
            s2.addAll(added);
            if (oldSize != s2.size()) {
                change = true;
            }
        }


        return !s2.contains(g.getInitial());
    }


}