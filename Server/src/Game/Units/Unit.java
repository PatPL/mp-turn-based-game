package Game.Units;

import Game.BuildingsGenerators.Base;
import Game.interfaces.ITextSerializable;
import Webserver.Utility;

import java.awt.*;

public class Unit implements ITextSerializable {
	
	//Attributes
	private int health;
	private int maxHealth;
	private int damage;
	private int range;      //default 1 for melee units
	private int type;       //archer, swordsman etc
	private int cost;       //gold cost of a unit
	private int speed;      //1 = 1 field per turn
	private int team;       //1 - red ; 2 - blue
	
	
	//Getters
	public int getHealth() {
		return health;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getRange() {
		return range;
	}
	
	public String getName() {
		return getType().name;
	}
	
	public UnitType getType() {
		return UnitType.values()[type];
	}
	
	public Image getImage() {
		if(team <= 1) {
			return getType().redImage;
		}
		else {
			return getType().blueImage;
		}
	}
	
	public int getCost() {
		return cost;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getTeam() {
		return team;
	}
	
	
	//Setters
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void addHealth(int deltaHealth) {
		health += deltaHealth;
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	public void setRange(int range) {
		this.range = range;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public void setTeam(int team) {
		this.team = team;
	}
	
	//Changes team number to team color string
	private String fromTeamNumberToTeamColor() {
		if(team == 2) return "Blue";
		return "Red";
	}
	
	public void die() {
		this.deserialize(UnitType.empty.defaultUnit.serialize(), 0);
	}
	
	//Constructors
	public Unit() {
		this(UnitType.empty);
	}
	
	public Unit(UnitType type) {
		this(type, 1, 1, 0);
	}
	
	public Unit(UnitType type, Base base) {
		this(
			type,
			base.getHealthModifier(),
			base.getAttackModifier(),
			base.getTeamNumber()
		);
	}
	
	public Unit(UnitType type, double healthMultiplier, double attackMultiplier, int teamNumber) {
		this.deserialize(type.defaultUnit.serialize(), 0);
		this.health *= healthMultiplier;
		this.maxHealth *= healthMultiplier;
		this.damage *= attackMultiplier;
		this.team = teamNumber;
	}
	
	public Unit(int health, int newDamage, int range, int type, int cost, int speed) {
		this.health = health;
		this.maxHealth = health;
		this.damage = newDamage;
		this.range = range;
		this.type = type;
		this.cost = cost;
		this.speed = speed;
		this.team = 0;
	}
	
	
	public void attackUnit(Unit enemyUnit) {
		
		//Double-edged combat for melee units
		if(range == 1) {
			enemyUnit.setHealth(enemyUnit.getHealth() - damage);
			this.setHealth(this.getHealth() - enemyUnit.getDamage());
			System.out.printf("%s dealt %d damage to %s.\n", fromTeamNumberToTeamColor(), damage, enemyUnit.fromTeamNumberToTeamColor());
			System.out.printf("%s dealt %d damage to %s.\n", enemyUnit.fromTeamNumberToTeamColor(), enemyUnit.getDamage(), fromTeamNumberToTeamColor());
			return;
		}
		
		//Ranged units damages enemy without taking damage
		enemyUnit.setHealth(enemyUnit.getHealth() - damage);
		System.out.printf("%s dealt %d damage to %s.\n", fromTeamNumberToTeamColor(), damage, enemyUnit.fromTeamNumberToTeamColor());
	}
	
	
	public void attackBase(Base enemyBase) {
		enemyBase.setHealth(enemyBase.getHealth() - damage);
		System.out.printf("%s dealt %d damage to the %s base.\n", fromTeamNumberToTeamColor(), damage, enemyBase.fromTeamNumberToTeamColor());
		
	}
	
	@Override
	public String serialize() {
		StringBuilder output = new StringBuilder();
		
		output.append(health);
		output.append(";");
		output.append(maxHealth);
		output.append(";");
		output.append(damage);
		output.append(";");
		output.append(range);
		output.append(";");
		output.append(type);
		output.append(";");
		output.append(cost);
		output.append(";");
		output.append(speed);
		output.append(";");
		output.append(team);
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
		this.maxHealth = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.damage = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.range = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.type = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.cost = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.speed = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.team = Integer.parseInt(tmp);
		
		return addedOffset;
	}
}
