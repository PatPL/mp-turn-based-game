package common;

import common.enums.KeyEnum;

import java.util.prefs.Preferences;

public class Settings {
    
    private final static Preferences userPrefs = Preferences.userNodeForPackage (Settings.class);
    
    public static String getSetting (KeyEnum setting) {
        String output = userPrefs.get (setting.key, null);
        
        if (output == null) {
            setSetting (setting, setting.defaultValue);
            if (setting.defaultValue == null) {
                System.out.printf ("Warning: '%s' might not be a setting: default value is null.", setting.key);
            }
            return setting.defaultValue;
        }
        
        return output;
    }
    
    public static void setSetting (KeyEnum setting, String value) {
        userPrefs.put (setting.key, value);
        
        if (setting.onChange != null) {
            setting.onChange.invoke (value);
        }
    }
    
}
