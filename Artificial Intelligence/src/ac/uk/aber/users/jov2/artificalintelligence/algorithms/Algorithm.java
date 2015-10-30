package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.HashSet;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;

public abstract class Algorithm {
	
	protected MyBoard myBoard;
	protected Tile myTile;
	
	// A HashSet is used for the explored list as we only need to check if a
	// state has been seen before
	// and we don't need to retain any more information about it
	protected HashSet<String> explored = null;
	
	protected Algorithm(MyBoard myBoard, Tile myTile) {
		this.myBoard = myBoard;
		this.myTile = myTile;
	}
	
	public abstract MyBoard solve(MyBoard mb);
	

	public MyBoard finalise(MyBoard finalNode, boolean displaySearch){
		return finalise(finalNode, displaySearch, -1);
	}
	
	// Update the GUI, output statistics
	public MyBoard finalise(MyBoard finalNode, boolean displaySearch, int depth) {
		// Paint the solution node.
		if (!myBoard.stopAlgorithm) {
			myBoard.stepCounter++;
			if(depth != -1){
				myTile.getStepCounterLabel().setText("<html>Nodes expanded: <br>" + Integer.toString(myBoard.stepCounter)
				+ "<br>Depth limit: " + depth + "</html>");
			}else{
				myTile.getStepCounterLabel().setText("<html>Nodes expanded: <br>" + Integer.toString(myBoard.stepCounter) + "</html>");
			}

			if (displaySearch) {
				myBoard.copyBoard(myBoard, finalNode);
				myBoard.paintSlow(myBoard.getGraphics());
			}

			myTile.getSoluLabel().setText("<html>Solution Found!<br>" + "<html>");
			myBoard.status = Tile.IDLE;

			// calculate the solution length
			int solutionLength = -1;

			MyBoard boardList = null;
			MyBoard temp;
			MyBoard tempNew;
			temp = finalNode;

			// work back from the final node reached to see the solution path
			// (and calculate its length)
			while (temp != null) {
				solutionLength++;
				tempNew = new MyBoard(myTile);
				myBoard.copyBoard(tempNew, temp);
				tempNew.next = boardList;
				boardList = tempNew;
				temp = temp.parent;
			}

			// Print out some stats
			System.out.println(" ---------------- ");
			System.out.println("Nodes expanded: " + myBoard.stepCounter);
			System.out.println("Solution length: " + solutionLength);
			return finalNode;
		} else {
			return null;
		}
	}
	
	// check if the state has been visited already
	protected boolean alreadyVisited(MyBoard board) {
		return explored.contains(board.hash());
	}
	
	protected void updateGUIWithDetph(boolean displaySearch, MyBoard board, int depth){
		// Display the step counter
		myBoard.stepCounter++;
		myTile.getStepCounterLabel().setText("<html>Nodes expanded: <br>" + Integer.toString(myBoard.stepCounter)
				+ "<br>Depth limit: " + depth + "</html>");

		// Display the inner node
		if (displaySearch) {
			myBoard.copyBoard(myBoard, board);
			myBoard.paintSlow(myBoard.getGraphics());
		}
	}
	
	protected void updateGUI(boolean displaySearch, MyBoard board){
		// Display the step counter
		myBoard.stepCounter++;
		myTile.getStepCounterLabel().setText("<html>Nodes expanded: <br>" + Integer.toString(myBoard.stepCounter) + "</html>");

		// Display the inner node
		if (displaySearch) {
			myBoard.copyBoard(myBoard, board);
			myBoard.paintSlow(myBoard.getGraphics());
		}
	}

	// Add to the explored list. If this state has not been encountered before,
	// add it to the list
	protected void addToExplored(MyBoard board) {
		String hash = board.hash();
		if (!explored.contains(hash))
			explored.add(hash);
	}

}
