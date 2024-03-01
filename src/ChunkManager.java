import java.io.File;

public class ChunkManager {
    static final String FILE_LOCATION = "data/";
    public String levelName = "";
    
    public void loadLevel(int levelNum) {
        levelName = "level_" + levelNum;
        File levelFile = new File(FILE_LOCATION + levelName);
        
    }

    public static void main(String[] args) {
        ChunkManager chunky = new ChunkManager();
        chunky.loadLevel(1);
        System.out.println(chunky.levelName);
        
    }
}