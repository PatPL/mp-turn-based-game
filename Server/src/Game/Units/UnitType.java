package Game.Units;

public enum UnitType {
	empty("-", new Unit(0, 0, 0, 0, 0, 0)),
	swordsman("Swordsman", new Unit(50, 20, 1, 1, 20, 1)),
	archer("Archer", new Unit(20, 10, 3, 2, 10, 3)),
	knight("Knight", new Unit(70, 30, 1, 3, 30, 1));
	
	public final String name;
	public final Unit defaultUnit;
	
	UnitType(String name, Unit defaultUnit) {
		this.name = name;
		this.defaultUnit = defaultUnit;
	}
}
