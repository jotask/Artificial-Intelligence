package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;

/**
 * All the algorithm needs to extend to this class
 * This class have generic methods and instance
 * for the all the possibles algorithm can be use
 * 
 * @author Jose Vives
 *
 */
public abstract class Algorithm {
	
	private long startTime;
	private long finishTime;
	
	/**
	 * The instance of the generic board
	 */
	protected MyBoard myBoard;
	
	/**
	 * The instance of the GUI
	 */
	protected Tile myTile;
	
	// A HashSet is used for the explored list as we only need to check if a
	// state has been seen before
	// and we don't need to retain any more information about it
	protected HashSet<String> explored = null;
	
	/**
	 * Constructor for this abstract class.
	 * All methods need call this constructor
	 * @param myBoard
	 * 			The initial board
	 * @param myTile
	 * 			The GUI instance
	 */
	protected Algorithm(MyBoard myBoard, Tile myTile) {
		this.myBoard = myBoard;
		this.myTile = myTile;
	}
	
	/**
	 * An abstract methods, because all the algorithm extending
	 * from this class needs have his own algorithm
	 * @param mb
	 * 		The initial board to solve
	 * @return
	 * 		return the board with all this childs for the
	 * 		solution
	 */
	public abstract MyBoard solve(MyBoard mb);
		
	/**
	 * Construct the path for the solution
	 * 	This method call the generic methods with a negative depth
	 * @param finalNode
	 * 		The final board who have the solution for iterate
	 * 		it until the initial state
	 * @param displaySearch
	 * 		boolean for know if we want display the search
	 * @return
	 * 		The initial board with the path build it
	 */
	public MyBoard finalise(MyBoard finalNode, boolean displaySearch){
		return finalise(finalNode, displaySearch, -1);
	}
	
	/**
	 * Construct the path for the solution
	 * Update the GUI, output statistics
	 * @param finalNode
	 * 		The final board who have the solution for iterate
	 * 		it until the initial state
	 * @param displaySearch
	 * 		boolean for know if we want display the search
	 * @return
	 * 		The initial board with the path build it
	 */	
	public MyBoard finalise(MyBoard finalNode, boolean displaySearch, int depth) {
		// Store the actual nanotime
		finishTime = System.nanoTime();
		
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

			myBoard.setStatus(Tile.IDLE);

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
			StringBuilder text = new StringBuilder();
			text.append("<html>");
			text.append("<p>Nodes expanded: " + myBoard.stepCounter + "</p>");
			text.append("<p>Solution lengh: " + solutionLength + "</p>");
			
			String time = getHTMLTime();
			if(!time.equals(""))
				text.append("<p>" + time + "</p>");
			
			text.append("</html>");
			
			myTile.getSoluLabel().setText(text.toString());
			return finalNode;
		} else {
			return null;
		}
	}
	
	/**
	 * This methods do the same algorithm but just knowing how many
	 * time the algorithm needs to be completed. With this method
	 * we know how many time has been elapsed for solve the puzzle
	 * @param mb
	 * 		The board we want solve
	 * @return
	 * 		The board with the path solved
	 */
	public MyBoard solveWithTime(MyBoard mb){
		startTime = System.nanoTime();
		MyBoard board = solve(mb);
		return board;
	}
	
	/**
	 * Check if the state has been visited already
	 * @param board
	 * 		The state if have been visited
	 * @return
	 * 		true if is already visited
	 */
	protected boolean alreadyVisited(MyBoard board) {
		return explored.contains(board.hash());
	}
	
	/**
	 * Update the GUI information
	 * @param displaySearch
	 * 		if we want show the actual display
	 * @param board
	 * 		The board with the information
	 */
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
	
	/**
	 * Generate the HTML for knowing the time elapsed
	 * 
	 * @return
	 * 		The HTML tag with the time calculated and converted
	 * 		to milliseconds
	 */
	private String getHTMLTime(){
		long time = getTime();
		StringBuilder text = new StringBuilder();
		
		// FIXME doesn't calculate the right elapsed time
		if(time != 0){
			text.append("Solved in ");
			System.out.println(time);
			text.append(TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS) + " ms.");
		}else{
			text.append("");
		}
		
		return text.toString();
	}
	
	/**
	 * Calculate the time elapsed between two nanotime
	 * @return
	 * 		The time elapsed between the start and the
	 * 		finish time
	 */
	private long getTime(){
		long end = finishTime - startTime;
		System.out.println("Total time is: " + end);
		return end;
	}

	/**
	 * Add to the explored list. If this state has not been encountered before,
	 * add it to the list
	 * @param board
	 * 		The board to add to the explored list
	 */
	protected void addToExplored(MyBoard board) {
		String hash = board.hash();
		if (!explored.contains(hash))
			explored.add(hash);
	}

}
