package src;

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

public class Wall extends PositionBlock{
	
	private static final int HITBOX_BUFFER_AMOUNT = 6;
	
	public Wall(int x, int y, int width, int height, Color c){
		super(x, y, width, height, c);
	}
	
	//'Hitbox' coords, the dimensions of the square
	public int[][] getHitboxCoords(){
							//The order is: top left, top right, bottom right, bottom left
		return new int[][] {{x-HITBOX_BUFFER_AMOUNT,x+width+HITBOX_BUFFER_AMOUNT,x+width+HITBOX_BUFFER_AMOUNT,x-HITBOX_BUFFER_AMOUNT},{y-HITBOX_BUFFER_AMOUNT,y-HITBOX_BUFFER_AMOUNT,y+height+HITBOX_BUFFER_AMOUNT,y+height+HITBOX_BUFFER_AMOUNT}};
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
