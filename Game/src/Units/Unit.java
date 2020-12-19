package Units;

import BuildingsGenerators.Base;

public class Unit {

    //Attributes
    private int health;
    private int damage;
    private int range;      //default 1 for melee units
    private String name;    //archer, swordsman etc
    private int cost;       //gold cost of a unit
    private int speed;      //1 = 1 field per turn
    private static int team;       //1 - red ; 2 - blue


    //Getters
    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getRange() {
        return range;
    }

    public String getName() {
        return name;
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

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }


    //Basic constructor
    
    public Unit(){
        team = 0;
        name = "";
    }
    
    public Unit(int health, int damage, int range, String name, int cost, int speed){
        this.health = health;
        this.damage = damage;
        this.range = range;
        this.name = name;
        this.cost = cost;
        this.speed = speed;
    }


    //---------------------------------------------------METHODS------------------------------------------------------\\

    
    //Double-edged combat
    public void attackUnit(Unit enemyUnit){
        enemyUnit.setHealth(enemyUnit.getHealth() - damage);
        this.setHealth(this.getHealth() - enemyUnit.getDamage());
    }
    
    public void attackBase(Base enemyBase){
        enemyBase.setHealth(enemyBase.getHealth() - damage);
    }
}
