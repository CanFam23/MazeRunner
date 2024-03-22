/*
 * Wall.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: February 20, 2024
 * 
 * Desc:
 * Wall class represents a wall in the maze
 * It extends the PositionBlock class.
 */
package src;

import java.awt.Color;

public class Wall extends PositionBlock {

	/**
	 * Constructor
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param c
	 */
	public Wall(int x, int y, int width, int height, Color c) {
		super(x, y, width, height, c);
	}
}
