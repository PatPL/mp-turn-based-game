import BuildingsGenerators.*;
import Units.*;

import java.util.Scanner;

public class Test {
	
	//Attributes for the test
	private final static int rows = 3;
	private final static int columns = 10;
	private Unit[][] unitMap = new Unit[rows][columns];
	
	private final static int baseHealth = 200;
	private Base redBase = new Base(baseHealth, 1);
	private Base blueBase = new Base(baseHealth, 2);
	private Scanner scan = new Scanner(System.in);
	
	//Places unit on a battlefield
	private void setUnitOnMap(Unit unit, int row, int column) {
		unitMap[row][column] = unit;
	}
	
	//Checks if it's possible to create an unit with current gold
	private boolean canBuyUnit(Base base, Unit unit){
		return (base.getGold() - unit.getCost() >= 0);
	}
	
	//Implements movement and attacks every turn. Unit can move or attack every turn.
	private void unitsTurn(Base base) {
		
		//Algorithm for red player
		if(base.getTeamNumber() == 1) {
			for(int i = 0; i < rows; i++) {
				for(int k = columns - 2; k >= 0; k--) {
					
					//Checks if current unit is dead. If true, replaces it with a new default unit as an empty field.
					if(unitMap[i][k].getHealth() <= 0){
						unitMap[i][k] = new Unit();
						continue;
					}
					
					//Attacks base if it's on the penultimate field
					if(unitMap[i][k].getTeam() == 1 && k == columns - 2) {
						unitMap[i][k].attackBase(blueBase);
						continue;
					}
					
					//Algorith only for ranged unit (first it scans possibility of attacks, if not, it moves)
					if(unitMap[i][k].getTeam() == 1 && unitMap[i][k].getRange() > 1){
						
						boolean didItAttacked = false;
						
						//Loop to search from the nearest to farest enemy in range, also includes attacking enemy base
						for(int r = 1;r <= unitMap[i][k].getRange(); r++){
							
							if(k+r >= columns){
								unitMap[i][k].attackBase(blueBase);
								didItAttacked = true;
								break;
							}
							
							if(unitMap[i][k+r].getTeam() == 2){
								unitMap[i][k].attackUnit(unitMap[i][k+r]);
								didItAttacked = true;
								break;
							}
						}
						
						//with no attack, it moves
						if(!didItAttacked){
							if(unitMap[i][k + 1].getTeam() == 0) {
								unitMap[i][k + 1] = unitMap[i][k];
								unitMap[i][k] = new Unit();
							}
						}
						continue;
					}
					
					//Melee attacking enemy unit
					if(unitMap[i][k].getTeam() == 1 && unitMap[i][k + 1].getTeam() == 2) {
						unitMap[i][k].attackUnit(unitMap[i][k + 1]);
						continue;
					}
					
					//Move from one field to another
					if(unitMap[i][k].getTeam() == 1 && unitMap[i][k + 1].getTeam() == 0 && k != columns - 2) {
						unitMap[i][k + 1] = unitMap[i][k];
						unitMap[i][k] = new Unit();
					}
				}
			}
		}
		
		//Algorithm for blue player
		if(base.getTeamNumber() == 2) {
			for(int i = 0; i < rows; i++) {
				for(int k = 1; k < columns; k++) {
					
					//Checks if current unit is dead. If true, replaces it with a new default unit as an empty field.
					if(unitMap[i][k].getHealth() <= 0){
						unitMap[i][k] = new Unit();
						continue;
					}
					
					//Attacks base if it's on the penultimate field
					if(unitMap[i][k].getTeam() == 2 && k == 1) {
						unitMap[i][k].attackBase(redBase);
						continue;
					}
					
					//Algorith only for ranged unit (first it scans possibility of attacks, if not, it moves)
					if(unitMap[i][k].getTeam() == 2 && unitMap[i][k].getRange() > 1){
						
						boolean didItAttacked = false;
						
						//Loop to search from the nearest to farest enemy in range, also includes attacking enemy base
						for(int r = 1;r <= unitMap[i][k].getRange(); r++){
							
							if(k-r <= 0){
								unitMap[i][k].attackBase(redBase);
								didItAttacked = true;
								break;
							}
							
							if(unitMap[i][k-r].getTeam() == 1){
								unitMap[i][k].attackUnit(unitMap[i][k-r]);
								didItAttacked = true;
								break;
							}
							
						}
						
						//with no attack, it moves
						if(!didItAttacked){
							if(unitMap[i][k - 1].getTeam() == 0) {
								unitMap[i][k - 1] = unitMap[i][k];
								unitMap[i][k] = new Unit();
							}
						}
						continue;
					}
					
					//Melee attacking enemy unit
					if(unitMap[i][k].getTeam() == 2 && unitMap[i][k - 1].getTeam() == 1) {
						unitMap[i][k].attackUnit(unitMap[i][k - 1]);
						continue;
					}
					
					//Move from one field to another
					if(unitMap[i][k].getTeam() == 2 && unitMap[i][k - 1].getTeam() == 0 && k != 1) {
						unitMap[i][k - 1] = unitMap[i][k];
						unitMap[i][k] = new Unit();
					}
				}
			}
		}
		
		
	}
	
	
	//Returns a letter/s according to the unit (map function below)
	private String letter(Unit unit){
		if(unit.getHealth() <= 0) unit = new Unit();
		String teamLetter = "R";
		if(unit.getTeam() == 2) teamLetter = "B";                                          //A - Archer
		if(unit.getName().equals("Archer")) return "[" + teamLetter + "A]    ";            //S - Swordsman
		if(unit.getName().equals("Swordsman")) return "[" + teamLetter + "S]    ";         //K - Knight
		if(unit.getName().equals("Knight")) return "[" + teamLetter + "K]    ";            //O - Empty
		else return "[O]    ";                                                             //R - Red  ; B - Blue
	}
	
	
	//Displays current map
	private void drawMap(){
		for(int i = 0 ; i < rows ; i++){
			System.out.print("RED    ");
			for(int k = 0 ; k < columns; k++){
				System.out.print(letter(unitMap[i][k]));
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
	
	
	//Menu with an interaction for player
	private void choiceMenu(Base base){
		
		//Menu for choosing a line
		System.out.println("***************************************MENU*****************************************");
		System.out.println("* 1 - Top           2 - Mid           3 - Bottom          4 - Upgrade income(+10)  *");
		System.out.println("*    By writing a number (1,2,3), you will choose a spawn location of your unit    *");
		System.out.println("*      By writing a number 4, you will upgrade you gold income and END TURN!       *");
		System.out.println("*  Write number and press enter to continue, or press only enter to skip the turn. *");
		System.out.println("************************************************************************************");
		
		//Loop while to avoid bugs
		String line = scan.nextLine();
		if(line.equals("")) return;
		if(line.equals("4")){
			base.setGoldIncome(base.getGoldIncome() + 10);
			return;
		}
		
		while(!line.equals("1") && !line.equals("2") && !line.equals("3")){
			System.out.println("*********************Make sure to correctly enter your choice***********************");
			line = scan.nextLine();
		}
		
		//Menu for creating new unit
		System.out.println("***************************************MENU*****************************************");
		System.out.println("* 1 - Create swordsman              2 - Create archer             3 - Create knight*");
		System.out.println("*  Write number and press enter to continue, or press only enter to skip the turn. *");
		System.out.println("************************************************************************************");
		
		//Loop while to avoid bugs
		String choice = scan.nextLine();
		if(choice.equals("")) return;
		while(!choice.equals("1") && !choice.equals("2") && !choice.equals("3")){
			System.out.println("*********************Make sure to correctly enter your choice***********************\n");
			choice = scan.nextLine();
		}
		
		while(!canBuyUnit(base, base.createUnit(choice))){
			System.out.println("You have not enough gold to buy this unit. Re-enter the correct number or skip...\n");
			choice = scan.nextLine();
			if(choice.equals("")) return;
		}
		
		//Red team spawns units on the left side, blue team spawns units on the right side
		int spawnNumber = 0;
		if(base.getTeamNumber() == 2) spawnNumber = columns - 1;
		
		//Spawns chosen unit and decreases gold
		Unit newUnit = base.createUnit(choice);
		base.setGold(base.getGold() - newUnit.getCost());
		setUnitOnMap(newUnit, Integer.parseInt(line) - 1, spawnNumber);
		drawMap();
	}
	
	//Turn for one player
	private void turn(Base base){
		//Interaction with player to continue after pressing enter
		System.out.println(base.getTeamNumber() + "\n Player PRESS ENTER TO CONTINUE...");
		scan.nextLine();
		
		//Gold income every turn
		base.addGoldIncome();
		System.out.println("================================== " + base.getTeamNumber()+ " Player Turn ===================================");
		
		//Displays status of both players
		displayStatus();
		
		//Draws current map with units
		drawMap();
		
		//Menu for a player (spawn an unit or upgrade income or skip a turn)
		choiceMenu(base);
		
		//Units for current team are making a turn
		unitsTurn(base);
		
		//Win conditions
		if(redBase.getHealth() <= 0){
			System.out.println("CONGRATULATIONS, BLUE TEAM WINS!");
			System.exit(0);
		}
		if(blueBase.getHealth()<= 0){
			System.out.println("CONGRATULATIONS, RED TEAM WINS!");
			System.exit(0);
		}
	}
	
	
	//Code execution
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
		
		while(true){
			
			//Game starts
			turn(redBase);
			turn(blueBase);
		}
	}
}

