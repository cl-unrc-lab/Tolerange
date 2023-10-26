This is the source code and binaries distribution of the Tolerange tool. Tolerange accepts as inputs one stochastic program (the spec) with  no faults, and 
another program which can be thought of as an implementation containing faults and fault-tolerance mechanisms. Tolerange measures how much tolerance to faults the implementation provides,
for doing so it uses the spec to evaluate in what extent the implementation deviates from the expected behavior. It uses stochastic bisimulation games for computing such a value. 

Tolerange is distributed under the the GNU Lesser Public License (see the file LICENCE in this distribution). 

Tolerange uses linear programming for solving games and so it uses linear programming libraries, the default library is SSC which is open source. The tool also supports the option of
using gurobi, for that the user must have [Gurobi](https://gurobi.com) installed. In all our tests Gurobi behaved faster than SSC. 

# The distribution contains the following folders:

* bin: it contains the scripts and executables for running the tool.
* src/core: it contains the class that provides the main methods for computing the expected number of faults preserved by the implementation.
* src/lang: it contains the AST used by the paser.
* src/game: it contains classes for the game graph, 
* src/main: the main file of the tool
* src/model: classes that allows on to keep track all the information of a program  
* src/parserL: the grammar for the language in cup and lex syntax
* tests/: it contains several examples used for testing the tools, since the examples may have several parameters, for instance number of bits of a memory, the corresponding scripts for 
	  generating the files with different parameters can be found in these folders.
* jar/: when the tool is compiled the corresponding .jat will be placed here.
* lib/: libs needed by tolerange, it does not include gurobi, if you want to use gurobi you need a license and then you can put the file gurobi.jar here.
* doc/: it contain classes documentations and the grammar for the faulty modelling language.

# Binary Files and Running the Tool
This distribution comes with binaries, thus you can execute the tool without compiling. If you want to compile the tool follows the step in the next section

All the scripts needed for running the tool are placed in the folder bin/ the basic script is "Tolerange", for printing a help you can
proceed as follows:

$ cd bin
$ ./Tolerange -h (then follow the help instructions). 

You also can find other scripts for running the tool with some options or with some specifics sets of tests, for instance:

* ./test: it replicates the tests reported in the paper
* ./test-gurobi: it replicates the tests using gurobi instead of SSC
* ./test-ssc-subset: it runs a subset of the tests
* ./test-gurobi-subset: it runs a subset of the tests using gurobi.

# Examples
We provide 5 examples with several configuration each.

* the memory example, this is our running example in the paper
* nmr-proc-mem, the NMR example also described in the paper,
* nmr, pretty similar example to the memory with no refreshing,
* Hamming(7,4), the hamming code for 3 parity bits,
* Hamming(15,11), the hamming code for 4 parity bits. The tool times out for this example.


Also, scripts for generating tests are found in tests/scripts. To generate all tests use:
./generate-tests in folder bin/

To replicate all experimental results run the script: ./test in folder bin/, to obtain better running times use the script ./test-gurobi, but you need to have installed gurobi in this case.


# Installation:
This distribution comes with binaries, thus you can execute the tool without compiling, if you want to do so just skip this section.

## ant installation
For compiling the tool you will need tha ant tool, If you have ant already installed you can skip this step, otherwise this distribution provides the package you can install ant (in Ubuntu) *ant_1.10.11-1_all.deb* and you can install it 
with the following command:

$ sudo dpkg -i ant_1.10.11_all.deb

You can compile the tool using ant, you need java > 1.8 and the tool ant. It will compile in Linux, Windows and MacOs in the same way. We have tested the tool with java 1.8 in several platforms.

## Compile:
$ ant compile jar

## Clean:
$ ant clean

# Documentation
In folder doc/ you can find the documentation of  all the tool classes in html, starting from [index.html](doc/index.html)). Also, in this folderyou can find a .pdf file with the grammar
of the modeling language. 
