import BuildingsGenerators.*;
import Field.*;
import Units.*;

import java.io.Console;
import java.util.HashMap;


public class Test {
	
	private final static int rows = 3;
	private final static int columns = 10;
	private Unit[][] unitMap = new Unit[rows][columns];
	private final static int baseHealth = 200;
	
	private Base redBase = new Base(baseHealth, 1);
	private Base blueBase = new Base(baseHealth, 2);
	
	
	//Returns a letter according to the unit
	private String letter(Unit unit, int team){
		if(unit.getHealth() <= 0) unit = new Unit();
		String teamLetter = "R";
		if(team == 2) teamLetter = "B";                                                    //A - Archer
		if(unit.getName().equals("Archer")) return "[" + teamLetter + "A]    ";            //S - Swordsman
		if(unit.getName().equals("Swordsman")) return "[" + teamLetter + "S]    ";         //K - Knight
		if(unit.getName().equals("Knight")) return "[" + teamLetter + "K]    ";            //O - empty
		else return "[O]    ";                                                             //R - Red  ; B - Blue
	}
	
	//Displays current map
	private void drawMap(int team){
		for(int i = 0 ; i < rows ; i++){
			System.out.print("RED    ");
			for(int k = 0 ; k < columns; k++){
				System.out.print(letter(unitMap[i][k], team));
			}
			System.out.println("BLUE\n\n\n");
		}
		System.out.println("====================================================================================");
	}
	
	//Displays actual game status
	private void displayStatus(){
		System.out.println("====================================================================================");
		System.out.println("Red health: " + redBase.getHealth() + "                                                    Blue health: " + blueBase.getHealth());
		System.out.println("Red gold: " + redBase.getGold() + "                                                        Blue gold: " + blueBase.getGold() + "\n");
	}
	
	//Turn for one player
	private void turn(Base base){
		System.out.println("================================== " + base.getTeamNumber()+ " Player Turn ===================================");
		displayStatus();
		drawMap(base.getTeamNumber());
	}
	
	
	//THIS IS WhERE THE CODE IS EXECUTED
	public static void main (String[] args) {
		try
		{
			Test obj = new Test ();
			obj.run (args);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
	
	
	public void run (String[] args) throws Exception {
		
		//Setting every unit on map to be like "not existing"
		for(int i = 0 ; i < rows ; i++){
			for(int k = 0 ; k < columns; k++){
				unitMap[i][k] = new Unit();
			}
		}
		
		//Game starts
		while(redBase.getHealth() > 0 && blueBase.getHealth() > 0){
			turn(redBase);
			turn(blueBase);
		}
	}
}

