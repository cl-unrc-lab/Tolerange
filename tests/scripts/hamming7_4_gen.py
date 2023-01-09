#!/usr/bin/env python3

import sys, getopt


def mv_mul(m,v) :
    return int(''.join([str(bin(n).count("1") % 2) for n in [(row & v) for row in m]]), 2)


def produce_nominal(outfile) :

    outfile.write("Process Nominal_model {\n\n")
    outfile.write("  state : INT ;\n\n")
    outfile.write("  Initial: state == 16 ;\n\n")

    outfile.write("  // Send message \n\n")
    for i in range(16) :
        outfile.write("  [snd_"+str(i)+"] (state == 16) -> state = "+str(i)+" ;\n")

    outfile.write("\n  // Receive message \n\n")
    for i in range(16) :
        outfile.write("  [rcv_"+str(i)+"] <1> (state == "+str(i)+") -> state = 16 ;\n")

    outfile.write("\n} \n\n")
    outfile.write("  Main() { \n")
    outfile.write("    h : Nominal_model ;\n")
    outfile.write("    run h() ; \n")
    outfile.write("  } \n")



def produce_implementation(outfile,prob) :

    outfile.write("Process Faulty_model {\n\n")
    outfile.write("  fstate : INT ;\n\n")
    outfile.write("  Initial : fstate == 32 ;\n\n")

    outfile.write("  // Send message \n\n")
    for i in range(16) :
        outfile.write("  [snd_"+str(i)+"] (fstate == 32) -> fstate = "+str(i)+" ;\n")

    matrixG = [0xd,0xb,0x8,0x7,0x4,0x2,0x1]
    matrixH = [0x55,0x33,0x0f]
    matrixR = [0x10,0x04,0x02,0x01]
    matrixE = [0x00,0x40,0x20,0x10,0x08,0x04,0x02,0x01]

    
    outfile.write("\n  // Fault event \n\n")
    for msgSent in range(16) :

        # encode
        transmit = mv_mul(matrixG,msgSent)

        # calculate transition probability for each possible change

        distr = [0] * 16

        for mask in range(128) :

            altered = transmit ^ mask

            # parity check
            parity = mv_mul(matrixH,altered)

            # decode
            msgRcvd = mv_mul(matrixR,(altered ^ matrixE[parity]))

            # increase probabilities for this value
            distr[msgRcvd] = distr[msgRcvd] + (prob ** bin(mask).count("1")) * ((1-prob) ** (7-bin(mask).count("1")))

        # output fault transition
        check = distr[15]
        outfile.write("  [fault] faulty (fstate == "+str(msgSent)+") -> ")
        for i in range(15) :
            outfile.write(format(distr[i], '.8f')+" : fstate = "+str(i+16)+" ++ ")
            check = check + distr[i]

        print(str(check))
        outfile.write(format(distr[15], '.8f')+" : fstate = "+str(15+16)+" ;\n")

    outfile.write("\n  // Receive message \n\n")
    for i in range(16) :
        outfile.write("  [rcv_"+str(i)+"] <1> (fstate == "+str(i+16)+") -> fstate = 32 ;\n")

    for i in range(16) :
        outfile.write("  [rcv_"+str(i)+"] <1> (fstate == "+str(i)+") -> fstate = 32 ;\n")

    outfile.write("\n} \n\n")
    outfile.write("  Main() { \n")
    outfile.write("    h : Faulty_model ;\n")
    outfile.write("    run h() ; \n")
    outfile.write("  } \n")


def main(argv) :

    filename = ""
    prob = 10**(-1)

    try :
        opts, args = getopt.getopt(argv,"p:",["probability"])
    except getopt.GetoptError :
        usage(2)
    for opt, arg in opts :
        if opt in ("-p","--probability") :
            try :
                prob = float(arg)
            except ValueError :
                print("The probability value must be a float in [0,1)")
                sys.exit(2)
            if (prob < 0) or (prob >= 1) :
                print("The probability value must be a float in [0,1)")
                sys.exit(2)

    filename = "hamming_nm.pts"
    filename2 = "hamming_"+str(prob)+"_ft.pts"

    try :
        outfile = open(filename,"w")
        outfile2 = open(filename2,"w")
    except IOError :
        print("The file '"+filename+"' could not be opened")
        sys.exit(2)


    produce_nominal(outfile)
    
    produce_implementation(outfile2,prob)

    outfile.close()
    outfile2.close()



# =============================================================================

main(sys.argv[1:])
