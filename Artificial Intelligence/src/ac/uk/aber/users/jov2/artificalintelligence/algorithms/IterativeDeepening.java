package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;

import java.util.HashMap;
import java.util.Stack;

public class IterativeDeepening extends Algorithm {
	
	//A HashMap has to be used for IDS as you also need to keep track of the depth of nodes:
	private HashMap<String, Integer> exploredIDS;

	public IterativeDeepening(MyBoard myBoard, Tile myTile) {
		super(myBoard, myTile);
	}
	
	@Override
	public MyBoard solve(MyBoard mb) {
		MyBoard board = null;
		myBoard.stepCounter = -1;

		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();
	
		for (int depth=0;depth<1000000000;depth++) {
			Stack<MyBoard> frontier = new Stack<>();
			mb.setDepth(0);
			frontier.push(mb);
			exploredIDS = new HashMap<>();
			
			while ((!myBoard.stopAlgorithm)&&(frontier.size()>0)) {	
				board = frontier.pop();
				if (myBoard.isGoal(board)) return finalise(board,displaySearch); 
				
				if (!alreadyVisitedIDS(board)&&board.getDepth() < depth) {
					// Display the step counter
					myBoard.stepCounter++;
					myTile.getStepCounterLabel().setText("<html>Nodes expanded: <br>" + Integer.toString(myBoard.stepCounter) + "<br>Depth limit: "+depth+"</html>");

					// Display the inner node
					if (displaySearch) {
						myBoard.copyBoard(myBoard, board);
						myBoard.paintSlow();
					}

					// Add it to the searched board list.
					addToExploredIDS(board);

					// Attach the expanded succeeding nodes onto the top of the stack.
					myBoard.expandAll(board, frontier, board.getDepth() + 1);
				}
			}
			//if (stopAlgorithm || isGoal(board)) break;
			if (myBoard.stopAlgorithm) break;
		}
		return finalise(board,displaySearch);
	}
	
	private boolean alreadyVisitedIDS(MyBoard board) {
		String hash = board.hash();
		if (exploredIDS.containsKey(hash)) {
			int depth = exploredIDS.get(hash);
			//If the previously encountered node was deeper than the current node 'board' then pretend that we haven't seen it before
			//This is done as the higher up node could lead to a shallower goal node ultimately.
			return depth <= board.getDepth();
		} else{
			//we haven't seen this node before
			return false; 
		}
	}

	//Add to the explored list. If this state has not been encountered before, add it to the list
	private void addToExploredIDS(MyBoard board) {
		// get the unique identifier for this board
		String hash = board.hash();
		// if it doesn't exist already then add it
		if (!exploredIDS.containsKey(hash)){
			exploredIDS.put(hash,board.getDepth()); 
		}else {
			//replace the depth indicator for this existing board to the smallest value seen
			int depth = exploredIDS.get(hash);
			exploredIDS.put(hash, Math.min(depth,board.getDepth()));
		}
	}

}
