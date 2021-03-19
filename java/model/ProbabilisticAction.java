package model;

import java.util.*;

import org.jgrapht.graph.DefaultEdge;

import lang.*;


public class ProbabilisticAction extends Action{

	private List<Double> probabilities;

	public ProbabilisticAction(String l, Boolean isF, Boolean isT, Boolean ifs, List<Double> probs){
		super(l,isF,isT,ifs);
		probabilities = probs;
	}

	public ProbabilisticAction(String l, Boolean isF, Boolean isT, Boolean ifs, Boolean isM, List<Double> probs){
		super(l,isF,isT,ifs,isM);
		probabilities = probs;
	}

	public ProbabilisticAction(String l, Boolean isF, Boolean isT, Boolean ifs, Boolean isM, int r, List<Double> probs){
		super(l,isF,isT,ifs,isM,r);
		probabilities = probs;
	}


	public List<Double> getProbabilities(){
		return probabilities;
	}

	public void setProbabilities(List<Double> l){
		probabilities = l;
	}

	@Override
	public boolean equals(Object o){
		//if (o instanceof Action){
		//	return super.equals(o);
		//}
		if (o instanceof ProbabilisticAction){
			ProbabilisticAction a = (ProbabilisticAction)o;
			if (this.getLabel().equals(a.getLabel()) && this.isFaulty().equals(a.isFaulty()) && this.isTau().equals(a.isTau()) && this.isFromSpec().equals(a.isFromSpec())){
				for (int i=0;i<probabilities.size();i++){
					if (!probabilities.get(i).equals(a.getProbabilities().get(i)))
						return false;
				}
				return true;
			}
		}
		return false;
	}

}