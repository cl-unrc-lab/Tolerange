package mc;

import java.io.*;

import faulty.*;
import maskingDistance.*;

/**
 * This class represents the compiler.
 */
public class MainMD {
	   
    
    public static void main(String[] args)
    {
       ProgramParser prog = new ProgramParser();
       boolean printTrace = false;
       boolean toDot = false;
       boolean startSimulation = false;
       boolean deadlockIsError = false;
       boolean noBisim = false;
       boolean deterministic = false;
       
       if (args.length < 2){
            System.out.println("Usage: ./faulty-mask <options> <nominal model path> <faulty model path>");
            System.out.println("Output: lim n->infinity of 1/(1+sumMaskedFaults[0..n]), where n is the number of steps and sumMaskedFaults[inf..sup] is the amount of faults masked from inf to sup");
            System.out.println("Options: \n -nb : f.model does not simulate n.model \n -det : use deterministic m.distance \n -d : create dot file \n -t : print error trace (only works with -det) \n -s : start simulation \n -l : also treat deadlock as error state");
       }
       else{
           for (int i = 0; i < args.length; i++){
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
              md.createDot();
        }
     }
}