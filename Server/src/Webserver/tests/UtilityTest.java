package Webserver.tests;

import Webserver.Utility;
import org.junit.Assert;

public class UtilityTest {
	
	@org.junit.Test
	public void getRandomStringTest() {
		final int testCount = 100;
		for(int i = 0; i < testCount; ++i) {
			String string = Utility.getRandomString(32);
			boolean match = string.matches("^[A-Z]{32}$");
			assert (match);
		}
	}
	
	@org.junit.Test
	public void getExtensionFromPathTest() {
		Assert.assertEquals("", Utility.getExtensionFromPath(".gitignore"));
		Assert.assertEquals("", Utility.getExtensionFromPath("C:/folder/aaaaa/.gitignore"));
		Assert.assertEquals("", Utility.getExtensionFromPath("../hfg/.gitignore"));
		
		Assert.assertEquals(".txt", Utility.getExtensionFromPath(".aaaa.txt"));
		Assert.assertEquals(".txt", Utility.getExtensionFromPath("C:/folder/aaaaa/.aaaa.txt"));
		Assert.assertEquals(".txt", Utility.getExtensionFromPath("../hfg/.aaaa.txt"));
		
		Assert.assertEquals("", Utility.getExtensionFromPath("file"));
		Assert.assertEquals("", Utility.getExtensionFromPath("C:/folder/aaaaa/file"));
		Assert.assertEquals("", Utility.getExtensionFromPath("../hfg/file"));
		
		Assert.assertEquals(".html", Utility.getExtensionFromPath("index.html"));
		Assert.assertEquals(".html", Utility.getExtensionFromPath("C:/folder/aaaaa/index.html"));
		Assert.assertEquals(".html", Utility.getExtensionFromPath("../hfg/index.html"));
	}
	
}