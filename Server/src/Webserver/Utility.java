package Webserver;

import java.nio.file.Path;

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
		String fileName = Path.of(path).getFileName().toString();
		int dotIndex = fileName.lastIndexOf('.');
		String extension = fileName.substring(Math.max(dotIndex, 0));
		// Special cases
		if(fileName.length() > 0 && fileName.charAt(0) == '.') {
			if(fileName.lastIndexOf('.') == 0) {
				// .htaccess
				extension = "";
			}
		}
		else {
			if(fileName.lastIndexOf('.') == -1) {
				// file
				extension = "";
			}
		}
		return extension;
	}
	
}
