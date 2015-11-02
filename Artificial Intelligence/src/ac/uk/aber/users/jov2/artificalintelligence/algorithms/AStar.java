package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics.Heuristic;
import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

public class AStar extends Algorithm {

	boolean testB = true;

	private final Heuristic heuristic;

	public AStar(MyBoard myBoard, Tile myTile, Heuristic heuristic) {
		super(myBoard, myTile);
		this.heuristic = heuristic;
	}

	@Override
	public MyBoard solve(MyBoard mb) {
		
		Node goal = new Node(mb.getGoalBoard());
		Node start = new Node(mb);
		start.setDepth(0);
		start.setDepth(0);
		start.setH(this.heuristic.calculate(start, goal));
		
		PriorityQueue<Node> open = new PriorityQueue<Node>();
		Set<String> closed = new HashSet<String>();
		open.offer(start);
		
		myBoard.stepCounter = -1;
		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();
		
		while(!open.isEmpty()){
			Node current = open.poll();
			closed.add(current.getHash());
			
			if(current.isGoal()){
				return finalise(current.getBoard(), false);
			}
			
			int depth = current.getDepth() + 1;
			this.updateGUI(displaySearch, current.getBoard());
			
			ArrayList<Node> successors = current.getSuccessors(depth);
			for(Node n: successors){
				
				n.setParent(current);
				n.setG(current.getG() + 1);
				n.setH(this.heuristic.calculate(n, goal));
				
				if(!closed.contains(n.getHash())){
					open.offer(n);
				}
				
			}
		}
		myTile.getSoluLabel().setText("This Board doesn´t have any solution");
		throw new RuntimeException("This Board doesn´t have any solution");
	}

}
