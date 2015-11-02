package ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics;

import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

/**
 * This class calculate how many tiles are not
 * in his correct position
 *  
 * @author Jose Vives
 *
 */
public class TileHeuristic extends Heuristic {

	/**
	 * Know how many tiles are not in place
	 */
	@Override
	public int calculate(Node current, Node goal) {
		int count = 0;
		int grid[][] = current.getBoard().getGrid();
		int target[][] = goal.getBoard().getGrid();
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if(grid[row][col] != target[row][col]){
					count++;
				}
			}
		}
		return count;
	}

}
