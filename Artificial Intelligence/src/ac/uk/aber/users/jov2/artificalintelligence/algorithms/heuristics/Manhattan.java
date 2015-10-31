package ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics;

import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

/**
 * Implements the heuristic Manhattan
 * Manhattan distance is the sum of the absolute values of
 * the horizontal and the vertical distance
 * @author Jose Vives
 *
 */
public class Manhattan extends Heuristic{

	@Override
	public int heuristic(Node n) {
		int count = 0;
		int grid[][] = n.getBoard().getGrid();
		int target[][] = n.getBoard().getGoalState();
		for(int row = 0; row < grid.length; row++){
			for(int col = 0; col < grid[0].length; col++){
				if(grid[row][col] != target[row][col]){
					count += moves(grid, target, row, col);
				}
			}
		}
		return count;
	}
	
	private int moves(int[][] grid, int[][] target, int row, int col){
		// find the position of the number we want
		// row 	vertical 	Y
		// col 	horizontal 	X
		int yGoal = 0;
		int xGoal = 0;
		for(int y = 0; y < target.length; y++){
			for(int x = 0; x < target.length; x++){
				if(grid[row][col] == target[y][x]){
					yGoal = y;
					xGoal = x;
				}
			}
		}
		int move = Math.abs(row - yGoal) + Math.abs(col - xGoal);
		return move;
	}

}
