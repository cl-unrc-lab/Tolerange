# Intro

This is the source code and binaries distribution of the Tolerange tool. Tolerange accepts as inputs one stochastic model (aka the specification) containing  no faults, together with 
another stochastic model,  which is an augmented version of the former (aka the implementation)  containing faults and fault-tolerance mechanisms. Tolerange measures how much tolerance to faults the implementation provides, for doing so it uses the spec to evaluate to what extent the implementation deviates from the expected behavior. It uses stochastic bisimulation games for computing such a value. 

Tolerange is distributed under the the GNU Lesser Public License (see the file LICENCE in this distribution). 

Tolerange uses linear programming for solving games and so it uses linear programming libraries, the default library is SSC which is open source. Optionally, the tool supports the use of
using Gurobi, for that the user must have [Gurobi](https://gurobi.com) installed (see [Installing Gurobi](#installing-gurobi)). In all our tests Gurobi behaved faster than SSC. 

# Folders:

the distribution contains de following folders:

* bin/: it contains the scripts and executables for running the tool.
* src/core: it contains the class that provides the main methods for computing the expected number of faults preserved by the implementation.
* src/lang: it contains the AST used by the paser.
* src/game: it contains classes for the game graph, 
* src/main: the main file of the tool
* src/model: classes that allows on to keep track all the information of a program  
* src/parserL: the grammar for the language in cup and lex syntax
* tests/: it contains several examples used for testing the tools, since the examples may have several parameters, for instance number of bits of a memory, the corresponding scripts for 
	  generating the files with different parameters can be found in these folders.
* jar/: when the tool is compiled the corresponding .jar will be placed here. The tool is distribuited with a precompiled .jar.
* lib/: libs needed by tolerange, it does not include gurobi, if you want to use gurobi you need a license and then you can put the file gurobi.jar here.
* doc/: it contain classes documentations and the grammar for the faulty modelling language.

# Binary Files and Running the Tool
This distribution comes with binaries, thus you can execute the tool without compiling. If you want to compile the tool just follow the steps in the [Installation Section](#installation).

All the scripts needed for running the tool are placed in the folder bin/ the basic script is "Tolerange", for printing the help you can
proceed as follows:

$ cd bin
$ ./Tolerange -h (then follow the help instructions). 

You also can find other scripts for running the tool with additional options or with some specific sets of tests, for instance:

* ./test-ssc-memory: it runs the tool over the memory example with ssc
* ./test-memory: it runs the tool over the memory example with Gurobi 
* ./test: it replicates all the tests 
* ./test-gurobi: it replicates the tests using gurobi instead of SSC
* ./test-ssc-subset: it runs a subset of the tests
* ./test-gurobi-subset: it runs a subset of the tests using gurobi.

# Examples
We provide 5 main examples with several configurations each.

* the memory example, this is our running example in the paper
* nmr-proc-mem, the NMR example also described in the paper,
* nmr, pretty similar example to the memory with no refreshing,
* Hamming(7,4), the hamming code for 3 parity bits,
* Hamming(15,11), the hamming code for 4 parity bits. The tool times out for this example.


Also, scripts for generating tests are found in tests/scripts. To generate all tests use:
./generate-tests in folder bin/

To replicate all experimental results run the script: ./test in folder bin/, to obtain better running times use the script ./test-gurobi, but you need to have installed gurobi in this case.


# Installation:
This distribution comes with binaries, thus you can execute the tool without compiling, in that case you can skip this section.

## Ant installation
For compiling the tool you will need the ant tool. If you have ant already installed you can skip this step, otherwise this distribution provides the package *ant_1.10.11-1_all.deb* and you can install ant (in Ubuntu) 
with the following command:

$ sudo dpkg -i ant_1.10.11_all.deb

Once you installed Ant, you can compile the tool, you need java > 1.8. It will compile in Linux, Windows and MacOs in the same way. We have tested the tool with java 1.8 in several platforms.

## Compile:
$ ant compile jar

This compiles the source code and generates the file *jar/Tolerange.jar*

## Clean:
$ ant clean

This cleans all the generated  binaries.

# Installing Gurobi

Gurobi is a commercial linear solver thus you need a licence to be able to use the library. Free academic licence are provided by [Gurobi](www.gurobi.com).
We have tested Tolerange with Gurobi v10.0.3, for running the tool with Gurobi please download this version for your architecture at [Gurobi Download](https://www.gurobi.com/downloads/), you have to be logged into your account for accessing the page.

The steps for installing Gurobi in Ubuntu are as follows:

1. Request a Gurobi license, they provide free academic licenses, the license is a file “gurobi.lic”
2. Download from the Gurobi site the file “gurobi10.0.3_linux64.tar.gz” (you will find similar files for other operating systems), and unzip the file in your home  folder with “tar xzvf gurobi10.0.3_linux.tar.gz”, this will create a folder gurobi1003 in you home directory.
3. Place the file “gurobi.lic” in your home folder.
4. Set the environment variables GUROBI_HOME, PATH, and LD_LIBRARY_PATH, for instance, in Ubuntu you have to exacute ths following commands in a terminal:
```console
$ export GUROBI_HOME="\<home-dir\>/gurobi1003/linux64"
$ export PATH="${PATH}:${GUROBI_HOME}/bin"
$ export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"
```
if you use bash you can add the line above into .bashrc, and the variable will be set for the next sessions. 
5. Test if gurobi is working by executing $ gurobi.sh. 
4. Copy the file “\<your home folder\>/gurobi1003/linux64/lib/gurobi.jar” to the “\<tolerange-dir\>/lib '' where \<tolerange-dir\> is the folder where Tolerange is installed.
5. You need internet connection (Gurobi requests this for validating the license)


After that Gurobi should be working.  More details for toher architectures can be found at [Gurobi environment variables](https://support.gurobi.com/hc/en-us/articles/13443862111761-How-do-I-set-system-environment-variables-for-Gurobi-)

If the Gurobi's license is not correctly installed, the tool may show an error when building the games (if the tool is executed with option -gurobi).

# Documentation
In folder doc/ you can find the documentation of  all the tool classes in html, starting from *index.html*. Also, in this folder you can find a .pdf file with the grammar
of the modeling language. 
