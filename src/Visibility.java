package src;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.GeneralPath;
import java.util.TimerTask;
import java.util.Timer;

/**
 * <h1>Visibility.java</h1>
 * 
 * <p>
 * This class handles drawing the visibility of the player in our game. The
 * player has a circle around themselves which is what they can see. As time
 * goes on, the circle gets smaller, making it harder for the player to see the
 * maze.
 * </p>
 * 
 * @author Nick Clouse
 * 
 * @since March 25, 2024
 * 
 * @see {@link GameVariables}
 */
public class Visibility implements GameVariables {

	/** Center x of the screen. */
	private final int centerX = SCREEN_WIDTH / 2;

	/** Center y of the screen. */
	private final int centerY = SCREEN_HEIGHT / 2;

	/** Starting radius of the circle. */
	private final int startingRadius = 500;

	/** The minimum radius the circle is allowed to reach. */
	private final int minRadius = 175;

	/** Amount to decrease the radius by. */
	private final int decreaseAmount = 1;

	/** Delay for the timer in milliseconds. */
	private final int delay = startingRadius - minRadius;

	/**
	 * New 'blank' color that is transparent. Used to make the gradient go from
	 * black to transparent.
	 */
	private final Color c = new Color(0, 0, 0, 0);

	/**
	 * New float[], used to determine the distribution of colors along the gradient.
	 */
	private final float[] dist = { 0.2f, 1.0f };
	/** Array of colors to use in the gradient. */
	private final Color[] colors = { c, Color.BLACK };

	/** radius that will be updated and used to draw the circle. */
	private int radius = startingRadius;
	/** Timer used to update the circle radius. */
	private Timer timer;

	/**
	 * Inner class that extends Timer Task. Creates a new TimerTask to update the
	 * radius of the players view.
	 */
	private class updateRadius extends TimerTask {
		public void run() {
			if (radius > minRadius) {
				radius -= decreaseAmount;
			} else {
				stopTimer();
			}
		}
	}

	/**
	 * Constructs a new Visibility object.
	 */
	public Visibility() {
		timer = new Timer();
	}

	/**
	 * Starts the timer.
	 */
	public void startTimer() {
		timer.schedule(new updateRadius(), 1000, delay);
	}

	/**
	 * Stops the timer.
	 */
	public void stopTimer() {
		timer.cancel();
	}

	/**
	 * Resets and restarts the timer.
	 */
	public void restartTimer() {
		radius = startingRadius;
		timer = new Timer();
		timer.schedule(new updateRadius(), delay);
	}

	/**
	 * Draws the visibility circle around the player. Uses GeneralPath objects to
	 * make it look like there is a circle around the player.
	 * 
	 * @param g2d the graphics to draw on.
	 */
	public void drawVision(Graphics2D g2d) {
		final RadialGradientPaint p = new RadialGradientPaint(centerX, centerY, radius * 2, dist, colors);

		// Drawing the right side
		final GeneralPath rightSide = new GeneralPath();
		// Move to the starting point (top point)
		rightSide.moveTo(centerX, centerY - radius);

		// First quadrant (top-right)
		rightSide.quadTo(centerX + radius, centerY - radius, centerX + radius, centerY);

		// Second quadrant (top-left)
		rightSide.quadTo(centerX + radius, centerY + radius, centerX, centerY + radius);

		// Bottom connecting line
		rightSide.lineTo(centerX, SCREEN_HEIGHT);

		// Bottom line
		rightSide.lineTo(SCREEN_WIDTH, SCREEN_HEIGHT);

		// back line
		rightSide.lineTo(SCREEN_WIDTH, 0);

		// Top line
		rightSide.lineTo(centerX, 0);

		// Top connecting line
		rightSide.lineTo(centerX, centerY - radius);

		rightSide.closePath();

		// Left side general path
		final GeneralPath leftSide = new GeneralPath();

		// Move to the starting point (top point)
		leftSide.moveTo(centerX, centerY - radius);

		// Second quadrant (top-left)
		leftSide.quadTo(centerX - radius, centerY - radius, centerX - radius, centerY);

		// Third quadrant (bottom-left)
		leftSide.quadTo(centerX - radius, centerY + radius, centerX, centerY + radius);

		// Bottom connecting line
		leftSide.lineTo(centerX, SCREEN_HEIGHT);

		// Bottom line
		leftSide.lineTo(0, SCREEN_HEIGHT);

		// back line
		leftSide.lineTo(0, 0);

		// Top line
		leftSide.lineTo(centerX, 0);

		// Top connecting line
		leftSide.lineTo(centerX, centerY - radius);

		leftSide.closePath();

		g2d.setPaint(p);
		g2d.fill(rightSide);
		g2d.fill(leftSide);
	}

	public static void main(String[] args) {
		// TODO add testing
	}

}
