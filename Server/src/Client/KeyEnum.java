package Client;

/**
 * Keys used in Preferences and HTTP headers
 */
public enum KeyEnum {
	
	userID("userID");
	
	public final String key;
	
	KeyEnum(String i) {
		this.key = i;
	}
	
}
