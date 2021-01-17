package Game.Utilities;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class PlaySound {
	
	public PlaySound() {
	}
	
	public static void playSound(Sounds sound) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(sound.AUDIO_INPUT_STREAM);
			if(clip.getFrameLength() > 1500) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
			else {
				sound.AUDIO_INPUT_STREAM.reset();
			}
			clip.start();
		}
		catch(Exception e) {
			System.out.println("Couldn't play sound");
			e.printStackTrace();
		}
	}
}
