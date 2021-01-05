package BuildingsGenerators;
import Units.Unit;


public class UnitGenerator {
	
	
	//Constructor
	public UnitGenerator(){
	
	}
	
	//Argument is an option chosen by player (f.ex. swordsman - 1, archer - 2, knight - 3 etc...)
	public Unit createUnit(String choice, int teamNumber) {
		
		switch(choice) {
			case "1":
				return new Unit(50, 20, 1, "Swordsman", 20, 1, teamNumber);
			case "2":
				return new Unit(20, 10, 3, "Archer", 30, 1, teamNumber);
			case "3":
				return new Unit(70, 30, 1, "Knight", 40, 1, teamNumber);
			default:
				return new Unit();
		}
	}
}
