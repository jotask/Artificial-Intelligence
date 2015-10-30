package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.HashSet;
import java.util.Stack;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;

public class DepthFirstSearch extends Algorithm{

	public DepthFirstSearch(MyBoard myBoard, Tile myTile) {
		super(myBoard, myTile);
	}

	@Override
	public MyBoard solve(MyBoard mb) {
		MyBoard board;
		Stack<MyBoard> frontier = new Stack<MyBoard>();
		frontier.add(mb);

		explored = new HashSet<String>();
		myBoard.stepCounter = -1;

		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();

		board = frontier.pop();

		while ((!myBoard.stopAlgorithm) && (!myBoard.isGoal(board)) && (board != null)) {
			if (!alreadyVisited(board)) {
				
				this.updateGUI(displaySearch, board);

				// Add it to the searched board list.
				addToExplored(board);

				// Attach the expanded succeeding nodes onto the top of the
				// stack.
				int depth = board.getDepth();
				myBoard.expandAll(board, frontier, depth++);
			}

			board = frontier.pop();
		}
		return finalise(board, displaySearch, board.getDepth());
	}

}
