package ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics;

import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

public class TwoHeuristics extends Heuristic {

	private final Heuristic manhattan;
	private final Heuristic hamming;
	
	public TwoHeuristics() {
		manhattan = new Manhattan();
		hamming = new Hamming();
	}

	@Override
	public int calculate(Node a, Node b) {
		int h = 0;
		h += manhattan.calculate(a, b);
		h += hamming.calculate(a, b);
		return h;
	}

}
