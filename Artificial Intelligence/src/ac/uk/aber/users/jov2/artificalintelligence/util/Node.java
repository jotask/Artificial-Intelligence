package ac.uk.aber.users.jov2.artificalintelligence.util;

import java.util.ArrayList;
import java.util.Arrays;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;

/**
 * This class holds information for the A* algorithm
 * I separated from the myBoard class because for me
 * it makes more sense to have a class for the algorithm
 * 
 * This class holds the instance of one board, the heuristic
 * value and the g cost
 * 
 * @author Jose Vives
 *
 */
public class Node implements Comparable<Node>{
	
	/**
	 * The G cost
	 */
	private int g;
	
	/**
	 * The heuristic value
	 */
	private int h;
	
	/**
	 * The instance of the board for this state
	 */
	private MyBoard board;
	
	/**
	 * Constructor for the Node class
	 * Just save the instance of the board
	 * and set the cost of g and h to zero
	 * 
	 * @param board
	 * 			The instance of the board for this node
	 */
	public Node(MyBoard board) {
		this.board = board;
		this.g = 0;
		this.h = 0;
	};
	
	/**
	 * Compare this object with another object
	 * this override the equals methods for make it
	 * to compare the grid array too
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node)) return false;
		if(this.getClass() != obj.getClass())return false;
		Node that = (Node) obj;
		return Arrays.equals(board.getGrid(), that.getBoard().getGrid());
	}
	
	/**
	 * Get the g cost of this node
	 * @return
	 * 		The g cost of this node
	 */
	public int getG(){
		return this.g;
	}
	
	/**
	 * Set the G cost of this node
	 * @param g
	 * 		The new cost for this node
	 */
	public void setG(int g){
		this.g = g;
	}
	
	/**
	 * Return the f value of this node
	 * The f value is the sum of the g cost and 
	 * the heuristic value. So we don´t need to
	 * store this value, just we want the value
	 * calculate it for it
	 * @return
	 * 		The f value for this node
	 */
	public int getF(){
		return this.g + this.h;
	}
	
	/**
	 * Getter for the Heuristic value of this node
	 * @return
	 * 		The H value of this node
	 */
	public int getH(){
		return this.h;
	}
	
	/**
	 * Set the Heuristic value for this node
	 * @param h
	 * 		The new heuristic value for this node
	 */
	public void setH(int h){
		this.h = h;
	}
	
	/**
	 * Set the parent of this node
	 * The parent is stored in the MyBoard class
	 * because is used for the GUI and other methods
	 * so it make more easy just leave the instance 
	 * of the parent there. But for me make more sense
	 * have it in the node class
	 * 
	 * @param parent
	 * 		The parent for this node
	 */
	public void setParent(Node parent){
		board.parent = parent.getBoard();
	}
	
	/**
	 * Get the board instance of this node
	 * @return
	 * 		The board instance
	 */
	public MyBoard getBoard() {
		return this.board;
	}

	/**
	 * Know if the actual node is the solved puzzle
	 * @return
	 * 		true if is the goal state
	 */
	public boolean isGoal() {
		return this.board.isGoal(this.board);
	}
	
	/**
	 * Get the depth of the actual node
	 * @return
	 * 		the depth of this node
	 */
	public int getDepth(){
		return this.getBoard().getDepth();
	}
	
	/**
	 * Set the depth of this node
	 * @param depth	
	 * 		the new depth of this node
	 */
	public void setDepth(int depth){
		this.getBoard().setDepth(depth);
	}
	
	/**
	 * Get all possibles successors for this node
	 * 
	 * A created this method for make it more easy get successors
	 * What it does is call the method from the current board for
	 * get his successors.
	 * Later for each my board I create a node object with the
	 * successors and I set the depth
	 * When the node is created I add the node to the arrayList
	 * of nodes
	 * 
	 * @param depth
	 * 		The depth of the successors
	 * @return
	 * 		An arrayList of Nodes containing all
	 * 		possibles successors
	 */
	public ArrayList<Node> getSuccessors(int depth){
		ArrayList<MyBoard> boards = new ArrayList<MyBoard>();
		this.getBoard().expandAll(this.getBoard(), boards, depth);
		ArrayList<Node> successors = new ArrayList<Node>();
		for(MyBoard b: boards){
			Node node = new Node(b);
			node.setDepth(depth);
			successors.add(node);
		}
		return successors;
	}

	/**
	 * Used for compare the F values is used in the priorityqueue
	 */
	@Override
	public int compareTo(Node o) {
		return this.getF() - o.getF();
	}

}
