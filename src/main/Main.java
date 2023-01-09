package main;


import lang.*;
import core.*;


public class Main {
	   
    public static void main(String[] args)
    {
       ProgramParser prog = new ProgramParser();
       boolean toDot = false;
       boolean verbose = false;
       boolean checkFair = false;
       boolean useGurobi = false;
       int precision = 6;
       int bound = Integer.MAX_VALUE;
       
       if (args.length < 2){
          printHelp();
       }
       else{
           for (int i = 0; i < args.length; i++){
              if (args[i].equals("-v")){
                verbose = true;
              }
              if (args[i].equals("-d")){
                toDot = true;
              }
              if (args[i].equals("-f")){
                checkFair = true;
              }
              if (args[i].equals("-gurobi")){
                useGurobi = true;
              }
              if (args[i].startsWith("p=")){
                  String[] splits = args[i].split("=");
                  precision = Integer.parseInt(splits[1]);
              }
              if (args[i].startsWith("b=")){
                String[] splits = args[i].split("=");
                bound = Integer.parseInt(splits[1]);
              }
           }
            Program spec = prog.parseAux(args[args.length - 2]);
            Program imp = prog.parseAux(args[args.length - 1]);
            try{
              AlmostSureMaskingDistance md = new AlmostSureMaskingDistance(spec,imp,verbose,useGurobi);
              if (checkFair)
                System.out.println("Is Almost Sure Failing Under Fairness? : "+ md.almostSureFailingUnderFairness());
              else
                System.out.println("Expected Milestones Achieved: "+ md.expectedMilestones(precision,bound));
              if (toDot)
                md.createDot();      
            }
            catch(Exception e){
              System.out.println("An error occurred");
            }
        }
     }

     private static void printHelp(){
      System.out.println("Usage: ./Tolerange <options> <specification path> <implementation path>\n");
      System.out.println("Options:");
      System.out.println("            -gurobi : use gurobi instead of ssc for lineal programming");
      System.out.println("            -d : create dot file");
      System.out.println("            -v : turn verbosity on");
      System.out.println("            -f : check if game is almost sure failing");
      System.out.println("            p=<integer> : epsilon for value iteration stopping criteria");
      System.out.println("            b=<real> : upper bound for value iteration process");
     }

}