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
		"redSwordsman.png",
		"blueSwordsman.png"
	),
	archer(
		"Archer",
		new Unit(20, 10, 3, 2, 30, 3),
		"redArcher.png",
		"blueArcher.png"
	),
	knight(
		"Knight",
		new Unit(70, 30, 1, 3, 40, 1),
		"redKnight.png",
		"blueKnight.png"
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
