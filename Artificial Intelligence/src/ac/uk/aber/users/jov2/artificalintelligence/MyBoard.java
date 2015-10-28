package ac.uk.aber.users.jov2.artificalintelligence;

import static ac.uk.aber.users.jov2.artificalintelligence.Tile.IDLE;
import static ac.uk.aber.users.jov2.artificalintelligence.Tile.INI_DELAY;
import static ac.uk.aber.users.jov2.artificalintelligence.Tile.PLAY;
import static ac.uk.aber.users.jov2.artificalintelligence.Tile.RANDOMIZE;
import static ac.uk.aber.users.jov2.artificalintelligence.Tile.START;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import ac.uk.aber.users.jov2.artificalintelligence.algorithms.AStar2;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.AStarTile;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.BreadthFirstSearch;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.DepthFirstSearch;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.IterativeDeeping;

// CS26110 Assignment
//----------------------------------------------------------------
// Define the board and the operations on it.
// The grid is stored in a 2D array in columns, a 0 is used to indicate the blank
//----------------------------------------------------------------
public class MyBoard extends Canvas implements MouseListener, Runnable, Comparable<MyBoard> {
	
	private static final long serialVersionUID = 2975289542336640148L;

	private Tile tile;
	
	final static int BOARD_SIZE = 3; // the size of the puzzle, 3x3
	int delay = INI_DELAY;
	public int status = IDLE;
	public int stepCounter = -1; // used to keep track of the number of expanded nodes
	private Thread animationThread;
	int[][] grid = new int[BOARD_SIZE][BOARD_SIZE]; // the 3x3 grid of 'tiles' =
													// just integers
	public MyBoard next;
	public MyBoard parent; // the parent of this board - used to trace back the path to
					// the solution from the goal node/board
	Graphics gr = this.getGraphics();
	int gWidth;
	int gHeight;

	// the user can decide to stop the algorithm if it's taking too long
	// Note that a new board will need to be created if another algorithm is to
	// be run after stopping
	public boolean stopAlgorithm;

	MyBoard temp;
	public int depth; // the depth of the node in the search, corresponds to g(n)

	// CS26110 Assignment
	/**
	 * These are currently unused but might be useful for your A* and heuristic
	 * implementation...
	 **/
	int heuristic; // h(n)
	int f; // f(n)

	// What the goal state looks like in this representation
	int[][] goalState = { { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 } };

	// These map from the tile to its coordinates in the goalState array
	int[] xcoord = { 0, 1, 2, 0, 1, 2, 0, 1, 2 };
	int[] ycoord = { 0, 0, 0, 1, 1, 1, 2, 2, 2 };

	// Constructor of the class of MyBoard
	// ---------------------------------------------------------
	public MyBoard(Tile tile) {
		this.tile = tile;
		addMouseListener(this);
		depth = 0;
	}

	// Constructor of the class of MyBoard which also sets the depth of the node
	// ---------------------------------------------------------
	public MyBoard(int d) {
		addMouseListener(this);
		depth = d;
	}

	// Reset the status of the board.
	// --------------------------------------------------------
	public void setStatus(int newStatus) {
		status = newStatus;
	}

	// print the grid in a one dimensional format
	// In this format, the goal would be printed: { 0 1 2 3 4 5 6 7 8 }
	public String print() {
		String ret = "{ ";

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				ret += grid[j][i] + " ";
			}
		}

		return ret + "}";
	}

	// CS26110 Assignment (though not too important)
	// Used for the explored list. Converts the grid into a String and this is
	// used to determine if the grid has been seen before.
	// The String is used to obtain a unique integer identifier for the String
	// (and hence the grid) and this can be used to see if
	// a particular grid is identical to another one (the integer values will be
	// the same in this case). You could also use String.equals.
	public String hash() {
		String ret = "";

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				ret += grid[i][j];
			}
		}

		return ret;
	}

	// Start a new Thread to listen to the status change.
	// ---------------------------------------------------------
	public void start() {
		if ((animationThread == null) || (!animationThread.isAlive())) {
			animationThread = new Thread(this);
		}

		animationThread.start();
	}

	// Stop the Thread.
	// ------------------------------------------------------------
	public void stop() {
		if ((animationThread != null) && (animationThread.isAlive())) {
			// look at the Java Tutorial or
			// http://java.sun.com/products/jdk/1.2/docs/guide/misc/threadPrimitiveDeprecation.html
			animationThread = null;
		}
	}

	// Run the thread. This is required when you implement the Runnable
	// interface.
	// Namely, repaint the display board according to the board status.
	// -----------------------------------------------------------
	public void run() {
		System.out.println("Tile Puzzle is running ... ...");

		while (true) {
			switch (status) {
			case RANDOMIZE:
				temp = null;
				initBoard();
				paintSlow(this.getGraphics());

				break;

			case START:

				if (tile.getCBG().getSelectedCheckbox() == tile.getCBBFS()) {
					temp = bfs(this);
				} else if (tile.getCBG().getSelectedCheckbox() == tile.getCBDFS()) {
					temp = dfs(this);

				} else if (tile.getCBG().getSelectedCheckbox() == tile.getCBIT()) {
					temp = iterativeDeepening(this);
				} else if (tile.getCBG().getSelectedCheckbox() == tile.getAStart()) {
					temp = aStar2(this);
				} else if (tile.getCBG().getSelectedCheckbox() == tile.getAstartTiles()) {
					temp = aStarTiles(this);
				}

				break;

			case PLAY:
				play(temp);

				break;

			default:
				break;
			}
		}
	}

	// Initialises the configuration of this board according to the difficulty
	// slider.
	// The difficulty slider sets a number of (random) moves to make to the
	// initial grid to
	// scramble it. The higher the difficulty setting, the more scrambling will
	// result
	//
	// As an aside, this guarantees that a solvable puzzle will be produced.
	// It's possible to
	// generate unsolvable 8 puzzles if you're not careful (i.e. just randomly
	// generating grids).
	// -----------------------------------------------------------
	public void initBoard() {
		int difficulty;
		int counter;
		int rand;
		MyBoard auxBoard = new MyBoard(tile);
		MyBoard temp;

		stopAlgorithm = false;
		tile.getSliderDisplay().setValue(INI_DELAY);
		tile.getStepCounterLabel().setText("<html>Nodes expanded: <br>0</html>");
		tile.getSoluLabel().setText("Initial Board");

		// Initialise the grid
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				grid[i][j] = (j * BOARD_SIZE) + i;

		// get the value from the GUI
		difficulty = tile.getSliderRandomizer().getValue();

		// For each difficulty 'step', make a random move
		// This can actually go backwards by undoing the previously made move
		while (difficulty > 0) {
			auxBoard.next = expandAll(this);
			temp = auxBoard.next;
			counter = 0;

			while (temp != null) {
				temp = temp.next;
				counter++;
			}

			// Select a random successor from all the successors
			rand = (int) (counter * Math.random()) + 1;

			while (rand > 0) {
				auxBoard = auxBoard.next;
				rand--;
			}

			copyBoard(this, auxBoard);

			difficulty--;
		}

		repaint();
		tile.getSliderDisplay().setValue(INI_DELAY);
		status = IDLE;
	}

	// Set the delay value of paint
	// ---------------------------------------------------------
	public int setDelay(int newDelay) {
		delay = newDelay;

		return delay;
	}

	// Paint the entire board.
	// --------------------------------------------------------
	public void paint(Graphics g) {
		gr = g;
		gWidth = getBounds().width / BOARD_SIZE;
		gHeight = getBounds().height / BOARD_SIZE;

		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				drawCell(i, j);
	}

	// Paint the entire board with the delay controlled by the slider.
	// ---------------------------------------------------------------
	public void paintSlow(Graphics g) {
		repaint();

		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	// Draw a specific cell on the board.
	// ---------------------------------------------------------
	public void drawCell(int x, int y) {
		// Draw the outline of the cell.
		gr.setColor(Color.black);
		gr.drawRect(x * gWidth, y * gHeight, gWidth, gHeight);

		// Draw the background of the cell.
		gr.setColor(Color.black);
		gr.setFont(new Font("SansSerif", Font.BOLD, 14));
		gr.drawRect((x * gWidth) + 4, (y * gHeight) + 4, gWidth - 8, gHeight - 8);

		// Draw the tile in the cell.
		switch (grid[x][y]) {
		case 0:
			break;

		default:
			gr.setColor(Color.white);
			gr.fillRect((x * gWidth) + 15, (y * gHeight) + 15, gWidth - 30, gHeight - 30);
			gr.setColor(Color.black);
			gr.drawChars(Integer.toString(grid[x][y]).toCharArray(), 0, Integer.toString(grid[x][y]).length(),
					(x * gWidth) + (gWidth / 2), (y * gHeight) + (gHeight / 2));

			break;
		}
	}

	// Handle the mouse actions.
	// The five methods are required when you implement the MouseListener
	// interface.
	// --------------------------------------------------------------------------
	public void mousePressed(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	// CS26110 Assignment
	// See whether current configuration is a goal.
	// The relationship between the graph and coordinates is below:
	// (0,0) (1,0) (2,0)
	// (0,1) (1,1) (2,1)
	// (0,2) (1,2) (2,2)
	// -----------------------------------------------------------------------
	public boolean isGoal(MyBoard mb) {
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				if (mb.grid[i][j] != ((j * BOARD_SIZE) + i)) {
					return false;
				}

		return true;
	}

	// Returns true if the coordinates are a legal board position.
	// -----------------------------------------------------------------------
	public boolean legal(int x, int y) {
		return ((x >= 0) && (x < BOARD_SIZE) && (y >= 0) && (y < BOARD_SIZE));
	}

	// Copy the grid from an old board to a new board.
	// -----------------------------------------------------------------------
	public void copyBoard(MyBoard mbNew, MyBoard mbOld) {
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				mbNew.grid[i][j] = mbOld.grid[i][j];
	}

	// find the ancestor link of the solution board.
	// ------------------------------------------------------------------------
	public MyBoard findAncestors(MyBoard mb) {
		MyBoard boardList = null;
		MyBoard temp;
		MyBoard tempNew;

		temp = mb;

		while (temp != null) {
			tempNew = new MyBoard(tile);
			copyBoard(tempNew, temp);
			tempNew.next = boardList;
			boardList = tempNew;
			temp = temp.parent;
		}

		return boardList;
	}

	// display a board List for a solution.
	// ------------------------------------------------------------------------
	public void displaySolution(MyBoard mb) {
		MyBoard temp = mb;
		stepCounter = -1;

		while (temp != null) {
			stepCounter++;
			tile.getStepCounterLabel().setText("<html>Solution step: <br>" + Integer.toString(stepCounter) + "</html>");
			copyBoard(this, temp);
			paintSlow(this.getGraphics());
			temp = temp.next;
		}
	}

	// After search, play the solution.
	// ------------------------------------------------------------------------
	public void play(MyBoard mb) {
		tile.getSliderDisplay().setValue(INI_DELAY);
		displaySolution(findAncestors(mb));
		status = IDLE;
	}

	// CS26110 Assignment
	// Expand all the possible succeeding configurations. These are the actions.
	// The current board has 2, 3, or 4 succeeding boards.
	// You might need to alter this method depending on how you implement the
	// heuristic calculations
	// ------------------------------------------------------------------------
	public void expandAll(MyBoard mb, Collection<MyBoard> list, int depth) {
		int p = -1;
		int q = -1;

		// locate the "0" tile.
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				if (mb.grid[i][j] == 0) {
					p = i;
					q = j;
				}

		if (legal(p, q - 1)) {
			MyBoard child = new MyBoard(depth);
			copyBoard(child, mb);

			child.grid[p][q - 1] = mb.grid[p][q];
			child.grid[p][q] = mb.grid[p][q - 1];
			child.parent = mb;

			list.add(child);
		}

		if (legal(p, q + 1)) {
			MyBoard child = new MyBoard(depth);
			copyBoard(child, mb);

			child.grid[p][q + 1] = mb.grid[p][q];
			child.grid[p][q] = mb.grid[p][q + 1];
			child.parent = mb;

			list.add(child);
		}

		if (legal(p - 1, q)) {
			MyBoard child = new MyBoard(depth);
			copyBoard(child, mb);

			child.grid[p - 1][q] = mb.grid[p][q];
			child.grid[p][q] = mb.grid[p - 1][q];
			child.parent = mb;

			list.add(child);
		}

		if (legal(p + 1, q)) {
			MyBoard child = new MyBoard(depth);
			copyBoard(child, mb);

			child.grid[p + 1][q] = mb.grid[p][q];
			child.grid[p][q] = mb.grid[p + 1][q];
			child.parent = mb;

			list.add(child);
		}

	}

	// Depth first search (DFS)
	// Uses a stack. (last-in-first-out)
	// As you will see, DFS is not good for solving 8 puzzles...
	// --------------------------------------------------------------------
	public MyBoard dfs(MyBoard mb) {
		DepthFirstSearch dfs = new DepthFirstSearch(this, tile);
		return dfs.solve(mb);
	}

	// Iterative deepening search - an incremental depth limit until a solution
	// is reached
	public MyBoard iterativeDeepening(MyBoard mb) {
		IterativeDeeping id = new IterativeDeeping(this, tile);
		return id.solve(mb);
	}

	// CS26110 Assignment
	// You need to write the code for this method
	public MyBoard aStar2(MyBoard mb) {
		AStar2 as2 = new AStar2(this, tile);
		return as2.solve(mb);
	}

	// CS26110 Assignment
	// You need to write the code for this method
	public MyBoard aStarTiles(MyBoard mb) {
		AStarTile ast = new AStarTile(this, tile);
		return ast.solve(mb);
	}

	// CS26110 Assignment - use the structure of this algorithm as a basis for
	// your A* implementation
	// Breadth first search (BFS) uses a queue to store unexpanded nodes
	// (queue: first-in-first-out)
	// -------------------------------------------------------------------------
	public MyBoard bfs(MyBoard mb) {
		BreadthFirstSearch bfs = new BreadthFirstSearch(this, tile);
		return bfs.solve(mb);
	}

	// Used elsewhere - ignore this
	// Expand all the possible succeeding configurations.
	// The current board has 2, 3, or 4 succeeding boards, return the list.
	// ------------------------------------------------------------------------
	public MyBoard expandAll(MyBoard mb) {
		int p = -1;
		int q = -1;
		MyBoard nextBoardHead = new MyBoard(tile);
		MyBoard tempBoard = nextBoardHead;

		// locate the "0" tile.
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				if (mb.grid[i][j] == 0) {
					p = i;
					q = j;
				}

		if (legal(p, q - 1)) {
			tempBoard.next = new MyBoard(tile);
			tempBoard = tempBoard.next;

			copyBoard(tempBoard, mb);

			tempBoard.grid[p][q - 1] = mb.grid[p][q];
			tempBoard.grid[p][q] = mb.grid[p][q - 1];
			tempBoard.parent = mb;
		}

		if (legal(p, q + 1)) {
			tempBoard.next = new MyBoard(tile);
			tempBoard = tempBoard.next;

			copyBoard(tempBoard, mb);

			tempBoard.grid[p][q + 1] = mb.grid[p][q];
			tempBoard.grid[p][q] = mb.grid[p][q + 1];
			tempBoard.parent = mb;
		}

		if (legal(p - 1, q)) {
			tempBoard.next = new MyBoard(tile);
			tempBoard = tempBoard.next;

			copyBoard(tempBoard, mb);

			tempBoard.grid[p - 1][q] = mb.grid[p][q];
			tempBoard.grid[p][q] = mb.grid[p - 1][q];
			tempBoard.parent = mb;
		}

		if (legal(p + 1, q)) {
			tempBoard.next = new MyBoard(tile);
			tempBoard = tempBoard.next;

			copyBoard(tempBoard, mb);

			tempBoard.grid[p + 1][q] = mb.grid[p][q];
			tempBoard.grid[p][q] = mb.grid[p + 1][q];
			tempBoard.parent = mb;
		}

		return nextBoardHead.next;
	}

	// CS26110 Assignment
	// This doesn't work at the moment - you'll need to make sure that the
	// variable 'f' is calculated correctly elsewhere
	// Once 'f' is calculated correctly, this will order the MyBoard states in a
	// priority queue correctly for A* search.
	public int compareTo(MyBoard board) {
		// TODO
		return this.f - board.f;
	}

}