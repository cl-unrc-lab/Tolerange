package mc;

import java.io.*;

import lang.*;
import maskingDistance.*;

/**
 * This class represents the compiler.
 */
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
       int algorithm = 0;
       
       if (args.length < 3){
            System.out.println("Usage: ./maskD <algorithm> <options> <specification path> <implementation path>");
            System.out.println("Output: Masking Distance between specification and fault-tolerant implementation");
            System.out.println("Algorithm: --md : masking distance --sssf : synthesis of stochastic strategy under fairness --smd : stochastic masking distance");
            System.out.println("Options: \n -nb : f.model does not simulate n.model \n -det : use deterministic m.distance \n -d : create dot file \n -t : print error trace (only works with -det) \n -s : start simulation \n -l : also treat deadlock as error state");
       }
       else{
           if (args[0].equals("--md")){
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
                if (args[i].equals("-nb")){
                  noBisim = true;
                }
                if (args[i].equals("-det")){
                  deterministic = true;
                }
              }
              Program spec = prog.parseAux(args[args.length - 2]);
              Program imp = prog.parseAux(args[args.length - 1]);
              MaskingDistance md = new MaskingDistance(spec,imp,deadlockIsError,noBisim);
              if (printTrace){
                  md.printTraceToError();
              }
              else{
                if (startSimulation){
                    md.simulateGame();
                }
                else{
                  if (!deterministic)
                    System.out.println("Masking Distance: "+md.calculateDistance());
                  else
                    System.out.println("Masking Distance: "+md.calculateDistanceBFS());
                }
              }
              if (toDot)
                md.createDot(2000);
          }
           
           if (args[0].equals("--sssf")){
              algorithm = 1;
              Program test = prog.parseAux(args[args.length - 2]);
           }
           if (args[0].equals("--smd")){
              algorithm = 2;
              for (int i = 1; i < args.length; i++){
                if (args[i].equals("-d")){
                  toDot = true;
                }
              }
              Program spec = prog.parseAux(args[args.length - 2]);
              Program imp = prog.parseAux(args[args.length - 1]);
              AlmostSureMaskingDistance md = new AlmostSureMaskingDistance(spec,imp);
              try{
                System.out.println("Almost Sure Failing Masking Distance: "+md.valueIteration(4,100,true));
                if (toDot)
                  md.createDot(200);
              }
              catch(Exception e){
                System.out.println(e);
              }
           }
                     
            
        }
     }
}