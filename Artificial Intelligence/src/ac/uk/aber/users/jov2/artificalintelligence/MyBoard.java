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

import ac.uk.aber.users.jov2.artificalintelligence.algorithms.AStar;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.BreadthFirstSearch;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.DepthFirstSearch;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.IterativeDeeping;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics.Manhattan;
import ac.uk.aber.users.jov2.artificalintelligence.algorithms.heuristics.TileHeuristic;

/**
 * Define the board and the operations on it. The grid is stored in a 2D array
 * in columns, a 0 is used to indicate the blank
 * 
 * @author Richard Jensen and Jose Vives
 *
 */
public class MyBoard extends Canvas implements MouseListener, Runnable {

	/**
	 * Auto-generated serial for the canvas This needs to be generated
	 */
	private static final long serialVersionUID = 2975289542336640148L;

	/**
	 * The tile instance
	 */
	private Tile tile;

	/**
	 * The size of the puzzle, 3x3
	 */
	public final static int BOARD_SIZE = 3;

	/**
	 * The delay for the Tiles be moved
	 */
	private int delay = INI_DELAY;

	/**
	 * The status the Board can have
	 */
	private int status = IDLE;

	/**
	 * Used to keep track of the numbers of expanded nodes
	 */
	public int stepCounter = -1;

	/**
	 * The thread for the animation
	 */
	private Thread animationThread;

	/**
	 * The grid for this board This Holds the number each tile have
	 */
	private int[][] grid = new int[BOARD_SIZE][BOARD_SIZE];

	/**
	 * The next board
	 */
	public MyBoard next;

	/**
	 * The parent of this board
	 */
	public MyBoard parent;

	/**
	 * The depth of actual board
	 */
	private int depth;

	private Graphics gr = this.getGraphics();

	/**
	 * The graphics width and height
	 */
	private int gWidth, gHeight;

	/**
	 * The user can decide to stop the algorithm if it's taking too long Note
	 * that a new board will need to be created if another algorithm is to be
	 * run after stopping
	 */
	public boolean stopAlgorithm;

	/**
	 * A temporal board for copy proposes
	 */
	private MyBoard temp;

	/**
	 * What the goal state looks like in this representation
	 */
	private int[][] goalState = { { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 } };

	/**
	 * Main constructor of the class of MyBoard
	 * 
	 * @param tile
	 *            The tile instance
	 */
	public MyBoard(Tile tile) {
		this.tile = tile;
		addMouseListener(this);
	}

	/**
	 * Constructor of the class of MyBoard which also sets the depth of the node
	 * 
	 * @param depth
	 *            the int value of the current depth
	 */
	public MyBoard(int depth) {
		this.setDepth(depth);
		addMouseListener(this);
	}

	/**
	 * print the grid in a one dimensional format In this format, the goal would
	 * be printed: { 0 1 2 3 4 5 6 7 8 }
	 * 
	 * @return
	 */
	public String print() {
		String ret = "{ ";
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				ret += grid[j][i] + " ";
			}
		}
		return ret + "}";
	}

	/**
	 * CS26110 Assignment (though not too important)
	 * 
	 * Used for the explored list. Converts the grid into a String and this is
	 * used to determine if the grid has been seen before. The String is used to
	 * obtain a unique integer identifier for the String (and hence the grid)
	 * and this can be used to see if a particular grid is identical to another
	 * one (the integer values will be the same in this case). You could also
	 * use String.equals.
	 * 
	 * @return The generated hash for this grid state
	 */
	public String hash() {
		String ret = "";
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				ret += grid[i][j];
			}
		}
		return ret;
	}

	/**
	 * Start a new Thread to listen to the status change
	 */
	public void start() {
		if ((animationThread == null) || (!animationThread.isAlive())) {
			animationThread = new Thread(this);
		}
		animationThread.start();
	}

	/**
	 * Stop the Thread Look at the Java Tutorial or
	 * http://java.sun.com/products/jdk/1.2/docs/guide/misc/
	 * threadPrimitiveDeprecation.html
	 */
	public void stop() {
		if ((animationThread != null) && (animationThread.isAlive())) {
			animationThread = null;
		}
	}

	/**
	 * Run the Thread. This is required when you implement the Runnable
	 * interface. Namely, repaint the display board according to the board
	 * status
	 */
	public void run() {
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

	/**
	 * Initialises the configuration of this board according to the difficulty
	 * slider. The difficulty slider sets a number of (random) moves to make to
	 * the initial grid to scramble it. The higher the difficulty setting, the
	 * more scrambling will result
	 *
	 * As an aside, this guarantees that a solvable puzzle will be produced.
	 * It's possible to generate unsolvable 8 puzzles if you're not careful
	 * (i.e. just randomly generating grids).
	 */
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

		// Get the value from the GUI
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

	/**
	 * Set the delay value of paint
	 * 
	 * @param newDelay
	 *            The new delay value
	 * @return the delay value
	 */
	public int setDelay(int newDelay) {
		delay = newDelay;
		return delay;
	}

	/**
	 * Paint the entire board
	 * 
	 * @param g
	 *            The Graphics instance for use
	 */
	public void paint(Graphics g) {
		gr = g;
		gWidth = getBounds().width / BOARD_SIZE;
		gHeight = getBounds().height / BOARD_SIZE;
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				drawCell(i, j);
	}

	/**
	 * Paint the entire board with the delay controlled by the slider
	 * 
	 * @param g
	 *            The Graphics instace for use
	 */
	public void paintSlow(Graphics g) {
		repaint();
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	/**
	 * Draw a specific cell on the board
	 * 
	 * @param x
	 *            The x position
	 * @param y
	 *            The y position
	 */
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

	/** Handle the mouse pressed action */
	public void mousePressed(MouseEvent e) {
	}

	/** Handle the mouse exit action */
	public void mouseExited(MouseEvent e) {
	}

	/** Handle the mouse released action */
	public void mouseReleased(MouseEvent e) {
	}

	/** Handle the mouse clicked action */
	public void mouseClicked(MouseEvent e) {
	}

	/** Handle the mouse entered action */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * CS26110 Assignment See whether current configuration is a goal. The
	 * relationship between the graph and coordinates is below.
	 * 
	 * (0,0) (1,0) (2,0) (0,1) (1,1) (2,1) (0,2) (1,2) (2,2)
	 * 
	 * @param mb
	 *            The Board to check if is a goal
	 * @return If is the goal
	 */
	public boolean isGoal(MyBoard mb) {
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				if (mb.grid[i][j] != ((j * BOARD_SIZE) + i)) {
					return false;
				}

		return true;
	}

	/**
	 * Check if the coordinates are legal boar position
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return If is legal thats coordinates
	 */
	public boolean legal(int x, int y) {
		return ((x >= 0) && (x < BOARD_SIZE) && (y >= 0) && (y < BOARD_SIZE));
	}

	/**
	 * Copy the grid from an old board to a new board
	 * 
	 * @param mbNew
	 *            The new board for copy
	 * @param mbOld
	 *            The old board to copy
	 */
	public void copyBoard(MyBoard mbNew, MyBoard mbOld) {
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				mbNew.grid[i][j] = mbOld.grid[i][j];
	}

	/**
	 * Fin the ancestor link of the solution
	 * 
	 * @param mb
	 *            The board who have the solution
	 * @return The board where start the solution
	 */
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

	/**
	 * Display a board List for solution
	 * 
	 * @param mb
	 *            The Board
	 */
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

	/**
	 * After search, play the solution
	 * 
	 * @param mb
	 *            The start board
	 */
	public void play(MyBoard mb) {
		tile.getSliderDisplay().setValue(INI_DELAY);
		displaySolution(findAncestors(mb));
		status = IDLE;
	}

	/**
	 * CS26110 Assignment
	 * 
	 * Expand all the possible succeeding configurations. These are the actions.
	 * The current board has 2, 3, or 4 succeeding boards. You might need to
	 * alter this method depending on how you implement the heuristic
	 * calculations
	 * 
	 * @param mb
	 *            The board to expand
	 * @param list
	 *            The list to add all his succesors
	 * @param depth
	 *            The depth we are currently
	 */
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

	/**
	 * Depth First Search (DFS Uses a stack. (last-in-last-out) As you will see,
	 * DFS is not a good for solving 8 puzzles...
	 * 
	 * @param mb
	 *          The board to solve
	 * @return
	 * 			The board solved
	 */
	public MyBoard dfs(MyBoard mb) {
		DepthFirstSearch dfs = new DepthFirstSearch(this, tile);
		return dfs.solveWithTime(mb);
	}

	/**
	 * Iterative Deeping Search (DPS)
	 * An incremental depth limit until solution is reached
	 * @param mb
	 * 			The board to solve
	 * @return
	 * 			The board solved
	 */
	public MyBoard iterativeDeepening(MyBoard mb) {
		IterativeDeeping id = new IterativeDeeping(this, tile);
		return id.solveWithTime(mb);
	}

	/**
	 * CS26110 Assignment
	 * Create a A* object and the heuristic we want use
	 * This method uses the Manhattan heuristic
	 * 
	 * @param mb
	 * 		The board for solve
	 * @return
	 * 		The board solved
	 */
	public MyBoard aStar2(MyBoard mb) {
		AStar as = new AStar(this, tile, new Manhattan());
		return as.solveWithTime(mb);
	}

	/**
	 * CS26110 Assignment
	 * Create a A* object and the heuristic we want use
	 * This method use the TileHeuristic
	 * 
	 * @param mb
	 * 		The board for solve
	 * @return
	 * 		The board solved
	 */
	public MyBoard aStarTiles(MyBoard mb) {
		AStar as = new AStar(this, tile, new TileHeuristic());
		return as.solveWithTime(mb);
	}

	/**
	 * Breadth First Search (BFS)
	 * Uses a queue to store unexpanded nodes
	 * @param mb
	 * 		The board to solve
	 * @return
	 * 		The Board solved
	 */
	public MyBoard bfs(MyBoard mb) {
		BreadthFirstSearch bfs = new BreadthFirstSearch(this, tile);
		return bfs.solveWithTime(mb);
	}
	
	/**
	 * Expand all the possibles succeeding configurations
	 * The current board has 2, 3 or 4 succeeding boards, return the list
	 * @param mb
	 * 		The board to get all possibles successors
	 * @return
	 * 		The List of all possibles successors
	 */
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

	/**
	 * Set the actual state of this board
	 * @param grid
	 * 		The grid we want set
	 */
	private void setState(int[][] grid) {
		this.grid = grid;
	}

	/**
	 * Create a boar who have the goal state
	 * @return
	 * 		A board with the goal state
	 */
	public MyBoard getGoalBoard() {
		MyBoard goal = new MyBoard(-1);
		goal.setState(getGoalState());
		return goal;
	}

	/**
	 * Get the grid for the goal state
	 * @return
	 * 		The grid with the goal state
	 */
	public int[][] getGoalState() {
		return this.goalState;
	}

	/**
	 * Get the actual grid for this state
	 * @return
	 * 		The grid from this state
	 */
	public int[][] getGrid() {
		return this.grid;
	}

	/**
	 * Get the status
	 * @return
	 * 		The status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set the status
	 * @param newStatus
	 * 		The new status we want
	 */
	public void setStatus(int newStatus) {
		status = newStatus;
	}

	/**
	 * Get the depth of this board
	 * @return
	 * 		The depth of this board
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * Set the depth of this board
	 * @param depth
	 * 			The new depth for this board
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

}