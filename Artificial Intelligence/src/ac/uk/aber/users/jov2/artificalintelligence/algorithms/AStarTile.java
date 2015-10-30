package ac.uk.aber.users.jov2.artificalintelligence.algorithms;

import java.util.ArrayList;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.Tile;
import ac.uk.aber.users.jov2.artificalintelligence.util.Node;

public class AStarTile extends Algorithm {

	public AStarTile(MyBoard myBoard, Tile myTile) {
		super(myBoard, myTile);
	}

	@Override
	public MyBoard solve(final MyBoard mb) {
		
		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> closed = new ArrayList<Node>();
		
		Node start = new Node(mb);
		open.add(start);
		
		myBoard.stepCounter = -1;
		myTile.getSoluLabel().setText("Searching ...");
		boolean displaySearch = myTile.getCBDisplay().getState();
		
		Node current = null;
		while(!open.isEmpty()){
			current = this.getLowestF(open);
			closed.add(current);
			open.remove(current);
			
			if(myBoard.isGoal(current.getBoard()))
				break;

			depth++;
			this.updateGUIWithDetph(displaySearch, myBoard, depth);
			
			ArrayList<MyBoard> neighbours = new ArrayList<MyBoard>();
			myBoard.expandAll(current.getBoard(), neighbours);
			
			for(MyBoard board: neighbours){
				Node node = new Node(board);
				node.setParent(current);
				node.setG(current.getG() + node.getH());
				if(!open.contains(node))
					open.add(node);
			}
		}
		return finalise(current.getBoard(), displaySearch);
	}
	
	private Node getLowestF(ArrayList<Node> open){
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