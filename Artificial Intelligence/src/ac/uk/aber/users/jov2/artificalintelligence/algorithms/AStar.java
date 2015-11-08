package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics.Heuristic;
import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * This class holds the A* algorithm and all he needs
 * for solve the problem
 * 
 * @author Jose Vives
 *
 */
public class AStar extends Algorithm {

	/**
	 * The heuristic we want use for this instance
	 * of the A*
	 */
	private final Heuristic heuristic;

	/**
	 * Constructor for the A*
	 * Just save the instance of all the thins we need
	 * @param myBoard
	 * 		The board
	 * @param myTile
	 * 		The GUI
	 * @param heuristic
	 * 		The heuristic we want use for this A* instance
	 */
	public AStar(MyBoard myBoard, Tile myTile, Heuristic heuristic) {
		super(myBoard, myTile);
		this.heuristic = heuristic;
	}

	@Override
	public MyBoard solve(MyBoard mb) {
		
		// Create the goal node
		Node goal = new Node(mb.getGoalBoard());
		
		// Create the initial node and set the depth and the g
		// to zero. And calculate the heuristic from the initial
		// state from the goal state
		Node start = new Node(mb);
		start.setDepth(0);
		start.setG(0);
		start.setH(this.heuristic.calculate(start, goal));
		
		// Create a priority queue for the nodes for check OPEN LIST
		PriorityQueue<Node> open = new PriorityQueue<>();
		
		// Initialise the hasSet for the nodes already checked CLOSED LIST
		explored = new HashSet<>();
		
		// Add the start node the Queue
		open.offer(start);
		
		// Just GUI changes
		myBoard.stepCounter = -1;
		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();
		
		// Iterate the open queue until is empty
		while((!myBoard.stopAlgorithm) && (!open.isEmpty())){
			// Poll the node from the queue with the lowest F value
			Node current = open.poll();
			// Add the current node to the checked list
			addToExplored(current.getBoard());
			
			// Check if the node is the goal state, if is the goal
			// state we done, solution founded, and recreate the path
			if(current.isGoal()){
				return finalise(current.getBoard(), false);
			}
			
			// Know the path of this cycle
			int depth = current.getDepth() + 1;
			// Update the GUI information
			this.updateGUI(displaySearch, current.getBoard());
			
			// Get all possibles successors nodes for the actual state
			ArrayList<Node> successors = current.getSuccessors(depth);
			
			// Iterate each node
			for(Node n: successors){
				
				// If this node has been checked continue with the next successor
				if(alreadyVisited(n.getBoard())){
					continue;
				}
				
				// Set the parent of this successors to the current Node
				n.setParent(current);
				
				// Calculate the G cost
				// The G cost is the parent g cost + 1
				// So the cost for be in one state and go to the next state is 1
				n.setG(current.getG() + 1);
				
				// Calculate the heuristic value from the successors to the goal state
				n.setH(this.heuristic.calculate(n, goal));
				
				// Add this node to the open list
				open.offer(n);
				
			}
		}
		
		// If we finish loop means we didn't find a solution for this puzzle
		// Just let know to the user we didn't find a solution
		// And throw a runtime exception
		myTile.getSoluLabel().setText("This Board doesn't have any solution");
		throw new RuntimeException("This Board doesn't have any solution");
	}

}
