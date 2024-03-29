package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;

import java.util.HashSet;
import java.util.LinkedList;

public class BreadthFirstSearch extends Algorithm{

	public BreadthFirstSearch(MyBoard myBoard, Tile myTile) {
		super(myBoard, myTile);
	}

	@Override
	public MyBoard solve(MyBoard mb) {

		MyBoard board;// new MyBoard();
		LinkedList<MyBoard> frontier = new LinkedList<>();
		frontier.add(mb);

		explored = new HashSet<>();
		myBoard.stepCounter = -1;

		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();

		board = frontier.poll();

		while ((!myBoard.stopAlgorithm) && (!myBoard.isGoal(board)) && (board != null)) {
			if (!alreadyVisited(board)) {
				
				int depth = board.getDepth() + 1;
				this.updateGUI(displaySearch, board);

				// Add it to the explored list.
				addToExplored(board);

				// Attach the expanded succeeding nodes onto the tail of the
				// queue.
				myBoard.expandAll(board, frontier, depth);
			}

			board = frontier.poll();
		}
		assert board != null;
		return finalise(board, displaySearch, board.getDepth());
	}

}
