package Webserver;

public class Utility {
	
	
	private final static String latinAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/**
	 * @param length Length of the returned String
	 * @return Uppercase String of length [length], made up of 26 characters from latin alphabet
	 */
	public static String getRandomString(int length) {
		StringBuilder output = new StringBuilder();
		
		for(int i = 0; i < length; ++i) {
			output.append(latinAlphabet.charAt((int) Math.floor(Math.random() * 26)));
		}
		
		return output.toString();
	}
	
	public static String getExtensionFromPath(String path) {
		String extension = path.substring(path.lastIndexOf('.'));
		// Special cases
		if(path.length() > 0 && path.charAt(0) == '.') {
			if(path.lastIndexOf('.') == 0) {
				// .htaccess
				extension = "";
			}
		}
		else {
			if(path.lastIndexOf('.') == -1) {
				// file
				extension = "";
			}
		}
		return extension;
	}
	
}
