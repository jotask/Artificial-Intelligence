package ac.uk.aber.users.jov2.artificalintelligence;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * All the GUI components for the application
 * 
 * Tile-Sliding Puzzle contact: zhengyi.le@dartmouth.edu Date: 1/22/2004
 *
 * @author Richard Jensen and Jose Vives
 *
 */
public class Tile extends JApplet implements ActionListener, ChangeListener {

	// Some constants to be used elsewhere
	private final static int MIN_DELAY = 0;
	private final static int MAX_DELAY = 3000;
	final static int INI_DELAY = 1500;

	private final static int MIN_DIFFICULTY = 0;
	private final static int MAX_DIFFICULTY = 100;
	private final static int INI_DIFFICULTY = 20; // initial difficulty setting

	// Determines the state of the GUI
	private final static int STOP = 0;
	public final static int RANDOMIZE = 1;
	public final static int IDLE = 2;
	public final static int START = 3;
	public final static int PLAY = 4;

	// GUI components
	private final JButton stop = new JButton("Stop Search");
	private final JButton start = new JButton("Start Search");
	private final JButton play = new JButton("Play Solution");
	private final JButton randomize = new JButton("Mix it!");
	private final JSlider sliderDisplay = new JSlider(JSlider.HORIZONTAL, MIN_DELAY, MAX_DELAY, INI_DELAY);
	private final JSlider sliderRandomize = new JSlider(JSlider.HORIZONTAL, MIN_DIFFICULTY, MAX_DIFFICULTY, INI_DIFFICULTY);
	private final JLabel stepCounterLabel = new JLabel("<html>Nodes expanded: <br>" + "0" + "</html>");
	private final JLabel soluLabel = new JLabel(" ");

	// Checkboxes for selecting the algorithms
	private final CheckboxGroup cbg = new CheckboxGroup();
	private final Checkbox cbBfs = new Checkbox("Breadth First", cbg, true);
	private final Checkbox cbDfs = new Checkbox("Depth First", cbg, false);
	private final Checkbox cbIt = new Checkbox("Iterative Deepening", cbg, false);
	private final Checkbox cbAStarTiles = new Checkbox("A*-Hamming", cbg, false);
	private final Checkbox cbAStar = new Checkbox("A*-Manhattan", cbg, false);
	private final Checkbox cbAStarBoth = new Checkbox("A*-TwoH", cbg, false);

	private final Checkbox cbDisplay = new Checkbox("Display Search");
	private final MyBoard board = new MyBoard(this); // this MyBoard b is used to display the
										// graphic of the current board.

	/**
	 * Initialise all the variables and put everything in his place
	 */
	public void init() {
		getContentPane().setLayout(new BorderLayout());

		// Add the north Panel.
		Panel sliderDisplayPanel = new Panel();
		sliderDisplayPanel.setLayout(new GridLayout(3, 1));

		JLabel sliderDisplayLabel = new JLabel("Display Interval : ( ms )", JLabel.CENTER);
		sliderDisplayPanel.add(sliderDisplayLabel);
		sliderDisplayPanel.add(sliderDisplay);
		sliderDisplayPanel.add(new JLabel("  "));

		sliderDisplay.setMajorTickSpacing(MAX_DELAY / 3);
		sliderDisplay.setMinorTickSpacing(MAX_DELAY / 15);
		sliderDisplay.setPaintTicks(true);
		sliderDisplay.setPaintLabels(true);

		getContentPane().add("North", sliderDisplayPanel);

		// Add the board onto the center of the frame.
		getContentPane().add("Center", board);

		// Add the east Panel.
		Panel eastPanel = new Panel();

		eastPanel.setLayout(new BorderLayout());
		eastPanel.add("North", new JLabel("  "));
		eastPanel.add("East", new JLabel("  "));
		eastPanel.add("West", new JLabel("  "));
		eastPanel.add("South", new JLabel("  "));

		Panel eastCenterPanel = new Panel();
		eastCenterPanel.setLayout(new GridLayout(4, 1));

		Panel checkboxPanel = new Panel();
		checkboxPanel.setLayout(new GridLayout(4, 1));
		checkboxPanel.add(cbBfs);
		checkboxPanel.add(cbDfs);
		checkboxPanel.add(cbIt);
		checkboxPanel.add(cbAStarTiles);
		checkboxPanel.add(cbAStar);
		checkboxPanel.add(cbAStarBoth);

		eastCenterPanel.add(checkboxPanel);
		eastCenterPanel.add(cbDisplay);
		eastCenterPanel.add(stepCounterLabel);
		eastCenterPanel.add(soluLabel);

		eastPanel.add("Center", eastCenterPanel);

		getContentPane().add("East", eastPanel);

		// Add a blank west Panel.
		Panel westPanel = new Panel();
		westPanel.setLayout(new GridLayout(1, 1));
		westPanel.add(new JLabel("     "));
		getContentPane().add("West", westPanel);

		// Add the south Panel.
		Panel southPanel = new Panel();
		southPanel.setLayout(new BorderLayout());

		Panel buttonPanel = new Panel();

		buttonPanel.setLayout(new GridLayout(5, 3));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(randomize);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(stop);
		buttonPanel.add(start);
		buttonPanel.add(play);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		southPanel.add("South", buttonPanel);

		Panel sliderRandomizePanel = new Panel();
		sliderRandomizePanel.setLayout(new GridLayout(3, 1));
		sliderRandomizePanel.add(new JLabel(" "));

		JLabel sliderRandomizeLabel = new JLabel("Randomize the initial board:  (easy --> difficult)", JLabel.CENTER);
		sliderRandomizePanel.add(sliderRandomizeLabel);
		sliderRandomizePanel.add(sliderRandomize);

		sliderRandomize.setMajorTickSpacing(MAX_DIFFICULTY / 4);
		sliderRandomize.setMinorTickSpacing(MAX_DIFFICULTY / 20);
		sliderRandomize.setPaintTicks(true);
		sliderRandomize.setPaintLabels(true);

		southPanel.add("North", sliderRandomizePanel);

		getContentPane().add("South", southPanel);
		sliderDisplay.addChangeListener(this);
		sliderRandomize.addChangeListener(this);

		randomize.addActionListener(this);
		stop.addActionListener(this);
		start.addActionListener(this);
		play.addActionListener(this);

		board.start();

	}

	/**
	 * Listen to the actions of the buttons and the slider
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == stop) {
			board.stopAlgorithm = true;
			board.setStatus(STOP);
		} else if (e.getSource() == randomize) {
			board.setStatus(RANDOMIZE);
		} else if (e.getSource() == start) {
			board.setStatus(START);
			stepCounterLabel.setText("<html>Nodes expanded: <br>" + "0" + "</html>");
			soluLabel.setText(" ");
		} else if (e.getSource() == play) {
			board.setStatus(PLAY);
		}
	}

	/**
	 * Reset the delay value of display from the slider
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == sliderDisplay) {
			int value = sliderDisplay.getValue();
			board.setDelay(value);
		}
	}

	// Getters
	public CheckboxGroup getCBG() {
		return this.cbg;
	}

	public Checkbox getCBBFS() {
		return this.cbBfs;
	}

	public Checkbox getCBDFS() {
		return this.cbDfs;
	}

	public Checkbox getCBIT() {
		return this.cbIt;
	}

	public Checkbox getAStart() {
		return this.cbAStar;
	}

	public Checkbox getAStartBoth() {
		return this.cbAStarBoth;
	}

	public Checkbox getAstartTiles() {
		return this.cbAStarTiles;
	}

	public JSlider getSliderDisplay() {
		return this.sliderDisplay;
	}

	public JLabel getStepCounterLabel() {
		return this.stepCounterLabel;
	}

	public JLabel getSoluLabel() {
		return this.soluLabel;
	}

	public JSlider getSliderRandomizer() {
		return this.sliderRandomize;
	}

	public Checkbox getCBDisplay() {
		return this.cbDisplay;
	}

}