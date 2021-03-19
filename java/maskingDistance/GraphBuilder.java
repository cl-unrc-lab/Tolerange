package maskingDistance;
import lang.*;
import model.*;

public class GraphBuilder implements Runnable {

	boolean isSpec;
	Program progProgram;
	Model prog;


	public GraphBuilder(Program p, boolean isS) {
		isSpec = isS;
		progProgram = p;
	}

	public void run() {
		prog = progProgram.toGraph(isSpec);
		prog.saturate();
	}

	public Model getProg(){
		return prog;
	}
}