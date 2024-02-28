package wallTesting;

/*
 * Wall generation:
 * Use a 2D array, each spot has a int if the spot contains a wall
 * Preload map from a file, use 1's and 0's, 1 = wall, 0 = empty space
 * 
 * Split the maze into chunks
 * If the user enters a new chunk, render that chunk and maybe the ones around it
 * 		- Each chunk could be 1/4 of the screen size, or some ratio
 * 		- Efficient and allows us to only load certain parts at a time
 * 
 * Ideas for classes:
 * 		- Wall class
 * 			* Stores its own coordinates
 *			* Stores Width and height, image or color
 * 		- Chunk class
 * 			* Keeps track of a group of walls, updates their coords
 * 			* All walls are relative to the top left wall when creating them?
 * 		- Chunk manager
 * 			* Handles chunk creating, deciding which to render, and renders them
 * 			* Handles collision detection, or should the chunk class?
 * 			* Chunks numbered 1-x, keep track of which one user is in at all times?
 * 
 * 		- Player class
 * 			* coordinates, width and height, image
 * 			* Don't need updating bc we don't actually move the player, we move the walls around to make it look like its moving
 * 			* What else? 
 * 
 * 		- Timer class?
 * 
 * 		- Do we need a class for the mini map that'll be shown?
 * 		
 *		- Class for dealing with loading the home screen?
 *		
 *		** Stretch feature classes, stuff we want to add if we have time, should we make thse now?:
 *
 *		* Obstacles
 *		
 *		* Enemies
 *
 *		* Weapons
 *
 *		* Currency
 */

import java.awt.Color;
import java.awt.Graphics2D;

public class Wall {
	
	private static final int SCREEN_HEIGHT = 800;
	private static final int SCREEN_WIDTH = 1000;
	
	
	private int x;
	private int y;
	
	private int width;
	private int height;
	
	private Color c;
	
	public Wall(int x, int y,int width, int height, Color c){
		this.x = x;
		this.y = y;
		this.c = c;
		this.width = width;
		this.height = height;
		
	}
	
	public void updateCoords(int newX, int newY) {
		this.x += newX;
		this.y += newY;
	}
	
	public int[] getCoords() {
		return new int[]{x,y};
	}
	
	//'Hitbox' coords, the dimensions of the square
	public int[][] getHitboxCoords(){
							//The order is: top left, top right, bottom right, bottom left
		return new int[][] {{x,x+width,x+width,x,},{y,y,y+height,y+height}};
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	//Returns true if the wall is within the screen dimensions
	public boolean isVisible() {
					//Change this 10 to -width if you want the walls to disppear once they are off the screen
		return (this.x > 10 && this.x < SCREEN_WIDTH+width) && (this.y > -height && this.y < SCREEN_HEIGHT+height);
	}
	
	public void draw(Graphics2D g) {
		Color temp = g.getColor();
		
		g.setColor(c);
		g.fillRect(x, y, width, height);
		
		g.setColor(temp);
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
