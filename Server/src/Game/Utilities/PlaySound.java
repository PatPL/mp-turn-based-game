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
			System.out.println(clip.getFrameLength());
			
			//Only loop music in background
			if(clip.getFrameLength() > 200000) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
			clip.start();
			if(clip.getFrameLength() < 1500) sound.AUDIO_INPUT_STREAM.reset();
		}
		catch(Exception e) {
			System.out.println("Couldn't play sound");
			e.printStackTrace();
		}
	}
}
