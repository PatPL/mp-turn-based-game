package common;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

public class Utility {
    
    private final static String latinAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static MessageDigest sha1md;
    static {
        MessageDigest tmp;
        try {
            tmp = MessageDigest.getInstance ("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            tmp = null;
            System.out.println ("Error - SHA-1 not found");
            e.printStackTrace ();
        }
        sha1md = tmp;
    }
    
    /**
     * @param length Length of the returned String
     * @return Uppercase String of length [length], made up of 26 characters from latin alphabet
     */
    public static String getRandomString (int length) {
        StringBuilder output = new StringBuilder ();
        
        for (int i = 0; i < length; ++i) {
            output.append (latinAlphabet.charAt ((int) Math.floor (Math.random () * 26)));
        }
        
        return output.toString ();
    }
    
    public static String getExtensionFromPath (String path) {
        String fileName = Path.of (path).getFileName ().toString ();
        int dotIndex = fileName.lastIndexOf ('.');
        String extension = fileName.substring (Math.max (dotIndex, 0));
        // Special cases
        if (fileName.length () > 0 && fileName.charAt (0) == '.') {
            if (fileName.lastIndexOf ('.') == 0) {
                // .htaccess
                extension = "";
            }
        } else {
            if (fileName.lastIndexOf ('.') == -1) {
                // file
                extension = "";
            }
        }
        return extension;
    }
    
    public static String leftPad (String input, String character, int size) {
        return Arrays.stream (input.split ("\n")).map (line -> character.repeat (size) + line).collect (Collectors.joining ("\n"));
    }
    
    public static String leftPad (String input, String character) {
        return leftPad (input, character, 1);
    }
    
    public static String readUntil (String input, String separator, int offset) {
        if (offset >= input.length ()) {
            return null;
        } else {
            return input.substring (offset).split (separator, -1)[0];
        }
    }
    
    public interface DocumentListenerHandler {
        void onChange (String newValue);
    }
    
    public static void applyDocumentListener (JTextField element, DocumentListenerHandler handler) {
        element.getDocument ().addDocumentListener (new DocumentListener () {
            @Override
            public void insertUpdate (DocumentEvent e) {
                handler.onChange (element.getText ());
            }
            
            @Override
            public void removeUpdate (DocumentEvent e) {
                handler.onChange (element.getText ());
            }
            
            @Override
            public void changedUpdate (DocumentEvent e) {
                handler.onChange (element.getText ());
            }
        });
        
        element.addFocusListener (new FocusListener () {
            @Override
            public void focusGained (FocusEvent e) {
                // Don't override selection behaviour, if the cause of focus gain is a mouse event.
                if (e.getCause ().ordinal () != 0) {
                    element.selectAll ();
                }
            }
            
            @Override
            public void focusLost (FocusEvent e) {
                handler.onChange (element.getText ());
            }
        });
    }
    
    public static String btoa (byte[] data) {
        return Base64.getEncoder ().encodeToString (data);
    }
    
    public static byte[] atob (String value) {
        return Base64.getDecoder ().decode (value);
    }
    
    /**
     * @param input String to be hashed
     * @return input hashed with SHA-1 in form of a base64 string
     */
    public static String sha1 (String input) {
        return btoa (sha1md.digest (input.getBytes ()));
    }
    
}
