package Game.tests;

import Game.Game;
import org.junit.Assert;

public class GameTest {
	
	@org.junit.Test
	public void gameSerializeTest() {
		Game target = new Game();
		Game clone = new Game();
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target = new Game(24, 5, true);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target = new Game(1, 1, true);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
	}
	
	@org.junit.Test
	public void gameLocalFieldNoSyncTest() {
		// Regression test
		// Make sure "isPlayerRed" is not overwritten
		Game target = new Game(0, 0, false);
		Game clone = new Game(0, 0, true);
		clone.deserialize(target.serialize(), 0);
		Assert.assertTrue(clone.isPlayerRed());
		
		target = new Game(0, 0, true);
		clone = new Game(0, 0, false);
		clone.deserialize(target.serialize(), 0);
		Assert.assertFalse(clone.isPlayerRed());
	}
	
}
