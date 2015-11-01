package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.ArrayList;
import java.util.Collection;

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
		
		Node start = new Node(mb);
		start.setG(0);
		start.setDepth(0);
		Node goal = new Node(myBoard.getGoalBoard());

		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> closed = new ArrayList<Node>();
		open.add(start);
		
		myBoard.stepCounter = -1;
		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();

		int depth = start.getDepth();
		
		Node current = null;
		while (!open.isEmpty() && !myBoard.stopAlgorithm) {
			current = getLowest(open);
			if (current.isGoal()) {
				return finalise(current.getBoard(), displaySearch, current.getDepth());
			}
			this.updateGUIWithDetph(displaySearch, current.getBoard(), current.getDepth());
			open.remove(current);
			closed.add(current);
			depth++;
			ArrayList<Node> succesors = current.getSuccessors(depth);
			for (Node node : succesors) {
				node.setG(current.getG() + 1);
				node.setParent(current);
				node.setH(this.heuristic.calculate(node, goal));
				if (!open.contains(node) && !closed.contains(node)) {
					open.add(node);
				} else {
					Node tmp = inList(node, open);
					if(tmp != null){
						if(node.getG() < tmp.getG()){
							open.remove(tmp);
							open.add(node);
						}else{
							tmp = inList(node, closed);
							if(tmp != null){
								if(node.getG() < tmp.getG()){
									closed.remove(tmp);
									closed.add(node);
								}
							}
						}
					}
					
				}
			}
		}
		System.out.println("Not solution founded!");
		return null;
	}
	
	private Node inList(Node node, Collection<Node> list){
		for(Node n: list){
			if(node.equals(n)){
				return n;
			}
		}
		return null;
	}

	private Node getLowest(ArrayList<Node> open) {
		Node lowest = null;
		int lowestF = Integer.MAX_VALUE;
		for (Node n : open) {
			if (n.getF() < lowestF) {
				lowest = n;
				lowestF = lowest.getF();
			}
		}
		return lowest;
	}

}
