/*
 * KeyHandler.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: Febuary 20, 2024
 * 
 * Desc:
 * 'TBD'
 */
package src;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed, pPressed;

	@Override
	// Checks for key presses
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
	// Checks for key releases
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
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

}