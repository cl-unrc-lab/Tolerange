#!/usr/bin/python

import sys, getopt
import random
import math

def writePreamble(mFile) :

    # a depiction of the board as a comment
    mFile.write("// N-Modular Redundancy: N Processors, N Voters, 1 Memory Module\n")


def writeNMR(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process ProcMemNMR {\n")
    mFile.write("    v: INT; //0.."+str(modules)+"amount of faulty voters, a faulty voter outputs a value other than the majority of the input\n")
    mFile.write("    p: INT; //0.."+str(modules)+"amount of faulty processors, a faulty processor outputs a value other than the correct one\n")
    mFile.write("    s:INT; //0..2    0=normal, 1=faulty processor, 2=faulty voter\n")
    mFile.write("\n")
    
    mFile.write("    Initial : v==0 && p==0 && s==0;\n")
    mFile.write("\n")

    mFile.write("    // communication\n")
    mFile.write("\n")
    mFile.write("    [pToM] p<="+str(modules//2)+" && v<="+str(modules//2)+"-> s=s; //good\n")
    mFile.write("\n")

    mFile.write("    // tick\n")
    mFile.write("\n")
    mFile.write("    [tick] <1> s==0 ->"+str(1-probFaultP-probFaultV)+": v=v\n")
    mFile.write("                    ++ "+str(probFaultP)+": s=1\n")
    mFile.write("                    ++ "+str(probFaultV)+": s=2;\n")
    mFile.write("    [tick] <1> !(s==0) -> v=v;\n")
    mFile.write("\n")

    mFile.write("    // faults\n")
    mFile.write("\n")
    mFile.write("    [faultProc] faulty s==1 && p<"+str(modules)+"-> s=0, p=p+1;\n")
    mFile.write("    [faultVot] faulty s==2 && v<"+str(modules)+"-> s=0, v=v+1;\n")
    mFile.write("\n")

    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    m : ProcMemNMR ;\n")
    mFile.write("    run m() ;\n")
    mFile.write("}\n")

    mFile.close()




def usage(exitVal) :

    print("\nusage procmem_faulty_gen.py [-h]\n")
    print("-h :\n")
    print("  Print this help\n")
    print("-m num :\n")
    print("  Sets the number of modules\n")
    print("-p num :\n")
    print("  Sets the failure probability of a processor to fail to 'num'. 'num' must be a float")
    print("  in the interval (0,1) (default = 0.1)\n")
    print("-q num :\n")
    print("  Sets the failure probability of a voter to fail to 'num'. 'num' must be a float")
    print("  in the interval (0,1) (default = 0.1)\n")
    sys.exit(exitVal)



def main(argv):

    global modules
    global probFaultP
    global probFaultV

    modules = 3
    probFaultP = 0.05
    probFaultV = 0.05
    path = "../tests/stochastic/faulty/nmr-proc-mem/"

    try:
        opts, args = getopt.getopt(argv,"m:p:q:",["modules=","prob_fault_p","prob_fault_v"])
    except getopt.GetoptError:
        usage(2)
    for opt, arg in opts:
        if opt == '-h':
            usage(0)
        elif opt in ("-m","modules=") :
            try :
                modules = int(arg)
            except ValueError :
                print("The number must be a nonnegative integer")
                sys.exit(2)            
            if modules < 0 :
                print("The number must be a nonnegative integer")
                sys.exit(2)
        elif opt in ("-p","prob_fault_p=") :
            try :
                probFaultP = float(arg)
            except ValueError :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
            if probFaultP <= 0 or probFaultP >= 1 :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
        elif opt in ("-q","prob_fault_v=") :
            try :
                probFaultV = float(arg)
            except ValueError :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
            if probFaultV <= 0 or probFaultV >= 1 :
                print("The probability must be a float in (0,1)")
                sys.exit(2)


    writeNMR(path+"nmr-proc-mem_"+str(modules)+"-"+str(probFaultP)+"-"+str(probFaultV)+".pts")



main(sys.argv[1:])
