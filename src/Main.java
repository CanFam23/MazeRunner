package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * <h1>Main.java</h1>
 * 
 * <p>Serves as the main file for our game. This is the file you should run to play our game.</p>
 * 
 * @author Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * @since February 20, 2024
 * 
 * @see {@link JFrame}
 * @see {@link HomeScreen}
 * @see {@link GamePanel}
 */
public class Main {

	private static Timer timer;
	private static JFrame window;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				HomeScreen homeScreen = new HomeScreen();
				homeScreen.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						runMainCode();
					}
				});
				homeScreen.setVisible(true);
			}
		});
	}

	/**
	 * Creates window and starts game
	 */
	public static void runMainCode() {
		// Creates window
		window = new JFrame();

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Maze Runner - Use Arrows to start time");

		// window.setResizable(false);
		// Creates new game panel object
		GamePanel gamePanel = new GamePanel();
		window.add(gamePanel);

		// Timer to track seconds
		timer = new Timer(1000, new ActionListener() {
			int seconds = 0;
			int seconds_left = 0;

			/**
			 * Made to keep track of how much time is left in the game.
			 * 
			 * @param e ActionEvent to process.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				seconds++;
				seconds_left = 120 - seconds;

				if (seconds_left <= 0) {
					timer.stop();
					GameOverLOSE.GameOver();
					closeMainWindow();
				}

				window.setTitle("Maze Runner - Time Left: " + seconds_left + " seconds");

			}
		});

		gamePanel.addKeyListener(new KeyAdapter() {
			private boolean timerStarted = false;

			/**
			 * Made to check for key press. When a key is pressed, the timer starts.
			 * 
			 * @param e KeyEvent to process
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				// Start the timer only if it hasn't been started yet and arrow keys are pressed
				if (!timerStarted && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) {
					timerStarted = true;
					timer.start();
				}
			}
		});

		// Make sure the panel can receive focus
		gamePanel.setFocusable(true);
		gamePanel.requestFocusInWindow();

		// window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// Add window listener to handle window closing event
		window.addWindowListener(new WindowAdapter() {
			/**
			 * Used to handle window closing event.
			 * 
			 * @param e WindowEvent to process.
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Average FPS: " + gamePanel.getFPS());
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.dispose(); // Close the window

			}
		});

		// starts game
		gamePanel.startGameThread();
	}

	/**
	 * Closes window
	 */
	public static void closeMainWindow() {
		window.dispose();
	}
}
