package ac.uk.aber.users.jov2.artificalintelligence.util;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node>{

	@Override
	public int compare(Node node1, Node node2) {
		int r = (node1.getF()) - (node2.getF());
		return r;
	}

}
