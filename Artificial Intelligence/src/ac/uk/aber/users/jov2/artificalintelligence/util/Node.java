package ac.uk.aber.users.jov2.artificalintelligence.util;

import java.util.ArrayList;
import java.util.Arrays;

import ac.uk.aber.users.jov2.artificalintelligence.MyBoard;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics.Heuristic;

public class Node {
	
	private Heuristic heuristic;
	private Node came_from;
	private int g;
	private int h;
	private MyBoard board;
	
	public Node(MyBoard board, Heuristic heuristic) {
		this(board, heuristic, null, 0, 0);
	};
	
	public Node(MyBoard board, Heuristic heuristic,Node come_from, int g, int h){
		this.board = board;
		this.heuristic = heuristic;
		this.came_from = come_from;
		this.g = g;
		this.h = h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node))
			return false;
		if(obj == this)
			return true;
		Node that = (Node) obj;
		return Arrays.equals(board.getGrid(), that.getBoard().getGrid());
		
	}
	
	public int getG(){ return this.g;}
	public void setG(int g){ this.g = g; }
	public int getF(){ return this.g + this.h; }
	public int getH(){ return this.h; }
	public void knowH(){ this.h = heuristic.heuristic(this); }
	
	public Node getParent(){
		return this.came_from;
	}
	
	public void setParent(Node parent){
		this.came_from = parent;
		board.parent = parent.getBoard();
	}
	
	public MyBoard getBoard() {
		return this.board;
	}

	public boolean isGoal() {
		return this.board.isGoal(this.board);
	}
	
	public int getDepth(){
		return this.getBoard().getDepth();
	}
	
	public void setDepth(int depth){
		this.getBoard().setDepth(depth);
	}
	
	public ArrayList<Node> getSuccessors(int depth){
		ArrayList<MyBoard> boards = new ArrayList<MyBoard>();
		this.getBoard().expandAll(this.getBoard(), boards, depth);
		ArrayList<Node> successors = new ArrayList<Node>();
		for(MyBoard b: boards){
			Node node = new Node(b, this.heuristic);
			node.knowH();
			node.setG(getG() + 10);
			successors.add(node);
		}
		return successors;
	}

}
