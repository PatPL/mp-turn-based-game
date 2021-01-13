package Game.BuildingsGenerators.tests;

import Game.BuildingsGenerators.Base;
import org.junit.Assert;

public class BaseTest {
	
	@org.junit.Test
	public void baseSerializeTest() {
		Base target = new Base();
		Base clone = new Base();
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target.setGold(123);
		target.setGoldIncome(321);
		target.setHealth(999);
		target.setPowerBar(111);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target.setGold(0);
		target.setGoldIncome(0);
		target.setHealth(0);
		target.setPowerBar(0);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target.setGold(-123456789);
		target.setGoldIncome(-55);
		target.setHealth(0);
		target.setPowerBar(987654321);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
	}
	
}
