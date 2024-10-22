package Game;

import Game.Units.Unit;
import Game.Units.UnitType;
import common.Utility;
import common.interfaces.ITextSerializable;

public class Base implements ITextSerializable {
    
    private int health;
    private int teamNumber;
    private int gold;
    private int goldIncome;
    private double attackModifier;
    private double healthModifier;
    private int attackUpgradeCost;
    private int healthUpgradeCost;
    
    //Getters
    public int getHealth () {
        return health;
    }
    
    public int getTeamNumber () {
        return teamNumber;
    }
    
    public int getGold () {
        return gold;
    }
    
    public int getGoldIncome () {
        return goldIncome;
    }
    
    public double getAttackModifier () {
        return attackModifier;
    }
    
    public double getHealthModifier () {
        return healthModifier;
    }
    
    public int getAttackUpgradeCost () {
        return attackUpgradeCost;
    }
    
    public int getHealthUpgradeCost () {
        return healthUpgradeCost;
    }
    
    //Setters
    public void setHealth (int health) {
        this.health = health;
    }
    
    public void addHealth (int deltaHealth) {
        this.health += deltaHealth;
    }
    
    public void setGoldIncome (int goldIncome) {
        this.goldIncome = goldIncome;
    }
    
    public void setGold (int newGold) {
        this.gold = newGold;
    }
    
    public void addGold (int deltaGold) {
        this.gold += deltaGold;
    }
    
    public void setAttackModifier (double newAttackModifier) {
        attackModifier = newAttackModifier;
    }
    
    public void setHealthModifier (double newHealthModifier) {
        healthModifier = newHealthModifier;
    }
    
    public void setAttackUpgradeCost (int attackUpgradeCost) {
        this.attackUpgradeCost = attackUpgradeCost;
    }
    
    public void setHealthUpgradeCost (int healthUpgradeCost) {
        this.healthUpgradeCost = healthUpgradeCost;
    }
    
    //Constructor
    public Base () {
    }
    
    public Base (int health, int teamNumber) {
        this.health = health;
        this.teamNumber = teamNumber;
        gold = 100;
        goldIncome = 10;
        attackModifier = 1.0;
        healthModifier = 1.0;
        healthUpgradeCost = 40;
        attackUpgradeCost = 40;
    }
    
    public String fromTeamNumberToTeamColor () {
        if (teamNumber == 2) { return "Blue"; }
        return "Red";
    }
    
    public void addGoldIncome () {
        gold += goldIncome;
    }
    
    public Unit createUnit (String choice) {
        return new Unit (UnitType.values ()[Integer.parseInt (choice)], 1, 1, teamNumber);
        // return unitGenerator.createUnit(choice, teamNumber);
    }
    
    public boolean upgradeGold () {
        // gold is current amount of gold this base has
        // goldIncome is amount of gold that will be added every turn
        if (gold >= (goldIncome * 25 / 10)) {
            //Changes value of gold and gold income after upgrading gold income
            gold -= goldIncome * 25 / 10;
            goldIncome += 10;
            return true;
        }
        
        return false;
    }
    
    public boolean upgradeAttack () {
        if (gold >= attackUpgradeCost) {
            //Changes value attack modifier and it's cost
            gold -= attackUpgradeCost;
            attackModifier += 0.5;
            attackUpgradeCost += 40;
            return true;
        }
        
        return false;
    }
    
    public boolean upgradeHealth () {
        if (gold >= healthUpgradeCost) {
            //Changes value attack modifier and it's cost
            gold -= healthUpgradeCost;
            healthModifier += 0.5;
            healthUpgradeCost += 40;
            return true;
        }
        
        return false;
    }
    
    @Override
    public String serialize () {
        StringBuilder output = new StringBuilder ();
        
        output.append (health);
        output.append (";");
        output.append (teamNumber);
        output.append (";");
        output.append (gold);
        output.append (";");
        output.append (goldIncome);
        output.append (";");
        output.append (attackModifier);
        output.append (";");
        output.append (healthModifier);
        output.append (";");
        output.append (attackUpgradeCost);
        output.append (";");
        output.append (healthUpgradeCost);
        output.append (";");
        
        return output.toString ();
    }
    
    @Override
    public int deserialize (String rawText, int offset) {
        int addedOffset = 0;
        String tmp;
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.health = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.teamNumber = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.gold = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.goldIncome = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.attackModifier = Double.parseDouble (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.healthModifier = Double.parseDouble (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.attackUpgradeCost = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.healthUpgradeCost = Integer.parseInt (tmp);
        
        return addedOffset;
    }
    
}
