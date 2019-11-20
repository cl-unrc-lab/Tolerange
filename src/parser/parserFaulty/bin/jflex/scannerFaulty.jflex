package parserProgram;

import java_cup.runtime.*;
%%

%public
%cup
%line
%column
%char
%full

%eofval{
return  new Symbol(symFaulty.EOF,yyline,yycolumn,"");
%eofval}

%class scannerFaulty
%{
	public scannerFaulty(java.io.InputStream r, SymbolFactory sf){
		this(r);
		this.sf=sf;
	}
	private SymbolFactory sf;
%}



digit=[0-9]
char=[a-zA-Z]
id = {char}({char}|{digit})*	
integer = {digit}{digit}*	
comment=("//"(.)*)|"/*"(((([^*/])*"*"[^/])*)|((([^*])*"/"[^*/])*)|([^*/])*)*"*/"
/* To ignore */
end_of_line   = \r|\n|\r\n
white_space     = {end_of_line} | [ \t\f]

%%


 
{comment} {}

"+" {   //System.out.println("SUMA:"+ yytext()); 
        return new Symbol(symFaulty.PLUS,yyline,yycolumn,yytext());
    }
    
"-" {    //System.out.println("RESTA:"+ yytext()); 
        return new Symbol(symFaulty.MINUS,yyline,yycolumn, yytext());
    }
"*" {    //System.out.println("PROD:"+ yytext());
        return new Symbol(symFaulty.ASTERISK,yyline,yycolumn,yytext());
    }
    
"/" {    //System.out.println("DIV:"+ yytext());
        return new Symbol(symFaulty.SLASH,yyline,yycolumn,yytext());
    }
    
"(" {   //System.out.println("LPARENT:"+ yytext());
        return new Symbol(symFaulty.LPARENT,yyline,yycolumn,yytext());
    }
    
")" {   //System.out.println("RPARENT:"+ yytext());
        return new Symbol(symFaulty.RPARENT,yyline,yycolumn,yytext());
    }
    
";" {  //System.out.println("PUNTOYCOMA: "+ yytext());
        return new Symbol(symFaulty.SEMICOLON,yyline,yycolumn,yytext());
    }
    
":" {    //System.out.println(" DOSPUNTOS: "+ yytext());
        return new Symbol(symFaulty.COLON,yyline,yycolumn,yytext());
    }
    
"->" {  //System.out.println("FLECHA: "+ yytext());
        return new Symbol(symFaulty.RIGHTARROW,yyline,yycolumn,yytext());
     }
"=" {    //System.out.println(" ASIG:"+ yytext());
        return new Symbol(symFaulty.EQUAL,yyline,yycolumn,yytext());
    }
    
"==" { //System.out.println(" IGUAL:"+ yytext());
       return new Symbol(symFaulty.DOUBLE_EQUAL,yyline,yycolumn,yytext());
     }
     
"," {   //System.out.println(" COMA:"+ yytext());
        return new Symbol(symFaulty.COMMA,yyline,yycolumn,yytext());
    }
    
"||" {   //System.out.println(" OR:"+ yytext());
        return new Symbol(symFaulty.OR,yyline,yycolumn,yytext());
     }
     
"&&" {   //System.out.println(" AND:"+ yytext());
        return new Symbol(symFaulty.AND,yyline,yycolumn,yytext());
     }
     
">" {    //System.out.println("MAYOR :"+ yytext());
        return new Symbol(symFaulty.GT,yyline,yycolumn,yytext());
    }
    
"<" {    //System.out.println(" MENOR :"+ yytext());
        return new Symbol(symFaulty.LT,yyline,yycolumn,yytext());
    }
    
"!" {    //System.out.println(" NEG:"+ yytext());
        return new Symbol(symFaulty.EXCLAMATION,yyline,yycolumn,yytext());
    }
    
"{" {   //System.out.println(" LLAVEABRE:"+ yytext());
        return new Symbol(symFaulty.LBRACE,yyline,yycolumn,yytext());
    }

"}" {    //System.out.println(" LLAVECIERRA:"+ yytext());
        return new Symbol(symFaulty.RBRACE,yyline,yycolumn,yytext());
    }

"[" {  //System.out.println(" CORCHETE ABRE:"+ yytext());
        return new Symbol(symFaulty.LBRACKET,yyline,yycolumn,yytext());
    }

"]" {    //System.out.println(" CORCHETE CIERRA:"+ yytext());
        return new Symbol(symFaulty.RBRACKET,yyline,yycolumn,yytext());
    }

"." {   //System.out.println(" PUNTO :"+ yytext());
        return new Symbol(symFaulty.POINT,yyline,yycolumn,yytext());
    }
    
"true"  {   //System.out.println(" TRUE :"+ yytext()); 
           return new Symbol(symFaulty.TRUE,yyline,yycolumn, yytext());
         }
         
"false"  {  //System.out.println(" FALSE :"+ yytext());
           return new Symbol(symFaulty.FALSE,yyline,yycolumn, yytext());
         }
         
"BOOL"  { //System.out.println(" BOOL:"+ yytext());
           return new Symbol(symFaulty.BOOL,yyline,yycolumn,yytext());
        }
        
"INT" {  //System.out.println(" INT:"+ yytext());
         return new Symbol(symFaulty.INT,yyline,yycolumn,yytext());
      }
      
"Global" { //System.out.println(" GLOBAL :"+ yytext());
           return new Symbol(symFaulty.GLOBAL,yyline,yycolumn,yytext());
        }

      
"Enum" { //System.out.println(" ENUM :"+ yytext());
           return new Symbol(symFaulty.ENUM,yyline,yycolumn,yytext());
        }


"Initial" { //System.out.println(" INIT:"+ yytext());
            return new Symbol(symFaulty.INIT,yyline,yycolumn,yytext());
          }

"Normative" { //System.out.println(" NORMATIVE:"+ yytext());
              return new Symbol(symFaulty.NORMATIVE,yyline,yycolumn,yytext());
            }
       
"put" {   //System.out.println(" PUT :"+ yytext());
          return new Symbol(symFaulty.PUT,yyline,yycolumn,yytext());
       }

"get" {    //System.out.println(" GET:"+ yytext());
           return new Symbol(symFaulty.GET,yyline,yycolumn,yytext());
       }

"of" {   //System.out.println(" OF :"+ yytext());
         return new Symbol(symFaulty.OF,yyline,yycolumn,yytext());
     }
       
"Process" { //System.out.println(" PROCESS :"+ yytext());
            return new Symbol(symFaulty.PROCESS,yyline,yycolumn,yytext());
          }

"uses" {   //System.out.println(" USES:"+ yytext());
           return new Symbol(symFaulty.USES,yyline,yycolumn,yytext());
       }
       

"Main" {  //System.out.println(" MAIN :"+ yytext());
          return new Symbol(symFaulty.MAIN,yyline,yycolumn,yytext());
       }


"run" {   //System.out.println(" RUN:"+ yytext());
          return new Symbol(symFaulty.RUN,yyline,yycolumn,yytext());
       }
       
"Channel" {  //System.out.println(" CHANNEL:"+ yytext()); 
             return new Symbol(symFaulty.CHANNEL,yyline,yycolumn,yytext());
          }
          
{id}  {  //System.out.println("ID :  "+ yytext());
         return new Symbol(symFaulty.ID,yyline,yycolumn,yytext());
      }	
 
       
{integer} {  //System.out.println(" VALORINT:"+ yytext());
            Integer value = new Integer(yytext());
            return new Symbol(symFaulty.INTEGER,yyline,yycolumn,value);
          }
          
{white_space} {/* Ignore */} 

.     {   //return new Symbol(symFaulty.LEXICAL_ERROR,yyline,yycolumn,yytext());
       }