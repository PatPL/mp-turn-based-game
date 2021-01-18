package Game.Units;

import javax.imageio.ImageIO;
import java.awt.*;

public enum UnitType {
	empty(
		"-",
		new Unit(0, 0, 0, 0, 0, 0),
		"null64.png",
		"null64.png"
	),
	swordsman(
		"Swordsman",
		new Unit(50, 20, 1, 1, 20, 1),
		"units/redSwordsman.png",
		"units/blueSwordsman.png"
	),
	archer(
		"Archer",
		new Unit(20, 10, 3, 2, 30, 1),
		"units/redArcher.png",
		"units/blueArcher.png"
	),
	knight(
		"Knight",
		new Unit(70, 30, 1, 3, 40, 1),
		"units/redKnight.png",
		"units/blueKnight.png"
	),
	scout(
		"Scout",
		new Unit(25, 10, 1, 4, 30, 2),
		"units/redScout.png",
		"units/blueScout.png"
	),
	mage(
		"Mage",
		new Unit(40, 20, 2, 5, 40, 1),
		"units/redMage.png",
		"units/blueMage.png"
	);
	
	public final String name;
	public final Unit defaultUnit;
	public final Image redImage;
	public final Image blueImage;
	
	UnitType(String name, Unit defaultUnit, String redImagePath, String blueImagePath) {
		this.name = name;
		this.defaultUnit = defaultUnit;
		
		Image tmpRedImage = null;
		Image tmpBlueImage = null;
		try {
			tmpRedImage = ImageIO.read(getClass().getClassLoader().getResource(redImagePath));
			tmpBlueImage = ImageIO.read(getClass().getClassLoader().getResource(blueImagePath));
		}
		catch(Exception e) {
			System.out.println("Error in UnitType initializer");
		}
		this.redImage = tmpRedImage;
		this.blueImage = tmpBlueImage;
	}
}
