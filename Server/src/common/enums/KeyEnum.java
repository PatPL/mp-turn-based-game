package common.enums;

/**
 * Keys used in Preferences and HTTP headers
 */
public enum KeyEnum {
    
    userID ("userID"),
    nickname ("nickname");
    
    public final String key;
    
    KeyEnum (String i) {
        this.key = i;
    }
    
}
