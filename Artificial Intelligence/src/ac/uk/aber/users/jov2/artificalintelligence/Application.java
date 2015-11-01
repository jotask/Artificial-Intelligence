package ac.uk.aber.users.jov2.artificalintelligence;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Main class which hold the main method.
 * All the magic start here
 * 
 * @author Richard Jensen and Jose Vives
 *
 */
public class Application {

	private final static int WINDOW_WIDTH = 560;
	private final static int WINDOW_HEIGHT = 780;
	
	public Application() {
		JFrame applicationFrame = new JFrame( "8 Puzzle");

		// Kill application when window closes
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
	
	public static void main(String[] args) {
		new Application();
	}

}
