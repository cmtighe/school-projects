/**
 * Author: Topher Tighe (2/15/17)
 * 
 */

import java.util.*;
import java.io.*;

public class Pathfinding {
	public static void main(String[] args) throws IOException {
		Scanner file = new Scanner(new FileReader(args[0]));
		
		/*Build maze*/
		int rows = Integer.parseInt(file.next());
		int cols = Integer.parseInt(file.next());
		int[][] maze = new int[rows][cols];
		Node root = new Node(Integer.parseInt(file.next()), Integer.parseInt(file.next()));
		int[] goal = {Integer.parseInt(file.next()), Integer.parseInt(file.next())}; //Since we only need to compare
																					 //coordinates, Node is not required.
		
		/*Fill maze*/
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				maze[i][j] = Integer.parseInt(file.next());
			}
		}
		
		System.out.println("Pathfinding...");
		if (args[1].charAt(0) == 'B') {
			BFS(root, goal, maze);
		} else if (args[1].charAt(0) == 'I') {
			IDS(root, goal, maze);
		} else if (args[1].charAt(0) == 'A') {
			AS(root, goal, maze);
		} else {
			System.out.println("Bad input. Please enter file and 'B', 'I', or 'A' without the quotes");
		}
		
		printMap(rows, cols, root, goal, maze);
		
		file.close();
	}//End main
	
	
	//Prints the map
	static void printMap(int rows, int cols, Node root, int[] goal, int[][] maze) {
		System.out.println(rows + " " + cols);
		System.out.println(root.row + " " + root.col);
		System.out.println(goal[0] + " " + goal[1]);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.print(maze[i][j] + " ");
			}
			System.out.println();
		}
	}//End testPrint
	
	
	//Nodes are stored starting at North, going in clockwise order.
	static Node[] successors(Node parent, int[][] maze) {
		Node[] child = new Node[4];
		if (parent.row > 0 && maze[parent.row - 1][parent.col] != 0) {
			child[0] = new Node((parent.row - 1), parent.col, parent, (maze[parent.row - 1][parent.col] + parent.pathCost));
		}
		if (parent.col < (maze[0].length - 1) && maze[parent.row][parent.col + 1] != 0) { //map[0] because it is a uniform size.
			child[1] = new Node(parent.row, (parent.col + 1), parent, (maze[parent.row][parent.col + 1] + parent.pathCost));
		}
		if (parent.row < (maze.length - 1) && maze[parent.row + 1][parent.col] != 0) {
			child[2] = new Node((parent.row + 1), parent.col, parent, (maze[parent.row + 1][parent.col] + parent.pathCost));
		}
		if (parent.col > 0 && maze[parent.row][parent.col - 1] != 0) {
			child[3] = new Node(parent.row, (parent.col - 1), parent, (maze[parent.row][parent.col - 1] + parent.pathCost));
		}
		return child;
	}//End successors
	
	
	//Dequeues from fringe, generates successors, then enqueues them in the order of Breadth-First Search.
	static void BFS(Node root, int[] goal, int[][] maze) {
		LinkedList<Node> fringe = new LinkedList<Node>();
		fringe.add(root);
		Node current = fringe.peek(); //Peek to initialize current.
		LinkedList<Node> expanded = new LinkedList<Node>();
		boolean found = false;
		int max = fringe.size();
		
		long start = System.currentTimeMillis();
		while (!found && !fringe.isEmpty()  && (System.currentTimeMillis() - start < 180000)) {
			max = Math.max(max, fringe.size());         //3-minute cut off time.
			current = fringe.poll();
			expanded.add(current);
			
			if (current.row == goal[0] && current.col == goal[1]) {
				found = true;
				break;
			}
			current.child = successors(current, maze);
		
			//Check for repeats and add.
			for (int i = 0; i < current.child.length; i++) {
				if (current.child[i] != null) {
					if (betterThanFringe(current.child[i], fringe) && betterThanExpanded(current.child[i], expanded)) {
						fringe.add(current.child[i]);
					}
				}
			}
		}//End while loop
		long end = System.currentTimeMillis();
		long time = end - start;
		
		if (found) {
			System.out.println("RESULTS:");
			trail(current);
			System.out.println("Expanded: " + expanded.size() + " nodes.");
			System.out.println("Maximum in memory: " + max + " nodes.");
			System.out.println("Runtime: " + time + " ms\n");
		} else {
			System.out.println("RESULTS:");
			System.out.println("NULL PATH: cost -1");
			System.out.println("Expanded: " + expanded.size() + " nodes.");
			System.out.println("Maximum in memory: " + max + " nodes.");
			System.out.println("Runtime: " + time + " ms\n");
			System.out.println("Something's wrong, boss. It didn't work");
		}
	}//End BFS
	
	
	//Wraps around IDS_meat into run it multiple times. Each time, the search goes slightly deeper.
	static void IDS(Node root, int[] goal, int[][] maze) {
		boolean found = false;
		int depth = 0;
		
		long start = System.currentTimeMillis();
		while (!found && (System.currentTimeMillis() - start < 180000)) {
			//System.out.println("D: " + depth + " Time: " + (System.currentTimeMillis() - start));
			found = IDS_meat(root, goal, maze, depth, start);
			depth++;
		}
		long end = System.currentTimeMillis();
		long time = end - start;
		
		if (found) {
			System.out.println("Runtime: " + time + " ms\n");
		} else {
			System.out.println("NULL PATH: cost -1");
			System.out.println("Runtime: " + time + " ms\n");
			System.out.println("Something's wrong, boss. It didn't work");
			
		}
	}//End IDS
	
	//Dequeues from fringe, generates successors, then enqueues them in the order of Iterative-Deepening Search.
	static boolean IDS_meat(Node root, int[] goal, int[][] maze, int depth, long start) {
		LinkedList<Node> fringe = new LinkedList<Node>();
		fringe.add(root);
		Node current = fringe.peek(); //Peek to initialize current.
		LinkedList<Node> expanded = new LinkedList<Node>();
		boolean found = false;
		int max = fringe.size();
		
		while (!found && !fringe.isEmpty() && (System.currentTimeMillis() - start < 180000)) {
			max = Math.max(max, fringe.size());
			current = fringe.poll();
			expanded.add(current);
			
			if (current.row == goal[0] && current.col == goal[1]) {
				found = true;
				break;
			}
			current.child = successors(current, maze);
		
			//Check for repeats and add.
			for (int i = 0; i < current.child.length; i++) {
				if (current.child[i] != null) {
					if (current.child[i].depth <= depth) {
						if (betterThanFringe(current.child[i], fringe) && betterThanExpanded(current.child[i], expanded)) {
							fringe.add(0, current.child[i]); //Adds to the front of the list, DFS.
						}
					}
				}
			}
		}//End while loop
		if (found) {
			System.out.println("RESULTS:");
			trail(current);
			System.out.println("Expanded: " + expanded.size() + " nodes.");
			System.out.println("Maximum in memory: " + max + " nodes.");
		} else if (System.currentTimeMillis() - start > 180000) {
			System.out.println("RESULTS:");
			System.out.println("Expanded: " + expanded.size() + " nodes.");
			System.out.println("Maximum in memory: " + max + " nodes.");
		}
		return found;
	}//End IDS_meat
	
	
	//Same as the others, except it uses manhattan distance as a heuristic to calculate best route.
	static void AS(Node root, int[] goal, int[][] maze) {
		Comparator<Node> compare = new Node();
		PriorityQueue<Node> fringe = new PriorityQueue<Node>(compare);
		LinkedList<Node> mockFringe = new LinkedList<Node>(); //So that betterThanFringe will still work.
		
		fringe.add(root);
		mockFringe.add(root);
		manhattan(root, goal); //Calculate first heuristic
		Node current = fringe.peek(); //Peek to initialize current.
		LinkedList<Node> expanded = new LinkedList<Node>();
		boolean found = false;
		int max = fringe.size();
		
		long start = System.currentTimeMillis();
		while (!found && !fringe.isEmpty() && (System.currentTimeMillis() - start < 180000)) {
			max = Math.max(max, fringe.size());         //3-minute cut off time.
			current = fringe.poll();
			mockFringe.poll();
			expanded.add(current);
			
			if (current.row == goal[0] && current.col == goal[1]) {
				found = true;
				break;
			}
			current.child = successors(current, maze);
		
			//Check for repeats and add.
			for (int i = 0; i < current.child.length; i++) {
				if (current.child[i] != null) {
					if (betterThanFringe(current.child[i], mockFringe) && betterThanExpanded(current.child[i], expanded)) {
						fringe.add(current.child[i]);
						mockFringe.add(current.child[i]);
					}
				}
			}//End i for loop
		}//End while loop
		long end = System.currentTimeMillis();
		long time = end - start;
		
		if (found) {
			System.out.println("RESULTS:");
			trail(current);
			System.out.println("Expanded: " + expanded.size() + " nodes.");
			System.out.println("Maximum in memory: " + max + " nodes.");
			System.out.println("Runtime: " + time + " ms\n");
		} else {
			System.out.println("RESULTS:");
			System.out.println("NULL PATH: cost -1");
			System.out.println("Expanded: " + expanded.size() + " nodes.");
			System.out.println("Maximum in memory: " + max + " nodes.");
			System.out.println("Runtime: " + time + " ms\n");
			System.out.println("Something's wrong, boss. It didn't work");
		}
	}//End AS
	
	
	//Calculates Manhattan distance for the heuristic.
	static void manhattan(Node current, int[] goal) {
		int yDist = Math.abs(goal[0] - current.row);
		int xDist = Math.abs(goal[1] - current.col);
		current.heuristic = xDist + yDist;
	}//End manhattan
	
	
	//Checks the fringe for a repeat of the candidate. Returns true if there is no repeat,
	//or if the candidate is better than the repeat.
	static boolean betterThanFringe(Node candidate, LinkedList<Node> fringe) {
		for (int i = 0; i < fringe.size(); i++) {
			if (candidate.row == fringe.get(i).row && candidate.col == fringe.get(i).col) {
				if (candidate.pathCost < fringe.get(i).pathCost) {
					fringe.remove(i);
					return true; //Candidate is better, so we want to add and replace the other.
				}
				return false; //Found a repeat, and it was better, so we don't want to add.
			}
		}
		return true; //Didn't find a repeat, so we want to add.
	}//End betterThanFringe
	
	
	//Checks the expanded list for a repeat of the candidate. Returns true if there is no repeat,
	//or if the candidate is better than the repeat.
	static boolean betterThanExpanded(Node candidate, LinkedList<Node> expanded) {
		for (int i = 0; i < expanded.size(); i++) {
			if (candidate.row == expanded.get(i).row && candidate.col == expanded.get(i).col) {
				if (candidate.pathCost < expanded.get(i).pathCost) {
					return true; //Candidate is better, we want to add.
				}
				return false; //Found a repeat, and it was better, so we don't want to add.
			}
		}
		return true; //Didn't find a repeat, so we want to add.
	}//End betterThanExpanded

	
	//Trails the successful path backwards to the root, the prints out the whole path.
	static void trail(Node end) {
		if (end.parent != null) {
			trail(end.parent);
		}
		end.printNode();
	}//End trail
	
}//End Pathfinding
