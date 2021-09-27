#!/usr/bin/python

import sys, getopt
import random
import math

corralitos = []
#visited = []
#rewards = []


corralSyntax = [".","M","C","D","X"]

def genRndBoard(seed) :

    global mice
    global dogs
    global cats 
    global start

    maxReward = 6
    
    # construct the board    
    random.seed(seed)
    for i in range(length) :
        corralitos.append([])
        #visited.append([])
        #rewards.append([])
        for j in range(width) :
            corralitos[i].append(0)   
            #rewards[i].append(math.floor(-math.log(1.0/2.0**(maxReward+1)+random.random()*(1.0-1.0/2.0**(maxReward+1)))/math.log(2.0)))
    # meeting place for mice
    mice = [random.randrange(0,length),random.randrange(0,width)]
    if (corralitos[mice[0]][mice[1]] == 0):
        corralitos[mice[0]][mice[1]] = 1
    # meeting place for cats
    cats = [random.randrange(0,length),random.randrange(0,width)]
    if (corralitos[cats[0]][cats[1]] == 0):
        corralitos[cats[0]][cats[1]] = 2
    # meeting place for dogs
    dogs = [random.randrange(0,length),random.randrange(0,width)]
    if (corralitos[dogs[0]][dogs[1]] == 0):
        corralitos[dogs[0]][dogs[1]] = 3
    # starting point
    start = [random.randrange(0,length),random.randrange(0,width)]
    while corralitos[start[0]][start[1]] > 0:
        start = [random.randrange(0,length),random.randrange(0,width)]
    corralitos[start[0]][start[1]] = 4

def writePreamble(mFile) :

    # a depiction of the board as a comment
    mFile.write("// Board:\n//")
    for i in range(length) :
        mFile.write("\n//   ")
        for j in range(width) :
            #mFile.write("[" + str(int(rewards[i][j])) + "|" + corralSyntax[corralitos[i][j]] + "] ")
            mFile.write("[" + corralSyntax[corralitos[i][j]] + "] ")

    mFile.write("\n\n")


def writeRobotA(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process Robot {\n")
    mFile.write("    col : INT ;    // col in [0,width-1] and row  in [0,length-1] indicate\n")
    mFile.write("    row : INT ;    // the robot's position in the board\n")
    mFile.write("\n")
    mFile.write("    s_dog : BOOL ;  // sense dog\n")
    mFile.write("    s_cat : BOOL ;  // sense cat\n")
    mFile.write("    s_mouse : BOOL ;  // sense mouse\n")
    mFile.write("    h_dog : BOOL ;  // herd dog\n")
    mFile.write("    h_cat : BOOL ;  // herd cat\n")
    mFile.write("    h_mouse : BOOL ;  // herd mouse\n")
    mFile.write("    turn : BOOL ;  // player turn\n")
    mFile.write("    dogs : INT ;  // number of dogs\n")
    mFile.write("    cats : INT ;  // number of cats\n")
    mFile.write("    mice : INT ;  // number of mice\n")
    mFile.write("\n")

    mFile.write("   Rewards :\n")
    #for i in range(length) :
    #    for j in range(width) :
    #        mFile.write("       !(light == 0) && row == "+str(i)+" && col == "+str(j)+" : "+str(int(rewards[i][j]))+" ,\n")
    mFile.write("       true : -1 ,\n")
    mFile.write("       row=="+str(mice[0])+" && col=="+str(mice[1])+" && h_mouse: 5 ,\n")
    mFile.write("       row=="+str(cats[0])+" && col=="+str(cats[1])+" && h_cat: 5 ,\n")
    mFile.write("       row=="+str(dogs[0])+" && col=="+str(dogs[1])+" && h_dog: 5 ;\n\n")
    mFile.write("   Controller : turn;\n")
    mFile.write("   Goal : dogs == 0 && cats == 0 && mice == 0;\n")

    mFile.write("    // Initial configuration\n")
    mFile.write("    Initial : col=="+str(start[0])+" && row=="+str(start[1])+" && !turn && !s_dog && !s_cat && !s_mouse && !h_dog && !h_cat && !h_mouse && mice=="+str(miceCant)+" && dogs=="+str(dogsCant)+" && cats=="+str(catsCant)+";\n")
    mFile.write("\n")

    mFile.write("    // animals movements\n")
    mFile.write("\n")
    mFile.write("    [a_appears] !turn && dogs > 0 ->"+str(probAppear)+": turn = true, s_dog = true\n")
    mFile.write("    ++"+str(1-probAppear)+": turn = true;\n")
    mFile.write("    [a_appears] !turn && cats > 0 ->"+str(probAppear)+": turn = true, s_cat = true\n")
    mFile.write("    ++"+str(1-probAppear)+": turn = true;\n")
    mFile.write("    [a_appears] !turn && mice > 0 ->"+str(probAppear)+": turn = true, s_mouse = true\n")
    mFile.write("    ++"+str(1-probAppear)+": turn = true;\n")
    mFile.write("    [a_follow] !turn -> turn = true;\n")
    mFile.write("    [a_unfollow] !turn && h_dog-> turn = true, h_dog = false;\n")
    mFile.write("    [a_unfollow] !turn && h_cat-> turn = true, h_cat = false;\n")
    mFile.write("    [a_unfollow] !turn && h_mouse-> turn = true, h_mouse = false;\n")
    mFile.write("\n")

    mFile.write("    // robot moves\n")
    mFile.write("\n")

    #for i in range(length) :
    #    for j in range(width) :
    #        mFile.write("    [r_l] turn && (col == "+str(j)+") -> turn = false, col = "+str((j-1)%width)+";\n")
    #        mFile.write("    [r_r] turn && (col == "+str(j)+") -> turn = false, col = "+str((j+1)%width)+";\n")
    #        mFile.write("    [r_u] turn && (row == "+str(i)+") -> turn = false, row = "+str((i-1)%length)+";\n")
    #        mFile.write("    [r_d] turn && (row == "+str(i)+") -> turn = false, row = "+str((i+1)%length)+";\n")



    mFile.write("    [r_l] turn && col > 0 -> turn = false, col = (col - 1);\n")
    mFile.write("    [r_r] turn && col < "+str(width-1)+" -> turn = false, col = (col + 1);\n")
    mFile.write("    [r_u] turn && row > 0 -> turn = false, row = (row - 1);\n")
    mFile.write("    [r_d] turn && row < "+str(length-1)+" -> turn = false, row = (row + 1);\n")

    mFile.write("    [r_herd] turn && s_dog -> turn = false, h_dog = true, s_dog = false;\n")
    mFile.write("    [r_herd] turn && s_cat -> turn = false, h_cat = true, s_cat = false;\n")
    mFile.write("    [r_herd] turn && s_mouse -> turn = false, h_mouse = true, s_mouse = false;\n")        
    mFile.write("    [r_retrieve] turn && h_dog && (row == "+str(dogs[0])+") && (col == "+str(dogs[1])+") -> turn = false, h_dog = false, dogs = dogs -1;\n")
    mFile.write("    [r_retrieve] turn && h_cat && (row == "+str(cats[0])+") && (col == "+str(cats[1])+") -> turn = false, h_cat = false, cats = cats -1;\n")
    mFile.write("    [r_retrieve] turn && h_mouse && (row == "+str(mice[0])+") && (col == "+str(mice[1])+") -> turn = false, h_mouse = false, mice = mice -1;\n")
    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    robot : Robot ;\n")
    mFile.write("    run robot() ;\n")
    mFile.write("}\n")

    mFile.close()


def writeRobotB(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process Robot {\n")
    mFile.write("    col : INT ;    // col in [0,width-1] and row  in [0,length-1] indicate\n")
    mFile.write("    row : INT ;    // the robot's position in the board\n")
    mFile.write("\n")
    mFile.write("    s_dog : BOOL ;  // sense dog\n")
    mFile.write("    h_dog : BOOL ;  // herd dog\n")
    mFile.write("    turn : BOOL ;  // player turn\n")
    mFile.write("    dogs : INT ;  // number of dogs\n")
    mFile.write("\n")

    mFile.write("   Rewards :\n")
    #for i in range(length) :
    #    for j in range(width) :
    #        mFile.write("       !(light == 0) && row == "+str(i)+" && col == "+str(j)+" : "+str(int(rewards[i][j]))+" ,\n")
    mFile.write("       !(row=="+str(dogs[0])+" && col=="+str(dogs[1])+" && h_dog) : -1 ,\n")
    mFile.write("       row=="+str(dogs[0])+" && col=="+str(dogs[1])+" && h_dog: 5 ;\n\n")
    mFile.write("   Controller : turn;\n")
    mFile.write("   Goal : dogs == 0;\n")

    mFile.write("    // Initial configuration\n")
    mFile.write("    Initial : col=="+str(start[0])+" && row=="+str(start[1])+" && !turn && !s_dog && !h_dog  && dogs=="+str(dogsCant)+";\n")
    mFile.write("\n")

    mFile.write("    // animals movements\n")
    mFile.write("\n")
    mFile.write("    [a_appears] !turn && dogs > 0 ->"+str(probAppear)+": turn = true, s_dog = true\n")
    mFile.write("    ++"+str(1-probAppear)+": turn = true;\n")
    mFile.write("    [a_follow] !turn -> turn = true;\n")
    mFile.write("    [a_unfollow] !turn && h_dog-> turn = true, h_dog = false;\n")
    mFile.write("\n")

    mFile.write("    // robot moves\n")
    mFile.write("\n")


    mFile.write("    [r_l] turn && col > 0 -> turn = false, col = (col - 1);\n")
    mFile.write("    [r_r] turn && col < "+str(width-1)+" -> turn = false, col = (col + 1);\n")
    mFile.write("    [r_u] turn && row > 0 -> turn = false, row = (row - 1);\n")
    mFile.write("    [r_d] turn && row < "+str(length-1)+" -> turn = false, row = (row + 1);\n")

    mFile.write("    [r_herd] turn && s_dog -> turn = false, h_dog = true, s_dog = false;\n")     
    mFile.write("    [r_retrieve] turn && h_dog && (row == "+str(dogs[0])+") && (col == "+str(dogs[1])+") -> turn = false, h_dog = false, dogs = dogs -1;\n")
    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    robot : Robot ;\n")
    mFile.write("    run robot() ;\n")
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
    global dogsCant
    global catsCant
    global miceCant
    global start
    global dogs
    global cats
    global mice

    width = 20
    length = 20
    probAppear = 0.3
    dogsCant = 5
    catsCant = 5
    miceCant = 5
    seed = 0
    mice = [0,0]
    cats = [0,0]
    dogs = [0,0]
    start = [0,0]
    path = "../synthesis/herding/"


    try:
        opts, args = getopt.getopt(argv,"hs:w:l:p:d:c:m",["seed=","width=","length=","prob_appear_animal","dogs=","cats=","mice="])
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
        elif opt in ("-p","prob_appear_animal=") :
            try :
                probAppear = float(arg)
            except ValueError :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
            if probAppear <= 0 or probAppear >= 1 :
                print("The probability must be a float in (0,1)")
                sys.exit(2)
        elif opt in ("-d","dogs=") :
            try :
                dogsCant = int(arg)
            except ValueError :
                print("The number must be a positive integer")
                sys.exit(2)            
            if length <= 0 :
                print("The number must be a positive integer")
                sys.exit(2)
        elif opt in ("-c","cats=") :
            try :
                catsCant = int(arg)
            except ValueError :
                print("The number must be a positive integer")
                sys.exit(2)            
            if length <= 0 :
                print("The number must be a positive integer")
                sys.exit(2)
        elif opt in ("-m","mice=") :
            try :
                miceCant = int(arg)
            except ValueError :
                print("The number must be a positive integer")
                sys.exit(2)            
            if length <= 0 :
                print("The number must be a positive integer")
                sys.exit(2)

    genRndBoard(seed)

    writeRobotB(path+"HerdingRobot["+str(seed)+"-"+str(width)+"-"+str(length)+"-"+str(probAppear)+"-"+str(dogsCant)+"-"+str(catsCant)+"-"+str(miceCant)+"].sgg")



main(sys.argv[1:])
