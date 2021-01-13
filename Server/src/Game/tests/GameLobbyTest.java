package Game.tests;

import Game.Game;
import org.junit.Assert;

public class GameLobbyTest {
	
	@org.junit.Test
	public void gameSerializeTest() {
		Game target = new Game();
		Game clone = new Game();
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target = new Game(24, 5);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target = new Game(1, 1);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
	}
	
}
