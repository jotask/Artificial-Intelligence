package ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics;

import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

/**
 * Class for hold the abstract heuristic
 * All the different heuristic types needs
 * extend from this class for calculate the
 * heuristic
 * 
 * @author Jota
 *
 */
public abstract class Heuristic{
	
	/**
	 * An abstract method for calculate the heuristic
	 * from an given Node
	 * 
	 * @param n 
	 * 			The Node for calculate his heuristic
	 * @return
	 * 			The heuristic value for the node
	 */
	public abstract int heuristic(Node n);

}
