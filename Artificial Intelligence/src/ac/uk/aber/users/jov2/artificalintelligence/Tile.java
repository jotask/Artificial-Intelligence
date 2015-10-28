package ac.uk.aber.users.jov2.artificalintelligence;
/*************************************
 * Tile-Sliding Puzzle
 *************************************
 * contact:  zhengyi.le@dartmouth.edu
 * Date: 1/22/2004
 * Modified by Richard Jensen, 2015
 *************************************
 */

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Tile extends JApplet implements ActionListener, ChangeListener {

	private static final long serialVersionUID = 780417925043043398L;
	//Some constants to be used elsewhere
	private final static int WINDOW_WIDTH = 560;
	private final static int WINDOW_HEIGHT = 780;
	
	final static int MIN_DELAY = 0;
	final static int MAX_DELAY = 3000;
	final static int INI_DELAY = 1500;
	
	final static int MIN_DIFFICULTY = 0;
	final static int MAX_DIFFICULTY = 100;
	final static int INI_DIFFICULTY = 20; //initial difficulty setting
	
	//determines the state of the GUI
	public final static int STOP = 0;
	public final static int RANDOMIZE = 1;
	public final static int IDLE = 2;
	public final static int START = 3;
	public final static int PLAY = 4;
	
	JButton stop = new JButton("Stop Search");
	JButton start = new JButton("Start Search");
	JButton play = new JButton("Play Solution");
	JButton randomize = new JButton("Mix it!");
	JSlider sliderDisplay = new JSlider(JSlider.HORIZONTAL, MIN_DELAY,
			MAX_DELAY, INI_DELAY);
	JSlider sliderRandomize = new JSlider(JSlider.HORIZONTAL, MIN_DIFFICULTY,
			MAX_DIFFICULTY, INI_DIFFICULTY);
	JLabel stepCounterLabel = new JLabel("<html>Nodes expanded: <br>" + "0" +
			"</html>");
	JLabel soluLabel = new JLabel(" ");

	//Checkboxes for selecting the algorithms
	CheckboxGroup cbg = new CheckboxGroup();
	Checkbox cbBfs = new Checkbox("Breadth First", cbg, true);
	Checkbox cbDfs = new Checkbox("Depth First", cbg, false);
	Checkbox cbIt = new Checkbox("Iterative Deepening", cbg, false);
	Checkbox cbAStarTiles = new Checkbox("A*-Tiles", cbg, false);
	Checkbox cbAStar = new Checkbox("A*-2", cbg, false);

	Checkbox cbDisplay = new Checkbox("Display Search");
	MyBoard board = new MyBoard(this); // this MyBoard b is used to display the graphic of the current borad.

	public void init() {
		getContentPane().setLayout(new BorderLayout());

		// Add the north Panel.
		Panel sliderDisplayPanel = new Panel();
		sliderDisplayPanel.setLayout(new GridLayout(3, 1));

		JLabel sliderDisplayLabel = new JLabel("Display Interval : ( ms )",
				JLabel.CENTER);
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

		// Add the  east Panel.
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

		JLabel sliderRandomizeLabel = new JLabel("Randomize the initial board:  (easy --> difficult)",
				JLabel.CENTER);
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

	//-------------------------------------------------------------
	// Listen to the actions of the buttons and the slider.
	//-------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == stop) {
			board.stopAlgorithm = true;
			board.setStatus(STOP);
		} else if (e.getSource() == randomize) {
			board.setStatus(RANDOMIZE);
		} else if (e.getSource() == start) {
			board.setStatus(START);
			stepCounterLabel.setText("<html>Nodes expanded: <br>" + "0" +
					"</html>");
			soluLabel.setText(" ");
		} else if (e.getSource() == play) {
			board.setStatus(PLAY);
		}
	}

	//-------------------------------------------------------
	// Reset the delay value of display from the slider
	//-------------------------------------------------------
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == sliderDisplay) {
			int value = sliderDisplay.getValue();
			board.setDelay(value);
		}
	}

	//--------------------------------------------------------
	// Create and run the application.
	//--------------------------------------------------------
	public static void main(String[] args) {
		JFrame applicationFrame = new JFrame(
				"8 Puzzle");

		// kill application when window closes
		applicationFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		Tile appletObject = new Tile();
		appletObject.init();

		applicationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		applicationFrame.getContentPane().add(appletObject);
		applicationFrame.pack();

		applicationFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		applicationFrame.setVisible(true);
	}
	
	public CheckboxGroup getCBG(){ return this.cbg; }
	public Checkbox getCBBFS(){ return this.cbBfs; }
	public Checkbox getCBDFS(){ return this.cbDfs; }
	public Checkbox getCBIT(){ return this.cbIt; }
	public Checkbox getAStart(){ return this.cbAStar; }
	public Checkbox getAstartTiles(){ return this.cbAStarTiles; }
	public JSlider getSliderDisplay(){ return this.sliderDisplay; }
	public JLabel getStepCounterLabel(){ return this.stepCounterLabel; }
	public JLabel getSoluLabel(){ return this.soluLabel; }
	public JSlider getSliderRandomizer(){ return this.sliderRandomize; }
	public Checkbox getCBDisplay(){ return this.cbDisplay; }
	
}