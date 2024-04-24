package gameTools;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * <p>
 * Handles key events.
 * </p>
 *
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 *
 * @since Febuary 20, 2024
 */
public class KeyHandler implements KeyListener {

	/**
	 * Boolean used to check if corresponding key is being pressed or is released.
	 */
	public boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

	/**
	 * Checks for key presses.
	 *
	 * @param e the KeyEvent to process.
	 */
	@Override
	public void keyPressed(KeyEvent e) {

		int code = e.getKeyCode();

		// If any of these keys are pressed, the corresponding boolean is set to true
		if (code == KeyEvent.VK_RIGHT) {
			rightPressed = true;
		}
		if (code == KeyEvent.VK_LEFT) {
			leftPressed = true;
		}
		if (code == KeyEvent.VK_UP) {
			upPressed = true;
		}
		if (code == KeyEvent.VK_DOWN) {
			downPressed = true;
		}
		if (code == KeyEvent.VK_SPACE) {
			spacePressed = true;
		}
	}

	/**
	 * Checks for key releases.
	 *
	 * @param e the KeyEvent to process.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		// If any of these keys are released, the corresponding boolean is set to false
		if (code == KeyEvent.VK_RIGHT) {
			rightPressed = false;
		}
		if (code == KeyEvent.VK_LEFT) {
			leftPressed = false;
		}
		if (code == KeyEvent.VK_UP) {
			upPressed = false;
		}
		if (code == KeyEvent.VK_DOWN) {
			downPressed = false;
		}
		if (code == KeyEvent.VK_SPACE) {
			spacePressed = false;
		}
	}

	/**
	 * Not used.
	 *
	 * @param e the KeyEvent to process.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Main method, used for testing.
	 * 
	 * @param args Arguments passed.
	 */
	public static void main(String[] args) {
		// Create an instance of KeyHandler
		final KeyHandler keyHandler = new KeyHandler();

		// Create a JFrame to listen for key events
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);

		// Create a JLabel to display key status
		final JLabel statusLabel = new JLabel("Keys Pressed: ");
		statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		frame.add(statusLabel, BorderLayout.CENTER);

		// Create a JLabel to tell user to press a key
		final JLabel headLabel = new JLabel("Press a key");
		headLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		frame.add(headLabel, BorderLayout.NORTH);

		// Add the KeyHandler as a KeyListener to the JFrame
		frame.addKeyListener(keyHandler);

		// Create a Timer to update the status of keys in the JLabel
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> {
					String text = "Keys Pressed: ";
					if (keyHandler.upPressed) {
						text += "UP ";
					}
					if (keyHandler.downPressed) {
						text += "DOWN ";
					}
					if (keyHandler.leftPressed) {
						text += "LEFT ";
					}
					if (keyHandler.rightPressed) {
						text += "RIGHT ";
					}
					if (keyHandler.spacePressed) {
						text += "SPACE ";
					}
					statusLabel.setText(text);
				});
			}
		}, 0, 100); // 100 milliseconds

		// Center the JFrame on the screen
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
