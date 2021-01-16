package Game.BuildingsGenerators;


import Game.Units.Unit;
import Game.interfaces.ITextSerializable;
import Webserver.Utility;

public class Base implements ITextSerializable {
	
	private int health;
	private int teamNumber;
	private int gold;
	private static final UnitGenerator unitGenerator = new UnitGenerator();
	private int goldIncome;
	private int powerBar;
	private double attackModifier;
	private double healthModifier;
	private int attackUpgradeCost;
	private int healthUpgradeCost;
	
	//Getters
	public int getHealth() {
		return health;
	}
	
	public int getTeamNumber() {
		return teamNumber;
	}
	
	public int getGold() {
		return gold;
	}
	
	public int getGoldIncome() {
		return goldIncome;
	}
	
	public int getPowerBarValue() {
		return powerBar;
	}

	public double getAttackModifier() {
		return attackModifier;
	}

	public double getHealthModifier() {
		return healthModifier;
	}

	public int getAttackUpgradeCost() {
		return attackUpgradeCost;
	}

	public int getHealthUpgradeCost() {
		return healthUpgradeCost;
	}

	//Setters
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setGoldIncome(int goldIncome) {
		this.goldIncome = goldIncome;
	}
	
	public void setGold(int newGold) {
		this.gold = newGold;
	}
	
	public void setPowerBar(int powerBar) {
		this.powerBar = powerBar;
	}

	public void setAttackModifier(double newAttackModifier) {
		attackModifier = newAttackModifier;
	}

	public void setHealthModifier(double newHealthModifier) {
		healthModifier = newHealthModifier;
	}

	public void setAttackUpgradeCost(int attackUpgradeCost) {
		this.attackUpgradeCost = attackUpgradeCost;
	}

	public void setHealthUpgradeCost(int healthUpgradeCost) {
		this.healthUpgradeCost = healthUpgradeCost;
	}

	//Constructor
	public Base() {
	}

	public Base(int health, int teamNumber) {
		this.health = health;
		this.teamNumber = teamNumber;
		gold = 100;
		goldIncome = 10;
		powerBar = 30;
		attackModifier = 1.0;
		healthModifier = 1.0;
		healthUpgradeCost = 40;
		attackUpgradeCost = 40;
	}
	
	public String fromTeamNumberToTeamColor() {
		if(teamNumber == 2) return "Blue";
		return "Red";
	}
	
	public void addGoldIncome() {
		gold += goldIncome;
	}
	
	public Unit createUnit(String choice) {
		return unitGenerator.createUnit(choice, teamNumber);
	}
	
	@Override
	public String serialize() {
		StringBuilder output = new StringBuilder();
		
		output.append(health);
		output.append(";");
		output.append(teamNumber);
		output.append(";");
		output.append(gold);
		output.append(";");
		output.append(goldIncome);
		output.append(";");
		output.append(powerBar);
		output.append(";");
		
		return output.toString();
	}
	
	@Override
	public int deserialize(String rawText, int offset) {
		int addedOffset = 0;
		String tmp;
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.health = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.teamNumber = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.gold = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.goldIncome = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.powerBar = Integer.parseInt(tmp);
		
		return addedOffset;
	}
	
}
