package common.enums;

import common.HTTPClient;
import common.Settings;
import common.Utility;
import common.interfaces.IProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * Keys used in Preferences and HTTP headers
 * Keys used in Preferences have default values
 */
public enum KeyEnum {
    
    // Overall chances of an ID collision with 100000 concurrent players are <0.1% with ID of length 9
    // https://www.desmos.com/calculator/8w1bphnu6s
    // With ID of length 16, the overall odds of getting a collision are 1 in 7.5 trillion with 108000 players
    userID ("userID", Utility.getRandomString (16), newValue -> {
        // "userID" == KeyEnum.userID.key | but Java throws error "java: self-reference in initializer"
        HTTPClient.defaultHeaders.put ("userID", newValue);
    }),
    nickname ("nickname", "Player_" + Utility.getRandomString (6), newValue -> {
        // "nickname" == KeyEnum.nickname.key | but Java throws error "java: self-reference in initializer"
        HTTPClient.defaultHeaders.put ("nickname", newValue);
    }),
    serverAddress ("serverAddress", "127.0.0.1:1234", HTTPClient::setServerAddress);
    
    public final String key;
    public final String defaultValue;
    public final IProvider<String> onChange;
    
    KeyEnum (String key) {
        this (key, null);
    }
    
    KeyEnum (String key, String defaultValue) {
        this (key, null, null);
    }
    
    KeyEnum (String key, String defaultValue, IProvider<String> onChange) {
        if (KeyEnumUsedKeysWrapper.usedKeys.contains (key)) {
            this.key = null;
            this.defaultValue = null;
            this.onChange = null;
            System.out.printf ("Error in KeyEnum: duplicate key: %s", key);
            return;
        }
        this.key = key;
        this.defaultValue = defaultValue;
        this.onChange = onChange;
        if (onChange != null) {
            onChange.invoke (Settings.getSetting (this));
        }
    }
    
}

class KeyEnumUsedKeysWrapper {
    public final static Set<String> usedKeys = new HashSet<String> ();
}