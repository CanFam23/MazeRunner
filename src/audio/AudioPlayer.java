package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	
	public static int MENU_1 = 0;
	public static int LEVEL_1 = 1;
	public static int LEVEL_2 = 2;

	public static Clip DIE;
	public static int JUMP = 1;
	public static int GAMEOVER = 2;
	public static int LVL_COMPLETED = 3;
	public static int ATTACK_ONE = 4;
	public static int ATTACK_TWO = 5;
	public static int ATTACK_THREE = 6;
	
	private Clip[] songs, effects;
	private int currentSongId;
	private boolean songMute, effectMute;
	private Random rand = new Random();
	
    private Clip clip;

    private boolean isPlaying = false;
	
    public AudioPlayer() {

    }

    private Clip getClip(String name) {
        URL url = getClass().getResource(name + ".wav");
        AudioInputStream audio;

        try {
            audio = AudioSystem.getAudioInputStream(url);
            Clip c = AudioSystem.getClip();

            c.open(audio);
            return c;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        	System.out.print("Couldn't load");
            e.printStackTrace();
        }

        return null;
    }



    public void playSong(String fileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(fileName));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the clip continuously
            isPlaying = true;
            System.out.println("Playing song: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean isActive() {
        return isPlaying;
    }

    public void playSongOnce(String fileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(fileName));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start(); // Play the clip once
            isPlaying = true;
            System.out.println("Playing song: " + fileName);
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        isPlaying = false;
                        System.out.println("Song playback finished: " + fileName);
                    }
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            isPlaying = false;
        }
    }
	
   public static void main(String[] args) throws InterruptedException {
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.playSong("levelPlay.wav");
        Thread.sleep(100000000); // Play for 10 seconds
////        audioPlayer.stop();

   }
}
