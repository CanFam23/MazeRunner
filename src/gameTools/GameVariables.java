
package gameTools;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This interface is used to store constant variables that multiple classes
 * need. Instead of storing a copy of each variable in each class, they can just
 * implement this interface and have access to all the variables.
 * </p>
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since March 5, 2024
 */
public interface GameVariables {
	/**
	 * Enumeration used for collision detection.
	 * 
	 * @see ChunkManager#checkCollision()
	 * @see ChunkManager#containsPlayer(Chunk, PositionBlock)
	 * @see Chunk#checkCollision()
	 * @see Chunk#collision(PositionBlock)
	 */
	enum Collision {
		/** Constant for the left side of the players hit box colliding. */
		LEFT_SIDE,
		/** Constant for the right side of the players hit box colliding. */
		RIGHT_SIDE,
		/** Constant for the top side of the players hit box colliding. */
		TOP_SIDE,
		/** Constant for the bottom side of the players hit box colliding. */
		BOTTOM_SIDE,
		/** Constant for the bottom left corner of the players hit box colliding. */
		BOTTOM_LEFT_CORNER,
		/** Constant for the top left corner of the players hit box colliding. */
		TOP_LEFT_CORNER,
		/** Constant for the top right corner of the players hit box colliding. */
		TOP_RIGHT_CORNER,
		/** Constant for the bottom right corner of the players hit box colliding. */
		BOTTOM_RIGHT_CORNER,
		/**
		 * Constant for all four side of the players hit box colliding. Mainly used when
		 * detecting if player found the end.
		 */
		FULL_COLLISION,
		/** Constant for no collision between the players hit box and another object. */
		NO_COLLISION
	}
	
	/**
	 * Sprite States.
	 */
	public enum State {
		/** Constant for when sprite is idle. */
		Idle,
		/** Constant for when sprite is moving. */
		Move,
		/** When the sprite is attacking. */
		Attack,
		/** When the sprite is dead. */
		Dead;
	}

	/**
	 * The direction the sprite is currently facing.
	 */
	public enum Facing {
		/** Constant for the sprite facing south. */
		S,
		/** Constant for the sprite facing southeast. */
		SE,
		/** Constant for the sprite facing east. */
		E,
		/** Constant for the sprite facing northeast. */
		NE,
		/** Constant for the sprite facing north. */
		N,
		/** Constant for the sprite facing northwest. */
		NW,
		/** Constant for the sprite facing west. */
		W,
		/** Constant for the sprite facing southwest. */
		SW
	}
	
	@SuppressWarnings("serial")
	final Map<Facing,Facing> oppositeDirection = new HashMap<Facing,Facing>() {{
        put(Facing.N, Facing.S);
        put(Facing.S, Facing.N);
        put(Facing.E, Facing.W);
        put(Facing.W, Facing.E);
        put(Facing.NW, Facing.SE);
        put(Facing.NE, Facing.SW);
        put(Facing.SE, Facing.NW);
        put(Facing.SW, Facing.NE);
    }};

	/** Width of the screen. */
	final int SCREEN_WIDTH = 1000;
	/** Height of the screen. */
	final int SCREEN_HEIGHT = 800;

	/**
	 * Width of the player.
	 * 
	 * @see Player
	 */
	final int PLAYER_WIDTH = 50;

	/**
	 * Height of the player.
	 * 
	 * @see Player
	 */
	final int PLAYER_HEIGHT = 50;

	/**
	 * X coordinate of the player.
	 * 
	 * @see Player
	 */
	final int PLAYER_X = SCREEN_WIDTH / 2 - PLAYER_WIDTH / 2;

	/**
	 * Y coordinate of the player.
	 * 
	 * @see Player
	 */
	final int PLAYER_Y = SCREEN_HEIGHT / 2 - PLAYER_HEIGHT / 2;

	/**
	 * Width of each PositionBlock.
	 *
	 * @see PositionBlock
	 */
	final int WALL_WIDTH = SCREEN_WIDTH / 5;

	/**
	 * Height of each PositionBlock.
	 * 
	 * @see PositionBlock
	 */
	final int WALL_HEIGHT = SCREEN_HEIGHT / 5;

	/** Player's x coordinates. */
	final int[] playerXCoords = new int[] { PLAYER_X, PLAYER_X + PLAYER_WIDTH, PLAYER_X + PLAYER_WIDTH, PLAYER_X };
	/** Player's y coordinates. */
	final int[] playerYCoords = new int[] { PLAYER_Y, PLAYER_Y, PLAYER_Y + PLAYER_HEIGHT, PLAYER_Y + PLAYER_HEIGHT };
	

	/**
	 * Used to check if a full collision is occurring, when all four sides of the
	 * object are overlapping by this amount or less.
	 */
	final int FULL_COLLISION_INT = 75;
	
	/**
	 * Used to check if the collision overlap is close enough for collision, mainly
	 * multiple sides colliding at once.
	 */
	final int COLLISION_INT = 5;
	
	/** Used to make the block hitboxes slightly bigger then their dimensions. */
	final int HITBOX_BUFFER_AMOUNT = 6; // TODO How close is close enough to register a collision?

}
