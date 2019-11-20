package faulty;

import java.util.*;
import java.io.*;
import graph.*;


/**
* This class defines a Faulty program, it provides the basic structures: a list of enumTypes,  
* a collection of global variables( internally separating in bool and int variables),
* a collection of channels( internally separating in bool and int channels),
* and a collection of all processes defined and invocated.
* @author Ceci
**/

public class Program extends ProgramNode{
	LinkedList<EnumType> enumTypes;
	GlobalVarCollection globalVars;
	ChannelCollection channels;
	ProcessCollection process;
	Main mainProgram;
    int maxEnumSize;
	
    /** GlobalVars + Channels
     * @param gVars: Collection of all global variables classified by their type.
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public Program(GlobalVarCollection gVars, ChannelCollection channels, ProcessCollection process, Main mainProgram){
        this.enumTypes = new LinkedList<EnumType>();
		this.globalVars = gVars;
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
		this.maxEnumSize = 0;
	}
	
    /**  Channels
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public Program(ChannelCollection channels, ProcessCollection process, Main mainProgram){
        this.enumTypes = new LinkedList<EnumType>();
		this.globalVars = new GlobalVarCollection();
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
        this.maxEnumSize = 0;
		
	}
	
	/**  GlobalVars
     * @param gVars: Collection of all global variables classified by their type.
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public Program(GlobalVarCollection gVars, ProcessCollection process, Main mainProgram){
        this.enumTypes = new LinkedList<EnumType>();
		this.globalVars = gVars;
		this.channels= new ChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
        this.maxEnumSize = 0;
	}
	
    /** !EnumTypes & !GlobalVars & !Channels
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public Program( ProcessCollection process, Main mainProgram){
        this.enumTypes = new LinkedList<EnumType>();
		this.globalVars = new GlobalVarCollection();
		this.channels=new ChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
	}
    
    /** EnumTypes + GlobalVars + Channels
     * @param enumList: List of EnumTypes.
     * @param gVars: Collection of all global variables classified by their type.
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public Program(LinkedList<EnumType> enumList, GlobalVarCollection gVars, ChannelCollection channels, ProcessCollection process, Main mainProgram){
        this.enumTypes = enumList;
		this.globalVars = gVars;
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
        
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
		
	}
	
	
    /** EnumTypes + Channels
     * @param enumList: List of EnumTypes
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public Program(LinkedList<EnumType> enumList, ChannelCollection channels, ProcessCollection process, Main mainProgram){
        this.enumTypes = enumList;
		this.globalVars = new GlobalVarCollection();
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
        
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
		
	}
	
	
    /** EnumTypes + GlobalVars 
     * @param enumList: List of EnumTypes
     * @param gVars: Collection of all global variables classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public Program(LinkedList<EnumType> enumList, GlobalVarCollection gVars, ProcessCollection process, Main mainProgram){
        this.enumTypes = enumList;
		this.globalVars = gVars;
		this.channels= new ChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
        
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
	}
	
	
    /** EnumTypes 
     * @param enumList: List of EnumTypes
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public Program(LinkedList<EnumType> enumList,ProcessCollection process, Main mainProgram){
        this.enumTypes = enumList;
		this.globalVars = new GlobalVarCollection();
		this.channels=new ChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
	}
	
    public GlobalVarCollection getGlobalVars(){
        return this.globalVars;
    }
    
    public LinkedList<EnumType> getEnumList(){
        return this.enumTypes;
    }
    
    public int getMaxEnumSize(){
        return this.maxEnumSize;
    }
    
	public void accept(FaultyVisitor v){
	     v.visit(this);			
	}

    /*Generates java code implementing the complete program*/
    public String toJava(){
        String imports,tEnums,prog,globals,params,procs,main;
        imports = "import java.util.Random;"+ "\n\n";
        prog = "";
        tEnums = "";
        for (int i = 0; i < enumTypes.size(); i++){
            tEnums = "public enum " + enumTypes.get(i).getName() + "{";
            for (int j = 0; j < enumTypes.get(i).cons.length; j++){
                tEnums += enumTypes.get(i).getCons(j);
                if (j == enumTypes.get(i).cons.length-1)
                    tEnums += "}\n";
                else
                    tEnums += ",";
            }

            try{
                File file = new File("../out/" + enumTypes.get(i).getName() +".java");
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(tEnums);
                bw.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }   
        }

        procs = "";
        for (int i = 0; i < process.getProcessList().size(); i++){
            procs += process.getProcessList().get(i).toJava() + "\n\n";
        }

        globals = "    public static Random randomGenerator = new Random();\n";
        params = "";
        for (int i = 0; i < globalVars.getBoolVars().size(); i++){
            Var v = globalVars.getBoolVars().get(i);
            globals += "    public static "+"boolean" + " " + v.getName() + " = false;\n";
            params += "     "+"Bool" + " " + v.getName() + " = new Bool(false);\n";
        }
        for (int i = 0; i < globalVars.getIntVars().size(); i++){
            Var v = globalVars.getIntVars().get(i);
            globals += " "+"Int" + " " + v.getName() + " = 0;\n";
        }
        for (int i = 0; i < globalVars.getEnumVars().size(); i++){
            Var v = globalVars.getEnumVars().get(i);
            globals += " "+v.getType() + " " + v.getName() + ";\n";
        }

        main = "  public static void main(String[] args){\n" + params;
        for (int i = 0; i < mainProgram.getProcessDecl().size(); i++){
            ProcessDecl curr = mainProgram.getProcessDecl().get(i);
            main += "    "+ curr.getType() + " " + curr.getName() + " = new "+curr.getType()+"(";
            for (int j = 0; j < process.getProcessList().size(); j++){
                if (process.getProcessList().get(j).getName().equals(curr.getType()))
                    for (int k = 0; k < process.getProcessList().get(j).processInvkParameters.get(0).getInvkValues().size(); k++){
                        if (process.getProcessList().get(j).processInvkParameters.get(0).getInvkValues().get(k) instanceof Var){
                            main += ((Var)process.getProcessList().get(j).processInvkParameters.get(0).getInvkValues().get(k)).getName();
                            if (k < process.getProcessList().get(j).processInvkParameters.get(0).getInvkValues().size()-1)
                                main += ",";
                        }
                    }
            }
            main += ")"+";\n";
        }
        for (int i = 0; i < mainProgram.getProcessDecl().size(); i++){
            ProcessDecl curr = mainProgram.getProcessDecl().get(i);
            main += "    "+ curr.getName() + ".start();\n";
        }
        main += "}\n\n";

        prog += imports+"public class Program {\n\n" + globals + main + "}";

        try{
            File file = new File("../out/" + "Program" +".java");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(prog);
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        toGraph(false); //mehhh
        return prog;
    }

    /*Generates a explicit model (Kripke structure) for the complete program*/
   /* public ExplicitCompositeModel toGraph(){
        ExplicitCompositeModel m = new ExplicitCompositeModel(globalVars);

        //states in m are lists of states (from processes)
        //calculate initial state
        CompositeNode init = new CompositeNode(m);
        for (int j=0; j < mainProgram.getProcessDecl().size(); j++){
            ProcessDecl pDecl = mainProgram.getProcessDecl().get(j);
            for (int i = 0; i < process.getProcessList().size(); i++){
                Proc proc = process.getProcessList().get(i);
                if (pDecl.getType().equals(proc.getName())){
                    init.getModel().getProcs().add(proc);
                    init.getModel().getProcDecls().add(pDecl.getName());
                    init.evalInit(proc.getInitialCond(),j);
                }
            }
        }
        
        m.addNode(init);
        m.setInitial(init);

        TreeSet<CompositeNode> iterSet = new TreeSet<CompositeNode>();
        iterSet.add(m.getInitial());

        //build the whole model
        while(!iterSet.isEmpty()){
            CompositeNode curr = iterSet.pollFirst();
            for (int i = 0; i < m.getProcDecls().size(); i++){ // for each process in current global state
                for (Branch b : m.getProcs().get(i).getBranches()){
                    if (b.getIsTau())
                        m.setIsWeak(true);
                    if (curr.satisfies(b.getGuard(),i)){
                        //create global successor curr_
                        CompositeNode curr_ = curr.createSuccessor(b.getAssignList(),i);
                        //curr_.checkNormCondition(m.getProcs().get(i).getNormativeCond(),i);
                        CompositeNode toOld = m.search(curr_);
                        if (toOld == null){
                            m.addNode(curr_);
                            iterSet.add(curr_);
                            m.addEdge(curr, curr_, new Action(m.getProcDecls().get(i)+b.getLabel(),b.getIsFaulty(),b.getIsTau(),isSpec));
                        }
                        else{
                            m.addEdge(curr, toOld, new Action(m.getProcDecls().get(i)+b.getLabel(),b.getIsFaulty(),b.getIsTau(),isSpec));
                        }
                    }
                }
            }
        }
        return m;
    }
*/
    /*TOTRY: Create the whole model on the fly*/
    public ExplicitCompositeModel toGraph(boolean isSpec){
        ExplicitCompositeModel m = new ExplicitCompositeModel(globalVars, isSpec);

        //states in m are lists of states (from processes)
        //calculate initial state
        CompositeNode init = new CompositeNode(m);
        for (int j=0; j < mainProgram.getProcessDecl().size(); j++){
            ProcessDecl pDecl = mainProgram.getProcessDecl().get(j);
            for (int i = 0; i < process.getProcessList().size(); i++){
                Proc proc = process.getProcessList().get(i);
                if (pDecl.getType().equals(proc.getName())){
                    init.getModel().getProcs().add(proc);
                    init.getModel().getProcDecls().add(pDecl.getName());
                    init.evalInit(proc.getInitialCond(),j);
                }
            }
        }
        
        m.addNode(init);
        m.setInitial(init);

        TreeSet<CompositeNode> iterSet = new TreeSet<CompositeNode>();
        iterSet.add(m.getInitial());

        //build the whole model
        while(!iterSet.isEmpty()){
            CompositeNode curr = iterSet.pollFirst();
            for (int i = 0; i < m.getProcDecls().size(); i++){ // for each process in current global state
                for (Branch b : m.getProcs().get(i).getBranches()){
                    if (b.getIsTau())
                        m.setIsWeak(true);
                    if (curr.satisfies(b.getGuard(),i)){
                        //create global successor curr_
                        CompositeNode curr_ = curr.createSuccessor(b.getAssignList(),i);
                        //curr_.checkNormCondition(m.getProcs().get(i).getNormativeCond(),i);
                        CompositeNode toOld = m.search(curr_);
                        if (toOld == null){
                            m.addNode(curr_);
                            iterSet.add(curr_);
                            m.addEdge(curr, curr_, new Action(m.getProcDecls().get(i)+b.getLabel(),b.getIsFaulty(),b.getIsTau(),isSpec));
                        }
                        else{
                            m.addEdge(curr, toOld, new Action(m.getProcDecls().get(i)+b.getLabel(),b.getIsFaulty(),b.getIsTau(),isSpec));
                        }
                    }
                }
            }
        }
        return m;
    }

}
