/*
 * KeyHandler.java
 * 
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * Date: Febuary 20, 2024
 * 
 * Desc:
 * This program handles key events
 */
package src;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed, pPressed;

	/**
	 * Checks for key presses
	 * 
	 *@param e the KeyEvent to use
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
		// Used to pause/unpause game
		if (code == KeyEvent.VK_P) {
			if (pPressed == true) {
				pPressed = false;
			} else if (pPressed == false) {
				pPressed = true;
			}

		}

	}

	@Override
	/**
	 * Checks for key releases
	 * 
	 *@param e the KeyEvent to use
	 */
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

	}

	@Override
	/**
	 * Not used
	 */
	public void keyTyped(KeyEvent e) {
	}

}
