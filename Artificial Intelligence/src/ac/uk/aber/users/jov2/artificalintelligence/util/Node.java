package ac.uk.aber.users.jov2.artificalintelligence.util;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;

public class Node implements Comparable<Node> {

	private MyBoard board;
	private Node parent;
	private int g = Integer.MAX_VALUE;
	private int h; // number of missplaced tiles

	public Node(MyBoard board) {
		this.board = board;
		calculateH();
	}

	public void setH(int h) {
		this.h = h;
	}

	public void setG(int g) {
		this.g = g;
	}

	public void setParent(Node parent) {
		this.parent = parent;
		this.board.parent = parent.getBoard();
	}

	public int getH() {
		return this.h;
	}

	public int getG() {
		return this.g;
	}

	public int getF() {
		return this.g + this.h;
	}

	public Node getParent() {
		return this.parent;
	}

	public MyBoard getBoard() {
		return this.board;
	}

	// CS26110 Assignment
	// This doesn't work at the moment - you'll need to make sure that the
	// variable 'f' is calculated correctly elsewhere
	// Once 'f' is calculated correctly, this will order the MyBoard states in a
	// priority queue correctly for A* search.
	@Override
	public int compareTo(Node other) {
		return this.getF() - other.getF();
	}

	private void calculateH() {
		int result = 0;
		int[][] grid = board.getGrid();
		int[][] goal = board.getGoalState();
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[0].length; x++) {
				if (grid[y][x] != goal[y][x]) {
					result++;
				}
			}
		}
		this.h = result;
	}

}
