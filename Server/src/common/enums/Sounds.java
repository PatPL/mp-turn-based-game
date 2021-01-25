package common.enums;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;

public enum Sounds {
    buttonPress ("buttonPress.wav"),
    silence ("silence.wav");
    
    public final AudioInputStream AUDIO_INPUT_STREAM;
    public final String PATH;
    
    public AudioInputStream getAudioInputStream () {
        AudioInputStream tryAudioInputStream = null;
        try {
            tryAudioInputStream = AudioSystem.getAudioInputStream (new BufferedInputStream (getClass ().getClassLoader ().getResourceAsStream (PATH)));
        } catch (Exception e) {
            System.out.println ("Couldn't load sound file.");
            e.printStackTrace ();
        }
        return tryAudioInputStream;
    }
    
    Sounds (String path) {
        this.PATH = path;
        this.AUDIO_INPUT_STREAM = getAudioInputStream ();
    }
}
