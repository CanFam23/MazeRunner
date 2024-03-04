package src;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class ChunkManager {
	// Declare attributes that are always the same
    static final String FILE_LOCATION = "~/../data/";
    private final int screenWidth = 1000;
	private final int screenHeight = 800;
	private final int WALL_WIDTH = screenWidth/5;
	private final int WALL_HEIGHT = screenHeight/5;
    
    // Declare attributes that change
    public String levelName = "";
    public int levelXDimension;
    public int levelYDimension;
    public int chunkXDimension;
    public int chunkYDimension;
    public Chunk[][] chunks;
    
    public boolean loadLevel(int levelNum) {
        levelName = "level_" + levelNum;
        try (final Scanner input = new Scanner(new File(FILE_LOCATION + levelName + ".txt"))) {
        	input.nextLine(); // Discard data description
        	final String[] levelStrings = input.nextLine().split(":")[1].split("x"); // Save the dimension of the chunks - example: (x chunks, y chunks)
        	levelXDimension = Integer.parseInt(levelStrings[0]);
        	levelYDimension = Integer.parseInt(levelStrings[1]);
        	final String[] chunkStrings = input.nextLine().split(":")[1].split("x"); // Save chunk dimensions - example: (x walls, y walls)
        	chunkXDimension = Integer.parseInt(chunkStrings[0]);
        	chunkYDimension = Integer.parseInt(chunkStrings[1]);
        	
        	// Initialize Chunks with correct chunk sizes.
        	chunks = new Chunk[levelYDimension][levelXDimension];
        	for (int y = 0; y < chunks.length; y++) {
        		for (int x = 0; x < chunks[y].length; x++) {
        			chunks[y][x] = new Chunk(chunkXDimension, chunkYDimension, x, y);
        		}
        	}
        	// Load the level data
        	int yPosition = 0;
        	int yChunk = 0;
        	int xChunk = 0;
        	PositionBlock pb;
        	while (input.hasNextLine()) {
        		yChunk = yPosition / chunkYDimension;
        		String[] inputData = input.nextLine().split("");
        		for (int xPosition = 0; xPosition < inputData.length; xPosition++) {
        			xChunk = xPosition / chunkXDimension;
        			pb = null;
        			if (inputData[xPosition].equals("0"))
        				pb = new EmptyBlock((xPosition % chunkXDimension) * WALL_WIDTH, (yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.white);
        			else if (inputData[xPosition].equals("1"))
        				pb = new Wall((xPosition % chunkXDimension) * WALL_WIDTH, (yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.black);
        			else if (inputData[xPosition].equals("2"))
        				pb = new StartingBlock((xPosition % chunkXDimension) * WALL_WIDTH, (yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.blue);
        			else if (inputData[xPosition].equals("3"))
        				pb = new EndBlock((xPosition % chunkXDimension) * WALL_WIDTH, (yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.green);
        			chunks[yChunk][xChunk].add(xPosition % chunkXDimension, yPosition % chunkYDimension, pb);
        		}
        		yPosition++;
        	}
        	
        	return true;
		} catch (FileNotFoundException e) {
			System.err.println("File: '" + FILE_LOCATION + levelName + ".txt" + "' not found");
			return false;
		} catch (Exception e) {
			System.err.println("An unexpected error occured while loading mazes");
			return false;
		}
    }
    
    public void updateCoords(int dx, int dy) {
    	for (int y = 0; y < chunks.length; y++) {
    		for (int x = 0; x < chunks[y].length; x++) {
    			chunks[y][x].updateCoords(dx, dy);
    		}
    	}
    }
    
    public void draw(Graphics2D g2d) {
    	for (int i = 0; i < chunks.length; i++) {
    		for (int j = 0; j < chunks[i].length; j++) {
    			chunks[i][j].draw(g2d);
    		}
    	}
    }
    
    public static void main(String[] args) {
        ChunkManager chunky = new ChunkManager();
        chunky.loadLevel(1);

        boolean all_passed = true;
        // Test that dimensions have been loaded correctly
        if (chunky.levelXDimension != 4) {
        	System.out.println("The X-level dimension was not 4");
        	all_passed = false;
        }
        if (chunky.chunkXDimension != 10) {
        	System.out.println("The X Chunk size was not 10");
        	all_passed = false;
        }
        if (all_passed == true) {
        	System.out.println("All cases passed! :)");
        } else {
        	System.out.println("At least one case failed! :(");
        }
    }
}