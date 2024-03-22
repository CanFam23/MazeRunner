/*
 * PositionBlock.java
 * 
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * Date: March 2, 2024
 * 
 * Description:
 * This class creates a individual block of the maze, which could be a wall, empty space, start block, or end block
 */
package src;

import java.awt.Color;
import java.awt.Graphics2D;

public class PositionBlock implements GameVariables {

	protected int x;
	protected int y;

	protected int width;
	protected int height;

	private Color c;

	/**
	 * Constructor
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param c
	 */
	public PositionBlock(int x, int y, int width, int height, Color c) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.width = width;
		this.height = height;
	}

	/**
	 * Updates block coords
	 * 
	 * @param newX
	 * @param newY
	 */
	public void updateCoords(int newX, int newY) {
		this.x += newX;
		this.y += newY;
	}

	/**
	 * @return the coords of the block
	 */
	public int[] getCoords() {
		return new int[] { x, y };
	}

	/**
	 * @return width of block
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return height of block
	 */
	public int getHeight() {
		return height;
	}

	/**
	 *@return string version of block
	 */
	public String toString() {
		if (this instanceof EmptyBlock)
			return "empt";
		else if (this instanceof Wall)
			return "wall";
		else if (this instanceof StartingBlock)
			return "strt";
		else if (this instanceof EndBlock) {
			return "EndB";
		} else
			return "????";
	}

	/**
	 * Draws block on g
	 * 
	 * @param g
	 * @param chunkXPosition
	 * @param chunkYPosition
	 */
	public void draw(Graphics2D g, int chunkXPosition, int chunkYPosition) {
		Color temp = g.getColor();

		g.setColor(c);
		g.fillRect(x + chunkXPosition, y + chunkYPosition, width, height);

		g.setColor(temp);
	}
	
	/**
	 * Tests methods of PositionBlock
	 * We made this a method so we could test all child classes
	 * Within this file too
	 * 
	 * @param initX
	 * @param initY
	 * @param width
	 * @param height
	 * @return if all tests were passed
	 */
	private boolean testMethods(int initX, int initY, int width, int height, String blockStr) {
		boolean allPassed = true;
		
		int[] initialCoords = getCoords();
		
		System.out.format("Testing PositionBlock of type %s.\n", toString());
		
		//Checking inital X coord is correct
		if(initialCoords[0] != initX) {
			System.err.format("Initial x should be %d, not %d!\n",initX,initialCoords[0]);
			allPassed = false;
		}
		
		//Checking inital Y coord is correct
		if(initialCoords[1] != initY) {
			System.err.format("Initial y should be %d, not %d!\n",initY,initialCoords[1]);
			allPassed = false;
		}
		
		//Checking for correct width
		if(getWidth() != width) {
			System.err.format("Width should be %d, not %d!\n",width,getWidth());
			allPassed = false;
		}
		
		//Checking for correct height
		if(getHeight() != height) {
			System.err.format("Height should be %d, not %d!\n",height,getHeight());
			allPassed = false;
		}
		
		//Checking for correct toString conversion
		if(!toString().equals(blockStr)) {
			System.err.format("toString should be %s, not %s!\n",blockStr,toString());
			allPassed = false;
		}
		
		
		if (allPassed) {
			return true;
		} else {
			return false;
		}
		
		
	}
	
	public static void main(String[] args) {
		boolean allPassed = true;
		
		final int initialX = 0;
		final int initialY = 0;
		
		//Testing a position block
		PositionBlock pb = new PositionBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, Color.black);
		
		allPassed = pb.testMethods(initialX, initialY,WALL_WIDTH,WALL_HEIGHT, "????");
		
		//Testing a empty block
		pb = new EmptyBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, Color.black);
		
		allPassed = pb.testMethods(initialX, initialY,WALL_WIDTH,WALL_HEIGHT, "empt");
		
		//Testing a wall
		pb = new Wall(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, Color.black);
		
		allPassed = pb.testMethods(initialX, initialY,WALL_WIDTH,WALL_HEIGHT,"wall");
		
		//Testing a starting block
		pb = new StartingBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, Color.black);
		
		allPassed = pb.testMethods(initialX, initialY,WALL_WIDTH,WALL_HEIGHT,"strt");
		
		//Testing a end block
		pb = new EndBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, Color.black);
		
		allPassed = pb.testMethods(initialX, initialY,WALL_WIDTH,WALL_HEIGHT,"EndB");
		
		
		if (allPassed) {
			System.out.println("All cases passed!");
		} else {
			System.err.println("At least 1 case failed!");
		}
	}
}
