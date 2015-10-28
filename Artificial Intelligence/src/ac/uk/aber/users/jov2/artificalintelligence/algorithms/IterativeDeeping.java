package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.HashMap;
import java.util.Stack;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;

public class IterativeDeeping extends Algorithm {

	public IterativeDeeping(MyBoard myBoard, Tile myTile) {
		super(myBoard, myTile);
	}

	@Override
	// Iterative deepening search - an incremental depth limit until a solution
	// is reached
	public MyBoard solve(MyBoard mb) {
		MyBoard board = null;
		myBoard.stepCounter = -1;

		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();

		for (int depth = 0; depth < 1000000000; depth++) {
			Stack<MyBoard> frontier = new Stack<MyBoard>();
			mb.depth = 0;
			frontier.push(mb);
			exploredIDS = new HashMap<String, Integer>();

			while ((!myBoard.stopAlgorithm) && (frontier.size() > 0)) {
				board = frontier.pop();
				if (myBoard.isGoal(board))
					return finalise(board, displaySearch);

				if (!alreadyVisitedIDS(board) && board.depth < depth) {
					// Display the step counter
					myBoard.stepCounter++;
					myTile.getStepCounterLabel().setText("<html>Nodes expanded: <br>" + Integer.toString(myBoard.stepCounter)
							+ "<br>Depth limit: " + depth + "</html>");

					// Display the inner node
					if (displaySearch) {
						myBoard.copyBoard(myBoard, board);
						myBoard.paintSlow(myBoard.getGraphics());
					}

					// Add it to the searched board list.
					addToExploredIDS(board);

					// Attach the expanded succeeding nodes onto the top of the
					// stack.
					myBoard.expandAll(board, frontier, board.depth + 1);
				}

			}
			// if (stopAlgorithm || isGoal(board)) break;
			if (myBoard.stopAlgorithm)
				break;
		}

		return finalise(board, displaySearch);
	}
	

	// A HashMap has to be used for IDS as you also need to keep track of the
	// depth of nodes:
	// http://stackoverflow.com/questions/12598932/how-to-store-visited-states-in-iterative-deepening-depth-limited-search
	HashMap<String, Integer> exploredIDS;

	private boolean alreadyVisitedIDS(MyBoard board) {
		String hash = board.hash();
		if (exploredIDS.containsKey(hash)) {
			int depth = exploredIDS.get(hash);
			// If the previously encountered node was deeper than the current
			// node 'board' then pretend that we haven't seen it before
			// This is done as the higher up node could lead to a shallower goal
			// node ultimately.
			if (depth > board.depth) {
				return false; // pretend we haven't seen this before (the
								// current board is higher up the tree)
			} else
				return true; // say that we have seen this before (the current
								// node is at least at the depth of the
								// previously stored node)
		} else
			return false; // we haven't seen this node before
	}
	
	// Add to the explored list. If this state has not been encountered before,
	// add it to the list
	private void addToExploredIDS(MyBoard board) {
		String hash = board.hash(); // get the unique identifier for this board
		if (!exploredIDS.containsKey(hash))
			exploredIDS.put(hash, board.depth); // if it doesn't exist already
												// then add it
		else { // replace the depth indicator for this existing board to the
				// smallest value seen
			int depth = exploredIDS.get(hash);
			exploredIDS.put(hash, Math.min(depth, board.depth));
		}
	}

}
