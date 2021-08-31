#!/usr/bin/python

import sys, getopt
import random
import math

roads = [[]]
checkpoints = []
cpRewards = []

def genRndBoard(seed) :
    
    # construct the road map    
    random.seed(seed)
    for i in range(maxWaypoint) :
        checkpoints.append(random.randrange(0,2))
        cpRewards.append(random.randrange(0,maxRewards))
        roads.append([])
        for j in range(maxWaypoint) :
            if i!=j:
                roads[i].append(random.randrange(-1,maxDistance))
            else:
                roads[i].append(-1)

def writePreamble(mFile) :

    # a depiction of the board as a comment
    mFile.write("// Map:\n//")
    for i in range(maxWaypoint) :
        mFile.write("\n// ")
        for j in range(maxWaypoint-i) :
            mFile.write("[w" + str(i) + "-w" + str(j+i) + " road length:"+str(int(roads[i][j+i])) + "] ")

    mFile.write("\n\n")


def writeUAV_1(fileName) :

    mFile = open(fileName,"w")

    writePreamble(mFile)

    mFile.write("Process UAVOperator {\n")
    mFile.write("    wc : INT ;    // waypoint current, -1 : null\n")
    mFile.write("    wd : INT ;    // waypoint destination, -1 : null\n")
    mFile.write("\n")
    mFile.write("    a : INT ;    // angles, -1 : null\n")
    mFile.write("    r : INT ;    // length of road left to destination\n")
    mFile.write("    photos : INT ;    // photos\n")
    mFile.write("    state: INT ; // 0:init,1:low,2:high,3:bad,4:good,5:exit,6:del,7:ndel,8:flying,9:arrival\n")
    mFile.write("    ctrl : BOOL ;  // player turn\n")
    mFile.write("\n")

    mFile.write("   Rewards :\n")
    cpCounter = 0
    for i in range(maxWaypoint) :
        if checkpoints[i]==1:
            cpCounter += 1
            if cpCounter == sum(checkpoints):
                mFile.write("       state==4 && wc=="+str(i)+" : "+str(int(cpRewards[i]))+" ;\n\n")
            else:
                mFile.write("       state==4 && wc=="+str(i)+" : "+str(int(cpRewards[i]))+" ,\n")

    mFile.write("   Controller : ctrl;\n")
    mFile.write("   Goal : photos == 0;\n")

    mFile.write("    // Initial State\n")
    mFile.write("   Initial : wc==0 && wd==-1 && r==0 && a==-1 && ctrl && photos=="+str(photoLimit)+" && state==0;\n")
    mFile.write("\n")

    mFile.write("    // environment moves\n")
    mFile.write("\n")
    mFile.write("    [lowFatigue] !ctrl && !(wc==-1) && state == 0 -> state = 1;\n")
    mFile.write("    [highFatigue] !ctrl && !(wc==-1) && state == 0 -> state = 2;\n")
    mFile.write("    [takePicHigh] !ctrl && !(wc==-1) && state == 2 -> state = 3;\n")
    mFile.write("    [takePicLow] !ctrl && state == 1 -> "+str(probBad)+": state = 3 ++  "+str(1-probBad)+": state = 4, photos = photos - 1;\n")
    mFile.write("    [loiter] !ctrl && state == 3 -> state = 0;\n")
    for i in range(maxWaypoint) :
        if checkpoints[i]==1:
            mFile.write("    [delegate] !ctrl && state == 5 && wc == "+str(i)+" -> "+str(probDel)+": state = 6,ctrl = true ++ "+str(1-probDel)+": state = 7;\n")
        else:
            mFile.write("    [ndelegate] !ctrl && state == 5 && wc == "+str(i)+" -> state = 7;\n")
    mFile.write("    [finish] !ctrl && state == 4 && photos == 0 -> ctrl = true, state = 0;\n")
    for i in range(8) :
        mFile.write("    [exit_"+str(i+1)+"] !ctrl && state == 4 && photos > 0 && !(wc == -1) && a == -1 -> state = 5, a = "+str(i+1)+";\n")
    for i in range(maxWaypoint) :
        for j in range(maxWaypoint) :
            if roads[i][j] >= 0:
                mFile.write("    [fly_"+str(i)+"_"+str(j)+"] !ctrl && state == 7 && wc == "+str(i)+" -> ctrl = true, state = 8, a = -1, r = "+str(roads[i][j])+", wd = "+str(j)+";\n")
                #mFile.write("    [fly_"+str(j)+"_"+str(i)+"] !ctrl && state == 7 && wc == "+str(j)+" -> ctrl = true, state = 8, a = -1, r = "+str(roads[i][j])+", wd = "+str(i)+";\n")
    mFile.write("\n")

    mFile.write("    // controller moves\n")
    mFile.write("\n")
    mFile.write("    [ready] ctrl && !(wc == -1) && state == 0 && photos > 0 -> ctrl = false, a = -1;\n")
    mFile.write("    [finish] ctrl && !(wc == -1) && state == 0 && photos == 0 -> state = 0; // loop forever\n")
    for i in range(maxWaypoint) :
        for j in range(maxWaypoint) :
            if roads[i][j] >= 0:
                mFile.write("    [fly_"+str(i)+"_"+str(j)+"] ctrl && state == 6 && wc == "+str(i)+" -> ctrl = true, state = 8, a = -1, r = "+str(roads[i][j])+", wd = "+str(j)+";\n")
                #mFile.write("    [fly_"+str(j)+"_"+str(i)+"] ctrl && state == 6 && wc == "+str(j)+" -> ctrl = true, state = 8, a = -1, r = "+str(roads[i][j])+", wd = "+str(i)+";\n")
    mFile.write("    [onroad] ctrl && state == 8 && r > 0 -> r = r-1;\n")
    for i in range(8) :
        mFile.write("    [enter_"+str(i+1)+"] ctrl && state == 8 && r == 0 && a == -1 -> a = "+str(i+1)+", state = 9;\n")
    mFile.write("    [waypoint] ctrl && state == 9 -> wd = -1, wc = wd, state = 0;\n")
    mFile.write("}\n\n")

    mFile.write("Main() {\n")
    mFile.write("    uav : UAVOperator ;\n")
    mFile.write("    run uav() ;\n")
    mFile.write("}\n")

    mFile.close()





def usage(exitVal) :

    print("\nusage uavOperator_gen.py [-h] [-s <int> ] [-w <int>] [-d <int>] [-r <int>] [-p <float>] [-q <float>]\n")
    print("-h :\n")
    print("  Print this help\n")
    print("-s num :\n")
    print("  Sets the seed for the pseudo-random number generator to 'num'. 'num' must be")
    print("  a non-negative integer (0 or higher) (default = 0)\n")
    print("-w num :\n")
    print("  Sets the number of waypoints of the map to 'num'. 'num' must be a positive integer")
    print("  (greater than 0) (default = 5)\n")
    print("-d num :\n")
    print("  Sets the maximum distance of the roads to 'num'. 'num' must be a positive integer")
    print("  (greater than 0) (default = 10)\n")
    print("-p num :\n")
    print("  Sets the probability of the operator to delegate a task to the UAV to 'num'. 'num' must be a float")
    print("  in the interval (0,1) (default = 0.1)\n")
    print("-q num :\n")
    print("  Sets the probability of the UAV taking a bad quality picture to 'num'. 'num' must be a float")
    print("  in the interval (0,1) (default = 0.05)\n")
    sys.exit(exitVal)



def main(argv):

    global maxWaypoint
    global maxDistance
    global maxRewards
    global photoLimit
    global probDel
    global probBad

    maxWaypoint = 6
    maxDistance = 3
    maxRewards = 4
    photoLimit = 5
    probDel = 0.1
    probBad = 0.2
    seed = 0
    path = "../synthesis/UAVOperator/"


    try:
        opts, args = getopt.getopt(argv,"hs:w:d:r:l:p:q:",["seed=","waypoints=","max_road_distance=","max_rewards=","photo_limit=","prob_delegate","prob_bad_image"])
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
        elif opt in ("-w","waypoints=") :
            try :
                maxWaypoint = int(arg)
            except ValueError :
                print("The number of waypoints must be a positive integer")
                sys.exit(2)            
            if maxWaypoint <= 0 :
                print("The number of waypoints must be a positive integer")
                sys.exit(2)
        elif opt in ("-d","max_road_distance=") :
            try :
                maxDistance = int(arg)
            except ValueError :
                print("The max road distance must be a positive integer")
                sys.exit(2)            
            if maxDistance <= 0 :
                print("The max road distance must be a positive integer")
                sys.exit(2)
        elif opt in ("-r","max_rewards=") :
            try :
                maxRewards = int(arg)
            except ValueError :
                print("The max reward must be a positive integer")
                sys.exit(2)            
            if maxRewards <= 0 :
                print("The max reward must be a positive integer")
                sys.exit(2)
        elif opt in ("-l","photo_limit=") :
            try :
                photoLimit = int(arg)
            except ValueError :
                print("The photo limit must be a positive integer")
                sys.exit(2)            
            if photoLimit <= 0 :
                print("The photo limit must be a positive integer")
                sys.exit(2)
        elif opt in ("-p","prob_delegate=") :
            try :
                probDel = float(arg)
            except ValueError :
                print("The delegate probability must be a float in (0,1)")
                sys.exit(2)
            if probDel <= 0 or probDel >= 1 :
                print("The delegate probability must be a float in (0,1)")
                sys.exit(2)
        elif opt in ("-q","prob_bad_image=") :
            try :
                probBad = float(arg)
            except ValueError :
                print("The bad image probability must be a float in (0,1)")
                sys.exit(2)
            if probBad <= 0 or probBad >= 1 :
                print("The bad image probability must be a float in (0,1)")
                sys.exit(2)

    genRndBoard(seed)

    writeUAV_1(path+"uav1["+str(seed)+"-"+str(maxWaypoint)+"-"+str(maxDistance)+"-"+str(maxRewards)+"-"+str(photoLimit)+"-"+str(probDel)+"-"+str(probBad)+"].sgg")



main(sys.argv[1:])
