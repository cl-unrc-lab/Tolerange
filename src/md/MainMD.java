package md;


import lang.*;
import core.*;


public class MainMD {
	   
    public static void main(String[] args)
    {
       ProgramParser prog = new ProgramParser();
       boolean toDot = false;
       boolean verbose = false;
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
              AlmostSureMaskingDistance md = new AlmostSureMaskingDistance(spec,imp,verbose);
              System.out.println("Almost Sure Failing Masking Distance: "+ md.valueIteration(precision,bound,false));
              if (toDot)
                md.createDot(5000);      
            }
            catch(Exception e){
              System.out.println("An error occurred");
            }
        }
     }

     private static void printHelp(){
      System.out.println("MaskD: Masking Distance Tool\n");
      System.out.println("Usage: ./tolerange <options> <specification path> <implementation path>\n");
      System.out.println("Options:");
      System.out.println("            -d : create dot file");
      System.out.println("            -v : turn verbosity on");
      System.out.println("            p=<integer> : epsilon for value iteration stopping criteria");
      System.out.println("            b=<real> : upper bound for value iteration process");
     }

}