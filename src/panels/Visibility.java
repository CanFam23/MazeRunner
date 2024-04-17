package panels;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gameTools.GameVariables;

/**
 * <p>
 * This class handles drawing the visibility of the player in our game. The
 * player has a circle around themselves which is what they can see. As time
 * goes on, the circle gets smaller, making it harder for the player to see the
 * maze.
 * </p>
 *
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 *
 * @since March 25, 2024
 *
 * @see GameVariables
 */
public class Visibility implements GameVariables {

	/**
	 * The current instance of Visibility.
	 */
	private static Visibility single_instance = null;

	/** Center x of the screen. */
	private final int centerX = SCREEN_WIDTH / 2;

	/** Center y of the screen. */
	private final int centerY = SCREEN_HEIGHT / 2;

	/** Starting radius of the circle. */
	private final int startingRadius = 400;

	/** Amount to decrease the radius by. */
	private final int decreaseAmount = 100;

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

	/**
	 * Stores the right side of the visibility shape.
	 * They are split into two sides so they are easier to draw.
	 * When they come together, they form a circle in the middle.
	 */
	private GeneralPath rightSide = new GeneralPath();

	/**
	 * Stores the right side of the visibility shape.
	 * They are split into two sides so they are easier to draw.
	 * When they come together, they form a circle in the middle.
	 */
	private GeneralPath leftSide = new GeneralPath();

	/**
	 * Color used for drawing the visibility.
	 * It's a gradient that goes from black to transparent.
	 */
	private RadialGradientPaint p;

	/**
	 * Constructs a new Visibility object.
	 */
	private Visibility() {
		reset();
	}

	/**
	 * Makes a new instance of Visibility. Visibility is a singleton, which means
	 * only one instance of Visibility can exist at a time. Visibility is a
	 * singleton because we only need one instance of it for our game, and don't
	 * want multiple instances to be made.
	 *
	 * @return The current instance of Visibility.
	 */
	public static synchronized Visibility getInstance() {
		if (single_instance == null) {
			single_instance = new Visibility();
		}

		return single_instance;
	}

	/**
	 * Decreases the radius by preset amount.
	 */
	public void updateRadius() {
		radius -= decreaseAmount;
		if(radius <= 0) {
			radius = 400;
		}
	}

	/**
	 * Resets radius, updates radius of visibility to current value of radius.
	 */
	public void reset() {
		// Drawing the right side
		rightSide = new GeneralPath();
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
		leftSide = new GeneralPath();

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

		//Update the gradient to match the size of the visibility circle
		p = new RadialGradientPaint(centerX, centerY, radius * 2, dist, colors);
	}

	/**
	 * Draws the visibility circle around the player. Uses GeneralPath objects to
	 * make it look like there is a circle around the player.
	 *
	 * @param g2d the graphics to draw on.
	 */
	public void drawVision(Graphics2D g2d) {
		g2d.setPaint(p);
		g2d.fill(rightSide);
		g2d.fill(leftSide);
	}

	/**
	 * Main method, used for testing.
	 *
	 * @param args Arguments passed.
	 */
	public static void main(String[] args) {
        // Create a new JFrame
        JFrame frame = new JFrame("Visibility Test");
        frame.setSize(GameVariables.SCREEN_WIDTH, GameVariables.SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create an instance of Visibility
        Visibility visibility = Visibility.getInstance();

        // Create a JPanel to draw the visibility
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = -2350091849192585086L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Draw the visibility circle
                visibility.drawVision(g2d);
                
                // Draw text in the center of screen
                String text = "Press Spacebar to Decrease Visibility";
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(text)) / 2;
                int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                g.setColor(Color.black);
                g2d.drawString(text, x, y);
            }
        };

        // Add KeyListener to the JPanel
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.addKeyListener(new KeyListener() {
            /**
             * Not used.
             *
             *@param e KeyEvent to use.
             */
            @Override
            public void keyTyped(KeyEvent e) {}

            /**
             * If space bar is pressed, update radius.
             *
             *@param e KeyEvent to use.
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    visibility.updateRadius();
                    visibility.reset();
                    panel.repaint();
                }
            }

            /**
             * Not used.
             *
             *@param e KeyEvent to use.
             */
            @Override
            public void keyReleased(KeyEvent e) {}
        });

        frame.add(panel);

        // Set up the JFrame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
