package ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics;

import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

/**
 * Implements the heuristic Manhattan distance is the sum of the
 * absolute values of the horizontal and the vertical distance.
 * 
 * Calculate the distance of two nodes iterating for each value
 * and know how far is from the target goal
 * 
 * @author Jose Vives
 *
 */
public class Manhattan extends Heuristic {

	@Override
	public int calculate(Node current, Node goal) {
		int count = 0;
		int grid[][] = current.getBoard().getGrid();
		int target[][] = goal.getBoard().getGrid();
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				int h = moves(grid, target, row, col);
				count += h;
			}
		}
		return count;
	}

	/**
	 * Know how far is one tile to the target tile
	 */
	private int moves(int[][] grid, int[][] target, int row, int col) {
		// find the position of the number we want
		// row vertical Y
		// col horizontal X
		int yGoal = 0;
		int xGoal = 0;
		loop: for (int y = 0; y < target.length; y++) {
			for (int x = 0; x < target.length; x++) {
				if (grid[row][col] == target[y][x]) {
					yGoal = y;
					xGoal = x;
					break loop;
				}
			}
		}
		return Math.abs(row - yGoal) + Math.abs(col - xGoal);
	}

}
