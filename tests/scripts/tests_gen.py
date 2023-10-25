import memory_nominal_gen as mng
import memory_faulty_gen as mfg
import nmr_nominal_gen as nng
import nmr_faulty_gen as nfg
import procmem_nominal_gen as png
import procmem_faulty_gen as pfg

def gen_all_memory_tests():
	path = "../tests/stochastic/nominal/memory/"
	for probR in range(1,5,2):
		r = 0.1*probR
		mng.writeMemoryTickMilestone(path+"memoryTickMilestone_"+str(r)+".mdp")
		mng.writeMemoryRefreshMilestone(path+"memoryRefreshMilestone_"+str(r)+".mdp")
	path = "../tests/stochastic/faulty/memory/"
	for bits in range(3,11,2):
		for probF in range(1,5,2):
			for probR in range(1,5,2):
				r = 0.1*probR
				f = 0.1*probF   			
				mfg.writeMemoryTickMilestone(path+"memoryTickMilestone_"+str(bits)+"-"+str(r)+"-"+str(f)+".mdp")
				mfg.writeMemoryRefreshMilestone(path+"memoryRefreshMilestone_"+str(bits)+"-"+str(r)+"-"+str(f)+".mdp")

def gen_all_nmr_tests():
	path = "../tests/stochastic/nominal/memory/"
	nng.writeNMR(path+"nmr[].mdp")
	path = "../tests/stochastic/faulty/memory/"
	for modules in range(3,11,2):
		for probF in range(1,5,2):
			f = 0.1*probF
			nfg.writeNMR(path+"nmr_"+str(modules)+"-"+str(f)+".mdp")


def gen_all_procmem_tests():
	path = "../tests/stochastic/nominal/nmr-proc-mem/"
	png.writeNMR(path+"nmr-proc-mem.mdp")
	path = "../tests/stochastic/faulty/nmr-proc-mem/"
	for modules in range(3,11,2):
		for probP in range(1,5,2):
			for probV in range(1,5,2):
				p = 0.1*probP
				v = 0.1*probV   			
				pfg.writeNMR(path+"nmr-proc-mem_"+str(modules)+"-"+str(p)+"-"+str(v)+".mdp")

def main(argv):

    gen_all_memory_tests()
    gen_all_nmr_tests()
    gen_all_procmem_tests()
    test_all()



main(sys.argv[1:])
