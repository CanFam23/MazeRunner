/*
 * EndBlock.java
 * 
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * Date: March 2, 2024
 * 
 * Description:
 * EndBlock class represents the ending location of the maze
 * It extends the PositionBlock class.
 */
package src;

import java.awt.Color;

public class EndBlock extends PositionBlock {

	/**
	 * Constructor
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param c
	 */
	public EndBlock(int x, int y, int width, int height, Color c) {
		super(x, y, width, height, c);
	}

}
