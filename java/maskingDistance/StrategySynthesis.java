package maskingDistance;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import lang.*;
import model.*;

import java.io.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;

public class StrategySynthesis{

	private SimpleGameGraph g; // The masking distance game graph, undefined until buildGraph is called

    public StrategySynthesis(Program prog) throws InterruptedException{
        buildGraph(prog);
    }

    public SimpleGameGraph getG(){
        return g;
    }

    public void buildGraph(Program prog) throws InterruptedException{
        Model m;
        System.out.println("Building Model...");
        m = prog.toGraph(true);
        System.out.println("Model states: "+m.getNumNodes());
        System.out.println("Model edges: "+m.getNumEdges());
        m.createDot(true);
        System.out.println("Building Game Graph...");
        g = new SimpleGameGraph();

        //calculate initial state
        SimpleGameNode init = new SimpleGameNode(m.getInitial(),new Action("", false, false, false) , false);
        g.addNode(init);
        g.setInitial(init);

        TreeSet<SimpleGameNode> iterSet = new TreeSet<SimpleGameNode>();
        iterSet.add(init);

        //build the game graph
        while(!iterSet.isEmpty()){
            SimpleGameNode curr = iterSet.pollFirst();
            if (!curr.isProbabilistic()){ // player C or E turn
              for (ModelState succ : m.getSuccessors(curr.getState())){
                  Pair p = new Pair(curr.getState(),succ);
                  if (m.getActions().get(p) != null){
                      for (int i=0; i < m.getActions().get(p).size(); i++){
                          SimpleGameNode curr_ = null;
                          if (m.getActions().get(p).get(i).getIsProbabilistic()){ //then we create a probabilistic node (has same data as curr)
                              curr_ = new SimpleGameNode(curr.getState(),m.getActions().get(p).get(i), true);
                          }
                          else{ //non probabilistic, so curr_ is C or E
                              curr_ = new SimpleGameNode(succ,m.getActions().get(p).get(i), false);
                          }
                          
                          SimpleGameNode toOld = g.search(curr_);
                          if (toOld == null){
                              g.addNode(curr_);
                              g.addEdge(curr,curr_, new Action("", false, false, false)); 
                              iterSet.add(curr_);
                          }
                          else{
                              g.addEdge(curr,toOld, new Action("", false, false, false));
                          }
                      }
                  }
              }
            }
            else{ // player P turn
                for (ModelState succ : m.getSuccessors(curr.getState())){
                    if (curr.getState().getModel().getProb(curr.getState(),succ,curr.getSymbol()) != null){
                        SimpleGameNode curr_ = null;
                        curr_ = new SimpleGameNode(succ,new Action("", false, false, false), false);
                        SimpleGameNode toOld = g.search(curr_);
                        if (toOld == null){
                            g.addNode(curr_);
                            g.addEdge(curr,curr_, new Action("", false, false, false)); 
                            iterSet.add(curr_);
                        }
                        else{
                            g.addEdge(curr,toOld, new Action("", false, false, false));
                        }
                    }
                }
            }
            

            
        }

        System.out.println("Game graph states: "+g.getNumNodes());
    }


  public double valueIteration(int precision, double upperBound, boolean verbose) throws Exception{
      SimpleGameNode init =  g.getInitial();
      boolean forceExit = false;
      int i = 0;
      for (SimpleGameNode v : g.getNodes()) {
          if (v.getIsGoal()) {
              v.setValue(1,0);
          }
          else {
              v.setValue(1,upperBound); 
          }
      }
      do {
          i++;
          for (SimpleGameNode v : g.getNodes()) {
              v.setValue(0,v.getValues()[1]);
          }
          for (SimpleGameNode v : g.getNodes()) {
              if (forceExit)
                    break;
              double val = 0;
              switch (v.getPlayerControl()){
                case 2:  val = v.getReward() + minValue(g.getSuccessors(v)); //Environment
                            break;
                case 1:  val = v.getReward() + maxValue(g.getSuccessors(v)); //Controller
                            break;
                case 3:  try{
                            val = sumProbs(v,g.getSuccessors(v)); //Probabilistic
                            } 
                            catch (Exception e){
                              System.out.println("error en P");
                            }
                            break;
                default: break;
              } 
              v.setValue(1,val);
              if (v.equals(init)) {
                    System.out.println(v.getValues()[1]);
              }
          }   
      }  while (thereIsNoFixPoint(precision) && !forceExit);
      return init.getValues()[1];
  }
  
  private boolean thereIsNoFixPoint(int precision) {
      String decimalFormat = "#.";
      for (int i = 0; i < precision; i++){
        decimalFormat += "#";
      }
      DecimalFormat newFormat = new DecimalFormat(decimalFormat);
      for (SimpleGameNode v : g.getNodes()){  
          double currValue =  Double.valueOf(newFormat.format(v.getValues()[1]));
          double oldValue = Double.valueOf(newFormat.format(v.getValues()[0]));
         if (currValue != oldValue) {
             return true;
         }
      }
      return false;
  }

  private double minValue(Set<SimpleGameNode> vs) throws Exception{
    double min = Double.POSITIVE_INFINITY;
    for (SimpleGameNode v : vs){
      double val = v.getValues()[0];
      if (val < min){
        min = val;
      }
    }
    return min;
  }


  private double maxValue(Set<SimpleGameNode> vs) throws Exception{
    double max = Double.NEGATIVE_INFINITY;
    for (SimpleGameNode v : vs){
        double val = v.getValues()[0];
        if (val > max){
          max = val;
        }
    }
    return max;
  }

  private double sumProbs(SimpleGameNode v, Set<SimpleGameNode> vs) throws Exception{
    double sum = 0;
    for (SimpleGameNode v_ : vs){
      sum += v.getState().getModel().getProb(v.getState(),v_.getState(),v.getSymbol()) * v_.getValues()[0];
    }
    return sum;
  }



    public void createDot(int lineLimit){
        g.createDot(lineLimit);
    }

}