package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.HashSet;
import java.util.LinkedList;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;

public class BreadthFirstSearch extends Algorithm{

	public BreadthFirstSearch(MyBoard myBoard, Tile myTile) {
		super(myBoard, myTile);
	}

	@Override
	public MyBoard solve(MyBoard mb) {

		MyBoard board = null;// new MyBoard();
		LinkedList<MyBoard> frontier = new LinkedList<MyBoard>();
		frontier.add(mb);

		explored = new HashSet<String>();
		myBoard.stepCounter = -1;

		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();

		board = frontier.poll();

		while ((!myBoard.stopAlgorithm) && (!myBoard.isGoal(board)) && (board != null)) {
			if (!alreadyVisited(board)) {
				// Display the step counter
				myBoard.stepCounter++;
				myTile.getStepCounterLabel().setText("<html>Nodes expanded: <br>" + Integer.toString(myBoard.stepCounter) + "</html>");

				// Display the inner node
				if (displaySearch) {
					myBoard.copyBoard(myBoard, board);
					myBoard.paintSlow(myBoard.getGraphics());
				}

				// Add it to the explored list.
				addToExplored(board);

				// Attach the expanded succeeding nodes onto the tail of the
				// queue.
				myBoard.expandAll(board, frontier, board.depth + 1);
			}

			board = frontier.poll();
		}

		return finalise(board, displaySearch);
	}

}
