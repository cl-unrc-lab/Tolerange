package md;

import java.io.*;

import lang.*;
import core.*;


public class MainMD {
	   
    
    public static void main(String[] args) throws InterruptedException
    {
       ProgramParser prog = new ProgramParser();
       boolean printTrace = false;
       boolean toDot = false;
       boolean startSimulation = false;
       boolean deadlockIsError = false;
       boolean noBisim = false;
       boolean deterministic = false;
       boolean verbose = false;
       int precision = 10;
       int bound = Integer.MAX_VALUE;
       int algorithm = 0;
       
       if (args.length < 3){
            if (args.length < 2){
              System.out.println("Use -h for help");
            }
            else{
              if (args[args.length-1].equals("-h")){
                printHelp();
              }
              else{
                System.out.println("Use -h for help");
              }  
            }             
       }
       else{
           if (args[0].equals("--a1")){
              for (int i = 1; i < args.length; i++){
                if (args[i].equals("-t")){
                  printTrace = true;
                }
                if (args[i].equals("-s")){
                  startSimulation = true;
                }
                if (args[i].equals("-d")){
                  toDot = true;
                }
                if (args[i].equals("-l")){
                  deadlockIsError = true;
                }
                if (args[i].equals("-v")){
                  verbose = true;
                }
                if (args[i].equals("-nb")){
                  noBisim = true;
                }
                if (args[i].equals("-det")){
                  deterministic = true;
                }
              }
              Program spec = prog.parseAux(args[args.length - 2]);
              Program imp = prog.parseAux(args[args.length - 1]);
              MaskingDistance md = new MaskingDistance(spec,imp,deadlockIsError,noBisim,verbose);
              if (printTrace){
                  md.printTraceToError();
              }
              else{
                if (startSimulation){
                    md.simulateGame();
                }
                else{
                  if (toDot)
                    md.createDot(2000);
                  if (!deterministic)
                    System.out.println("Masking Distance: "+md.calculateDistance());
                  else
                    System.out.println("Masking Distance: "+md.calculateDistanceBFS());
                }
              }
              
          }
           
           if (args[0].equals("--a2")){
              algorithm = 1;
              Program test = prog.parseAux(args[args.length - 1]);
              for (int i = 1; i < args.length; i++){
                if (args[i].equals("-d")){
                  toDot = true;
                }
                if (args[i].equals("-v")){
                  verbose = true;
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
              StrategySynthesis md = new StrategySynthesis(test,verbose);
              if (toDot){
                  md.createDot(200);
                  System.out.println("Dot file created! --> out/<filename>.dot");
              }
              else{
                try{
                  System.out.println("Almost Sure Failing Distance: "+md.synthesizeStrategy(precision,bound));
                  //System.out.println("Expected Number of Decisions: "+md.expectedNumberOfDecisions(precision));
                  System.out.println("A strategy for the Controller has been synthesized! --> out/<filename>.strat");
                }
                catch(Exception e){
                  System.out.println(e);
                }
              }
           }
           if (args[0].equals("--a3")){
              algorithm = 2;
              for (int i = 1; i < args.length; i++){
                if (args[i].equals("-d")){
                  toDot = true;
                }
                if (args[i].equals("-v")){
                  verbose = true;
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
              AlmostSureMaskingDistance md = new AlmostSureMaskingDistance(spec,imp,verbose);
              try{
                if (toDot)
                  md.createDot(200);
                System.out.println("Almost Sure Failing Masking Distance: "+md.valueIteration(precision,bound));      
              }
              catch(Exception e){
                System.out.println(e);
              }
           }
                     
            
        }
     }

     private static void printHelp(){
      System.out.println("MaskD: Masking Distance Tool\n");
      System.out.println("Usage: ./maskD <algorithm> <options> <specification path> <implementation path>\n");
      System.out.println("Algorithm:");
      System.out.println("            --a1 : masking distance");
      System.out.println("            --a2 : synthesis of controller strategy under fair adversary");
      System.out.println("            --a3 : almost sure failing masking distance\n");
      System.out.println("            * algorithm a2 only requires the path to a single model");
      System.out.println("Options:");
      System.out.println("            -nb : toggle if f.model don't need to simulate n.model (only for a1)");
      System.out.println("            -det : use deterministic m.distance algorithm (only for a1)");
      System.out.println("            -d : create dot file");
      System.out.println("            -t : print error trace (only works with -det) (only for a1)");
      System.out.println("            -s : start simulation (only for a1)");
      System.out.println("            -l : also treat deadlock as error state (only for a1)");
      System.out.println("            -v : turn verbosity on");
      System.out.println("            p=<num> : use precision <num> for real numbers (only for a2 and a3)");
      System.out.println("            b=<num> : set upper bound for value iteration (only for a2 and a3)");
     }
}