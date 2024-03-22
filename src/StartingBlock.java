/*
 * StartingBlock.java
 * 
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * Date: March 2, 2024
 * 
 * Description:
 * StartingBlock class represents the starting block of the player
 * It extends the PositionBlock class.
 * 
 */
package src;

import java.awt.Color;

public class StartingBlock extends PositionBlock {

	/**
	 * Constructor
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param c
	 */
	public StartingBlock(int x, int y, int width, int height, Color c) {
		super(x, y, width, height, c);
	}

}
