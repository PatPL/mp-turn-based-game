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
		target.setAttackModifier(1.7);
		target.setHealthModifier(2.9);
		target.setAttackUpgradeCost(783);
		target.setHealthUpgradeCost(132);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target.setGold(0);
		target.setGoldIncome(0);
		target.setHealth(0);
		target.setPowerBar(0);
		target.setAttackModifier(12.6);
		target.setHealthModifier(17.3);
		target.setAttackUpgradeCost(30);
		target.setHealthUpgradeCost(10);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target.setGold(-123456789);
		target.setGoldIncome(-55);
		target.setHealth(0);
		target.setPowerBar(987654321);
		target.setAttackModifier(0);
		target.setHealthModifier(0);
		target.setAttackUpgradeCost(0);
		target.setHealthUpgradeCost(0);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
	}
	
}
