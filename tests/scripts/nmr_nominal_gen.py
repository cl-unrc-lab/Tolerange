#!/usr/bin/python

import sys, getopt
import random
import math

def writePreamble(mFile) :

    # a depiction of the board as a comment
    mFile.write("// N-Modular Redundancy\n")


def writeNMR(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process NMR {\n")
    mFile.write("    v: INT; //0..1\n")
    mFile.write("\n")
    
    mFile.write("    Initial : v==0;\n")
    mFile.write("\n")

    mFile.write("    // environment input\n")
    mFile.write("\n")
    mFile.write("    [i0] true -> v=0;\n")
    mFile.write("    [i1] true -> v=1;\n")
    mFile.write("\n")

    mFile.write("    // vote\n")
    mFile.write("\n")
    mFile.write("    [v0] v==0 -> v=v;\n")
    mFile.write("    [v1] v==1 -> v=v;\n")
    mFile.write("\n")

    mFile.write("    // tick\n")
    mFile.write("\n")
    mFile.write("    [tick] <1> true -> v=v;\n")
    mFile.write("\n")

    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    m : NMR ;\n")
    mFile.write("    run m() ;\n")
    mFile.write("}\n")

    mFile.close()




def main(argv):

    path = "../tests/stochastic/nominal/nmr/"
    writeNMR(path+"nmr.pts")



main(sys.argv[1:])
