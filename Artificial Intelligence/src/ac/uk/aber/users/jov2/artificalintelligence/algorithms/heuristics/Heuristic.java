package ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;

public abstract class Heuristic {
	
	protected MyBoard board;
	protected int h;
	
	protected Heuristic(MyBoard board) {
		this.board = board;
	}

}
