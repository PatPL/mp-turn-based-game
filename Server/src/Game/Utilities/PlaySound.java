package Game.Utilities;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class PlaySound {
	// Play a given sound on repeat
	public static Clip repeatSound(Sounds sound) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(sound.getAudioInputStream());
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
			return clip;
		}
		catch(Exception e) {
			System.out.println("Couldn't play sound");
			e.printStackTrace();
			return null;
		}
	}
	
	// Play a given sound once
	public static void playSound(Sounds sound) {
		try {
			Clip clip = AudioSystem.getClip();
			sound.AUDIO_INPUT_STREAM.reset();
			clip.open(sound.AUDIO_INPUT_STREAM);
			clip.start();
		}
		catch(Exception e) {
			System.out.println("Couldn't play sound");
			e.printStackTrace();
		}
	}
}
