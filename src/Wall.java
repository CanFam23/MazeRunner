/*
 * Wall.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: February 20, 2024
 * 
 * Desc:
 * 'TBD'
 */
package src;


import java.awt.Color;

public class Wall extends PositionBlock{
	
	public Wall(int x, int y, int width, int height, Color c){
		super(x, y, width, height, c);
	}
	
	//Returns the top left coordinates of the wall
	public int[] getCoords() {
		return new int[] {x,y};
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
