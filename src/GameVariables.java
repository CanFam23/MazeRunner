package src;

/**
 * <h1>GameVariables.java</h1>
 * 
 * <p>This interface is used to store constant variables that multiple classes need. 
 * Instead of storing a copy of each variable in each class, they can just implement this interface
 * and have access to all the variables.</p>
 * 
 * @author Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * @since March 5, 2024
 */
public interface GameVariables {
	/**
	 * Enumeration used for collision detection.
	 * 
	 * @see {@link ChunkManager#checkCollision()}
	 * @see {@link ChunkManager#containsPlayer(Chunk, PositionBlock)}
	 * @see {@link Chunk#checkCollision()}
	 * @see {@link Chunk#collision(PositionBlock)}
	 */
	enum Collision {
		/**Constant for the left side of the players hit box colliding.*/
		LEFT_SIDE, 
		/**Constant for the right side of the players hit box colliding.*/
		RIGHT_SIDE, 
		/**Constant for the top side of the players hit box colliding.*/
		TOP_SIDE, 
		/**Constant for the bottom side of the players hit box colliding.*/
		BOTTOM_SIDE, 
		/**Constant for the bottom left corner of the players hit box colliding.*/
		BOTTOM_LEFT_CORNER, 
		/**Constant for the top left corner of the players hit box colliding.*/
		TOP_LEFT_CORNER, 
		/**Constant for the top right corner of the players hit box colliding.*/
		TOP_RIGHT_CORNER,
		/**Constant for the bottom right corner of the players hit box colliding.*/
		BOTTOM_RIGHT_CORNER, 
		/**Constant for all four side of the players hit box colliding. Mainly used when detecting if player found the end.*/
		FULL_COLLISION, 
		/**Constant for no collision between the players hit box and another object.*/
		NO_COLLISION
	}

	/** Width of the screen.*/
	final int SCREEN_WIDTH = 1000;
	/** Height of the screen.*/
	final int SCREEN_HEIGHT = 800;

	/** 
	 * Width of the player. 
	 * @see {@link Player}
	 * */
	final int PLAYER_WIDTH = 50;
	
	/** 
	 * Height of the player. 
	 * @see {@link Player}
	 * */
	final int PLAYER_HEIGHT = 50;
	
	/** 
	 * X coordinate of the player. 
	 * @see {@link Player}
	 * */
	final int PLAYER_X = SCREEN_WIDTH / 2 - PLAYER_WIDTH/2;
	
	/** 
	 * Y coordinate of the player. 
	 * @see {@link Player}
	 * */
	final int PLAYER_Y = SCREEN_HEIGHT / 2 - PLAYER_HEIGHT/2;

	/** 
	 * Width of each PositionBlock.
	 * 
	 * @see {@link PositionBlock}
	 * */
	final int WALL_WIDTH = SCREEN_WIDTH / 5;
	
	/** 
	 * Height of each PositionBlock.
	 * 
	 * @see {@link PositionBlock}
	 * */
	final int WALL_HEIGHT = SCREEN_HEIGHT / 5;
	
	/** Player's x coordinates.*/
	final int[] playerXCoords = new int[] { PLAYER_X, PLAYER_X + PLAYER_WIDTH, PLAYER_X + PLAYER_WIDTH,
			PLAYER_X };
	/** Player's y coordinates.*/
	final int[] playerYCoords = new int[] { PLAYER_Y, PLAYER_Y, PLAYER_Y + PLAYER_HEIGHT,
			PLAYER_Y + PLAYER_HEIGHT };

}
