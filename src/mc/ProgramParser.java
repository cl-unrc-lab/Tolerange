package mc;

import java_cup.runtime.*;
import java.io.*;
import java.util.LinkedList;
import faulty.*;
import parserProgram.*;

/**
 * This class represents the compiler.
 */
public class ProgramParser {
	
	private parserFaulty parser;	// Parser
	private static LinkedList<faulty.Error> errorList; // Errors
    private static SymbolsTable symbolsTable;
    private static FileReader programFile;
    
    
    public ProgramParser(){
    	this.programFile=null;
    	this.symbolsTable =null;
    	this.errorList= null;
    }
    
    

    public String parseJava(String NameFile){
        try {
            programFile = new FileReader(NameFile);
           

            // Read file
            parser = new parserFaulty(new scannerFaulty(programFile));
            Program program = (Program)parser.parse().value;

             
            
            // Check Types
            Type result = checkTypes(program);
            if(result == Type.ERROR){
                for(int i=0; i<errorList.size(); i++){
                    System.out.println(errorList.get(i).getErrorMsg());
                }               
            }
            else
                return program.toJava(); 
        } catch (Exception e) {         
            System.out.println("Program Error." + e.getMessage());
            e.printStackTrace(System.out);
        }
        
        return null;
        
    }

    public Program parseAux(String NameFile){
        try {
            programFile = new FileReader(NameFile);
        
            // Read file
            parser = new parserFaulty(new scannerFaulty(programFile));
            Program program = (Program)parser.parse().value;
            
            // Check Types
            Type result = checkTypes(program);
            if(result == Type.ERROR){
                for(int i=0; i<errorList.size(); i++){
                    System.out.println(errorList.get(i).getErrorMsg());
                }               
            }
            else
                return program; 
        } catch (Exception e) {         
            System.out.println("Program Error." + e.getMessage());
            e.printStackTrace(System.out);
        }
        
        return null;
        
    }

    /**
     * 
     * @return Return the symbols table of the model.
     */
    public static SymbolsTable getSymbolsTable(){
    	return symbolsTable;
    }
    
 	

 	/**
 	 * Check types
 	 */
 	private static Type checkTypes(Program prog) {
        TypeCheckerVisitor typeV = new TypeCheckerVisitor();
        prog.accept(typeV);
        Type result = typeV.getType();
        errorList = typeV.getErrorList();
        symbolsTable =typeV.getSymbolTable();
        return result;
 	}
 	
}