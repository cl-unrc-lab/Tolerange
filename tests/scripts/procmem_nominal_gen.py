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
    mFile.write("    v: INT; //0..0 amount of faulty voters, a faulty voter outputs a value other than the majority of the input\n")
    mFile.write("    p: INT; //0..0 amount of faulty processors, a faulty processor outputs a value other than the correct one\n")
    mFile.write("    s:INT; //0..2    0=normal, 1=faulty processor, 2=faulty voter\n")
    mFile.write("\n")
    
    mFile.write("    Initial : v==0 && p==0 && s==0;\n")
    mFile.write("\n")

    mFile.write("    // communication\n")
    mFile.write("\n")
    mFile.write("    [pToM] true -> s=s; //good\n")
    mFile.write("\n")

    mFile.write("    // tick\n")
    mFile.write("\n")
    mFile.write("    [tick] <1> true -> v=v;\n")
    mFile.write("\n")

    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    m : ProcMemNMR ;\n")
    mFile.write("    run m() ;\n")
    mFile.write("}\n")

    mFile.close()




def main(argv):

    path = "../tests/stochastic/nominal/nmr-proc-mem/"
    writeNMR(path+"nmr-proc-mem[].mdp")



main(sys.argv[1:])
