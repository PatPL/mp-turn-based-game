package Webserver;

public class Utility {
	
	/**
	 * @param length Length of the returned String
	 * @return Uppercase String of length [length], made up of 26 characters from latin alphabet
	 */
	private final static String latinAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static String getRandomString(int length) {
		StringBuilder output = new StringBuilder();
		
		for(int i = 0; i < length; ++i) {
			output.append(latinAlphabet.charAt((int) Math.floor(Math.random() * 26)));
		}
		
		return output.toString();
	}
	
}
