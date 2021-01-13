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
	
	@org.junit.Test
	public void leftPadTest() {
		String a = String.join("\r\n",
			"abc"
		);
		String aValid = String.join("\r\n",
			"012012012012abc"
		);
		
		String b = String.join("\r\n",
			"Example text",
			"multiline text",
			"",
			"above me is an empty line"
		);
		String bValid = String.join("\r\n",
			"  Example text",
			"  multiline text",
			"  ",
			"  above me is an empty line"
		);
		
		String c = String.join("\r\n",
			"a",
			"b",
			"c"
		);
		String cValid = String.join("\r\n",
			"\ta",
			"\tb",
			"\tc"
		);
		
		String d = String.join("\r\n",
			"123",
			"",
			"",
			"   ",
			"456",
			".",
			"333"
		);
		String dValid = String.join("\r\n",
			"........123",
			"........",
			"........",
			"........   ",
			"........456",
			".........",
			"........333"
		);
		
		Assert.assertEquals(aValid, Utility.leftPad(a, "012", 4));
		Assert.assertEquals(bValid, Utility.leftPad(b, " ", 2));
		Assert.assertEquals(cValid, Utility.leftPad(c, "\t"));
		Assert.assertEquals(dValid, Utility.leftPad(d, ".", 8));
		
	}
	
	@org.junit.Test
	public void readUntilTest() {
		Assert.assertEquals("abcd", Utility.readUntil("abcd;efgh;ijkl", ";", 0));
		Assert.assertEquals("cd", Utility.readUntil("abcd;efgh;ijkl", ";", 2));
		Assert.assertEquals("", Utility.readUntil("abcd;efgh;ijkl", ";", 4));
		Assert.assertEquals("efgh", Utility.readUntil("abcd;efgh;ijkl", ";", 5));
		Assert.assertEquals("fgh", Utility.readUntil("abcd;efgh;ijkl", ";", 6));
		Assert.assertEquals("h", Utility.readUntil("abcd;efgh;ijkl", ";", 8));
		Assert.assertEquals("ijkl", Utility.readUntil("abcd;efgh;ijkl", ";", 10));
		Assert.assertNull(Utility.readUntil("abcd;efgh;ijkl", ";", 999));
	}
	
}