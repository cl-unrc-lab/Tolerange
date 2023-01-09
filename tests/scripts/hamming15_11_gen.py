#!/usr/bin/env python3

import sys, getopt


def mv_mul(m,v) :
    return int(''.join([str(bin(n).count("1") % 2) for n in [(row & v) for row in m]]), 2)


def produce_nominal(outfile) :

    outfile.write("Process Nominal_model {\n\n")
    outfile.write("  state : INT ;\n\n")
    outfile.write("  Initial: state == 2048 ;\n\n")

    outfile.write("  // Send message \n\n")
    for i in range(2048) :
        outfile.write("  [snd_"+str(i)+"] (state == 2048) -> state = "+str(i)+" ;\n")

    outfile.write("\n  // Receive message \n\n")
    for i in range(2048) :
        outfile.write("  [rcv_"+str(i)+"] <1> (state == "+str(i)+") -> state = 2048 ;\n")

    outfile.write("\n} \n\n")
    outfile.write("  Main() { \n")
    outfile.write("    h : Nominal_model ;\n")
    outfile.write("    run h() ; \n")
    outfile.write("  } \n")



def produce_implementation(outfile,prob) :

    outfile.write("Process Faulty_model {\n\n")
    outfile.write("  fstate : INT ;\n\n")
    outfile.write("  Initial : fstate == 4096 ;\n\n")

    outfile.write("  // Send message \n\n")
    for i in range(2048) :
        outfile.write("  [snd_"+str(i)+"] (fstate == 4096) -> fstate = "+str(i)+" ;\n")
    matrixG = [0x6d5,0x5b3,0x400,0x38f,0x200,0x100,0x80,0x7f,0x40,0x20,0x10,0x8,0x4,0x2,0x1]        
    matrixH = [0x5555,0x3333,0xf0f,0xff]                     
    matrixR = [0x1000,0x400,0x200,0x100,0x40,0x20,0x10,0x08,0x04,0x02,0x01]
    matrixE = [0x00,0x4000,0x2000,0x1000,0x800,0x400,0x200,0x100,0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01]

    
    outfile.write("\n  // Fault event \n\n")
    for msgSent in range(2048) :

        # encode
        transmit = mv_mul(matrixG,msgSent)

        # calculate transition probability for each possible change

        distr = [0] * 2048

        for mask in range(32768) :

            altered = transmit ^ mask

            # parity check
            parity = mv_mul(matrixH,altered)

            # decode
            msgRcvd = mv_mul(matrixR,(altered ^ matrixE[parity]))

            # increase probabilities for this value
            distr[msgRcvd] = distr[msgRcvd] + (prob ** bin(mask).count("1")) * ((1-prob) ** (15-bin(mask).count("1")))

        # output fault transition
        check = distr[2047]
        outfile.write("  [fault] faulty (fstate == "+str(msgSent)+") -> ")
        for i in range(2047) :
            outfile.write(format(distr[i], '.8f')+" : fstate = "+str(i+2048)+" ++ ")
            check = check + distr[i]

        print(str(check))
        outfile.write(format(distr[2047], '.8f')+" : fstate = "+str(2047+2048)+" ;\n")

    outfile.write("\n  // Receive message \n\n")
    for i in range(2048) :
        outfile.write("  [rcv_"+str(i)+"] <1> (fstate == "+str(i+2048)+") -> fstate = 4096 ;\n")

    for i in range(2048) :
        outfile.write("  [rcv_"+str(i)+"] <1> (fstate == "+str(i)+") -> fstate = 4096 ;\n")

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
