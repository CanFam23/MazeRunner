/*
 * GameVariables.java
 * 
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * Date: March 5, 2024
 * 
 * Description
 * This interface is used to store constant variables that multiple classes need. 
 * Insetad of storing a copy of each variable in each class, they can just implement this interface
 * and have access to all the variables.
 */
package src;

public interface GameVariables {
	enum Collision {
		LEFT_SIDE, RIGHT_SIDE, TOP_SIDE, BOTTOM_SIDE, BOTTOM_LEFT_CORNER, TOP_LEFT_CORNER, TOP_RIGHT_CORNER,
		BOTTOM_RIGHT_CORNER, FULL_COLLISION, NO_COLLISION
	}

	final int SCREEN_WIDTH = 1000;
	final int SCREEN_HEIGHT = 800;

	final int PLAYER_X = SCREEN_WIDTH / 2;
	final int PLAYER_Y = SCREEN_HEIGHT / 2;
	final int PLAYER_WIDTH = 50;
	final int PLAYER_HEIGHT = 50;

	final int WALL_WIDTH = SCREEN_WIDTH / 5;
	final int WALL_HEIGHT = SCREEN_HEIGHT / 5;

}
