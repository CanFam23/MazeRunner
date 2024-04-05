package src;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>
 * This class handles key events. Implements KeyListener
 * </p>
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since Febuary 20, 2024
 * 
 * @see KeyListener
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

}
