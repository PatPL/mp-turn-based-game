package Game.Utilities;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public enum Sounds {
	buttonPress("buttonPress3.wav"),
	backgroundMusic("background.wav");
	
	public final AudioInputStream AUDIO_INPUT_STREAM;
	public final String PATH;
	
	Sounds(String path) {
		this.PATH = path;
		AudioInputStream tryAudioInputStream = null;
		try {
			tryAudioInputStream = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResource(path));
		}
		catch(Exception e) {
			System.out.println("Couldn't load sound file.");
			e.printStackTrace();
		}
		this.AUDIO_INPUT_STREAM = tryAudioInputStream;
	}
}
