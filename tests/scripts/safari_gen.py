#!/usr/bin/python

import sys, getopt
import random
import math

regions = []
#visited = []
#rewards = []


regionSyntax = [".","M","C","D","X"]

def genRndBoard(seed) :

    global start

    maxRegion = 3
    
    # construct the board    
    random.seed(seed)
    for i in range(length) :
        regions.append([])
        #visited.append([])
        #rewards.append([])
        for j in range(width) :
            regions[i].append(math.floor(-math.log(1.0/2.0**(maxRegion+1)+random.random()*(1.0-1.0/2.0**(maxRegion+1)))/math.log(2.0))) 
    # starting point
    # start = [random.randrange(0,length),random.randrange(0,width)]
    start = (0,0)
    regions[start[0]][start[1]] = 4

def writePreamble(mFile) :

    # a depiction of the board as a comment
    mFile.write("// Board:\n//")
    for i in range(length) :
        mFile.write("\n//   ")
        for j in range(width) :
            #mFile.write("[" + str(int(rewards[i][j])) + "|" + regionSyntax[regions[i][j]] + "] ")
            mFile.write("[" + regionSyntax[regions[i][j]] + "] ")

    mFile.write("\n\n")


def writeSafari(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process Safari {\n")
    mFile.write("    col : INT ;    // col in [0,width-1] and row  in [0,length-1] indicate\n")
    mFile.write("    row : INT ;    // the player's position in the board\n")
    mFile.write("    balls : INT ;    // the player's pokeballs\n")
    mFile.write("    bait : INT ;    // how much bait used in battle (max 3)\n")
    mFile.write("\n")
    mFile.write("    s_poke : BOOL ;  // sense pokemon\n")
    
    mFile.write("    c_poke : BOOL ;  // catch pokemon\n")
    mFile.write("    ctrl : BOOL ;  // player ctrl\n")
    mFile.write("\n")

    mFile.write("   Rewards :\n")
    #for i in range(length) :
    #    for j in range(width) :
    #        mFile.write("       !(light == 0) && row == "+str(i)+" && col == "+str(j)+" : "+str(int(rewards[i][j]))+" ,\n")
    #mFile.write("       !s_poke : -1 ,\n")
    mFile.write("       c_poke : 10 ;\n\n")
    mFile.write("   Controller : ctrl;\n")
    mFile.write("   Goal : balls == 0;\n")

    mFile.write("    // Initial configuration\n")
    mFile.write("    Initial : col=="+str(start[0])+" && row=="+str(start[1])+" && ctrl && !s_poke && !c_poke && bait==0 && balls=="+str(ballsCant)+";\n")
    mFile.write("\n")

    mFile.write("    // environment moves\n")
    mFile.write("\n")
    for i in range(length) :
        for j in range(width) :
            if regions[i][j] == 0 or regions[i][j] == 4:
                mFile.write("    [e_appears] !ctrl && !s_poke && (row == "+str(i)+") && (col == "+str(j)+") -> ctrl = true;\n")
            if regions[i][j] == 1 :
                mFile.write("    [e_appears] !ctrl && !s_poke && (row == "+str(i)+") && (col == "+str(j)+") -> "+str(probAppear)+": s_poke = true //if appears then first turn for pokemon\n")
                mFile.write("    ++"+str(1-probAppear)+": ctrl = true;\n")
            if regions[i][j] == 2 :
                mFile.write("    [e_appears] !ctrl && !s_poke && (row == "+str(i)+") && (col == "+str(j)+") -> "+str(probAppear/2)+": s_poke = true //if appears then first turn for pokemon\n")
                mFile.write("    ++"+str(1-(probAppear/2))+": ctrl = true;\n")
            if regions[i][j] == 3 :
                mFile.write("    [e_appears] !ctrl && !s_poke && (row == "+str(i)+") && (col == "+str(j)+") -> "+str(probAppear/3)+": s_poke = true //if appears then first turn for pokemon\n")
                mFile.write("    ++"+str(1-(probAppear/3))+": ctrl = true;\n")

    mFile.write("    [e_idle] !ctrl && s_poke -> ctrl = true;\n")
    mFile.write("    [e_escape] !ctrl && s_poke && bait == 0 -> ctrl = true, s_poke = false;\n")
    mFile.write("    [e_escape] !ctrl && s_poke && bait == 1 -> 0.75 : ctrl = true, s_poke = false, bait = 0 //if baited, there is less chance of escape\n")
    mFile.write("    ++ 0.25: ctrl = true;\n")
    mFile.write("    [e_escape] !ctrl && s_poke && bait == 2 -> 0.5 : ctrl = true, s_poke = false, bait = 0 //if baited, there is less chance of escape\n")
    mFile.write("    ++ 0.5: ctrl = true;\n")
    mFile.write("    [e_escape] !ctrl && s_poke && bait == 3 -> 0.25 : ctrl = true, s_poke = false, bait = 0 //if baited, there is less chance of escape\n")
    mFile.write("    ++ 0.75: ctrl = true;\n")
    mFile.write("    [e_catched] !ctrl && c_poke -> ctrl=true, bait = 0, c_poke = false;\n")
    mFile.write("\n")

    mFile.write("    // player moves\n")
    mFile.write("\n")

    #for i in range(length) :
    #    for j in range(width) :
    #        mFile.write("    [r_l] (light == 1) && (row == "+str(i)+") && (col == "+str(j)+") -> "+str(1-probRobot)+" : light = 0, col = "+str((j-1)%width)+" ++ "+str(probRobot)+" : light = 0 ;\n")
    #        mFile.write("    [r_r] (light == 1) && (row == "+str(i)+") && (col == "+str(j)+") -> "+str(1-probRobot)+" : light = 0, col = "+str((j+1)%width)+" ++ "+str(probRobot)+" : light = 0 ;\n")

    mFile.write("    [p_l] ctrl && !s_poke && col > 0 -> ctrl = false, col = (col - 1);\n")
    mFile.write("    [p_r] ctrl && !s_poke && col < "+str(width-1)+" -> ctrl = false, col = (col + 1);\n")
    mFile.write("    [p_u] ctrl && !s_poke && row > 0 -> ctrl = false, row = (row - 1);\n")
    mFile.write("    [p_d] ctrl && !s_poke && row < "+str(length-1)+" -> ctrl = false, row = (row + 1);\n")

    mFile.write("    [p_ball] ctrl && s_poke && balls > 0 -> "+str(probCatch)+" : ctrl = false, c_poke = true, s_poke = false, balls = balls - 1\n")
    mFile.write("    ++ "+str(1-probCatch)+": ctrl = true, balls = balls - 1;\n")
    mFile.write("    [p_ball] ctrl && s_poke && balls == 0 -> ctrl = false; //END\n")
    mFile.write("    [p_bait] ctrl && s_poke && bait < 3 -> ctrl = false, bait = bait + 1;\n")
    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    safari : Safari ;\n")
    mFile.write("    run safari() ;\n")
    mFile.write("}\n")

    mFile.close()






def usage(exitVal) :

    print("\nusage robot_gen.py [-h] [-s <int> ] [-w <int>] [-l <int>] [-p <float>] [-d <int>] [-c <int>] [-m <int>]\n")
    print("-h :\n")
    print("  Print this help\n")
    print("-r :\n")
    print("  Exclude the rewards setting\n")
    print("-s num :\n")
    print("  Sets the seed for the pseudo-random number generator to 'num'. 'num' must be")
    print("  a non-negative integer (0 or higher) (default = 0)\n")
    print("-w num :\n")
    print("  Sets the width of the board to 'num'. 'num' must be a positive integer")
    print("  (greater than 0) (default = 5)\n")
    print("-l num :\n")
    print("  Sets the length of the board to 'num'. 'num' must be a positive integer")
    print("  (greater than 0) (default = 10)\n")
    print("-p num :\n")
    print("  Sets the appear probability of the animals to 'num'. 'num' must be a float")
    print("  in the interval (0,1) (default = 0.1)\n")
    print("-d num :\n")
    print("  Sets the number of dogs to 'num'. 'num' must be a positive integer")
    print("  (greater than 0) (default = 10)\n")
    print("-c num :\n")
    print("  Sets the number of cats to 'num'. 'num' must be a positive integer")
    print("  (greater than 0) (default = 10)\n")
    print("-m num :\n")
    print("  Sets the number of mice to 'num'. 'num' must be a positive integer")
    print("  (greater than 0) (default = 10)\n")

    sys.exit(exitVal)



def main(argv):

    global width
    global length
    global probAppear
    global probCatch
    global ballsCant
    global start

    width = 20
    length = 20
    probAppear = 0.3
    probCatch = 0.25
    ballsCant = 30
    seed = 0
    start = [0,0]
    path = "../synthesis/safari/"


    try:
        opts, args = getopt.getopt(argv,"hs:w:l:p:q:b:",["seed=","width=","length=","prob_appear","prob_catch","balls="])
    except getopt.GetoptError:
        usage(2)
    for opt, arg in opts:
        if opt == '-h':
            usage(0)
        elif opt in ("-s","seed=") :
            try :
                seed = int(arg)
            except ValueError :
                print("The width must be a nonnegative integer")
                sys.exit(2)            
            if seed < 0 :
                print("The width must be a nonnegative integer")
                sys.exit(2)
        elif opt in ("-w","width=") :
            try :
                width = int(arg)
            except ValueError :
                print("The width must be a positive integer")
                sys.exit(2)            
            if width <= 0 :
                print("The width must be a positive integer")
                sys.exit(2)
        elif opt in ("-l","length=") :
            try :
                length = int(arg)
            except ValueError :
                print("The length must be a positive integer")
                sys.exit(2)            
            if length <= 0 :
                print("The length must be a positive integer")
                sys.exit(2)
        elif opt in ("-p","prob_appear=") :
            try :
                probAppear = float(arg)
            except ValueError :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
            if probAppear <= 0 or probAppear >= 1 :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
        elif opt in ("-q","prob_catch=") :
            try :
                probCatch = float(arg)
            except ValueError :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
            if probCatch <= 0 or probCatch >= 1 :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
        elif opt in ("-b","balls=") :
            try :
                ballsCant = int(arg)
            except ValueError :
                print("The number must be a positive integer")
                sys.exit(2)            
            if ballsCant <= 0 :
                print("The number must be a positive integer")
                sys.exit(2)


    genRndBoard(seed)

    writeSafari(path+"Safari["+str(seed)+"-"+str(width)+"-"+str(length)+"-"+str(probAppear)+"-"+str(probCatch)+"-"+str(ballsCant)+"].sgg")



main(sys.argv[1:])
