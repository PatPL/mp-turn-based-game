package Game.Units.tests;

import Game.Units.Unit;
import org.junit.Assert;

public class UnitTest {
	
	@org.junit.Test
	public void unitSerializeTest() {
		Unit target = new Unit();
		Unit clone = new Unit();
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target.setDamage(1234);
		target.setHealth(4732432);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
		target.setRange(-123);
		target.setSpeed(-87654321);
		target.setTeam(-98);
		target.setDamage(-1);
		clone.deserialize(target.serialize(), 0);
		Assert.assertEquals(target.serialize(), clone.serialize());
		
	}
	
}
