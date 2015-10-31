package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.ArrayList;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics.Heuristic;
import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

public class AStar extends Algorithm {
	
	private final Heuristic heuristic;

	public AStar(MyBoard myBoard, Tile myTile, Heuristic heuristic) {
		super(myBoard, myTile);
		this.heuristic = heuristic;
	}
	
	@Override
	public MyBoard solve(MyBoard mb) {
		
		myBoard.stepCounter = -1;
		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();
		
		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> closed = new ArrayList<Node>();
		
		Node start = new Node(mb, this.heuristic);
		start.setG(0);
		start.setDepth(0);
		start.knowH();
		
		open.add(start);
		
		Node current = null;
		while(!open.isEmpty() && !myBoard.stopAlgorithm){
			current = getLowest(open);
			if(current.isGoal()){
				System.out.println("done");
				return finalise(current.getBoard(), displaySearch, current.getDepth());
			}
			open.remove(current);
			closed.add(current);
			int depth = current.getDepth() + 1;
			this.updateGUIWithDetph(displaySearch, current.getBoard(), depth);
			ArrayList<Node> succesors = current.getSuccessors(depth);
			for(Node node: succesors){
				node.setParent(current);
				if(!closed.contains(node)){
					if(!open.contains(node)){
						open.add(node);
					}else{
						Node openN = open.get(open.indexOf(node));
						if(node.getG() < openN.getG()){
							openN.setG(node.getG());
							openN.setParent(node.getParent());
						}
					}
				}
			}
		}
		System.out.println("path not finded");
		return null;
	}
	
	private Node getLowest(ArrayList<Node> open){
		Node lowest = null;
		int lowestF = Integer.MAX_VALUE;
		for(Node n: open){
			if(n.getF() < lowestF){
				lowest = n;
				lowestF = lowest.getF();
			}
		}
		return lowest;
	}
	
}

