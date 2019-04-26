import java.util.*;

public class Node implements Comparator<Node> {
	int row; //X-coordinate
	int col; //Y-coordinate
	Node parent;
	Node[] child;
	int pathCost;
	int depth;
	int heuristic;
	
	Node() {
		
	}
	
	Node(int r, int c, Node p, Node[] ch, int pc) {
		row = r;
		col = c;
		parent = p;
		child = ch;
		pathCost = pc;
		depth = parent.depth + 1;
	}
	
	Node(int r, int c) {
		row = r;
		col = c;
		pathCost = 0;
		depth = 0;
	}
	
	Node(int r, int c, Node p, int pc) {
		row = r;
		col = c;
		parent = p;
		pathCost = pc;
		depth = parent.depth + 1;
	}
	
	void printNode() {
		System.out.println("(" + row + ", " + col + ")");
		System.out.println("Cost: " + pathCost);
		System.out.println("Depth: " + depth);;
		System.out.println();
	}
	
	@Override
	public int compare(Node first, Node second) {
		return (first.heuristic + first.pathCost) - (second.heuristic + second.pathCost);
	}

}
