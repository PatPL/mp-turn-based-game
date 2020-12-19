package BuildingsGenerators;
import Units.Unit;


public class UnitGenerator {
	
	//Argument is an option chosen by player (f.ex. swordsman - 1, archer - 2, knight - 3 etc...)
	public Unit createUnit(int option){
		switch(option){
			case 1:
				return new Unit(50, 20, 1, "Swordsman", 20, 1);
			case 2:
				return new Unit(20, 20, 2, "Archer", 30, 1);
			case 3:
				return new Unit(70, 30, 1, "Knight", 40,1);
		}
		return null;
	}
}
