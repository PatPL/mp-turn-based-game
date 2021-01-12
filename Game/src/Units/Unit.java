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
    private int team;       //1 - red ; 2 - blue
    
    
    //Getters
    public int getHealth() {
        return health;
    }

    private int getDamage() {
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
    private void setHealth(int health) {
        this.health = health;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    
    //Changes team number to team color string
    private String fromTeamNumberToTeamColor(){
        if(team == 2) return "Blue";
        return "Red";
    }
    
    
    //Constructors
    public Unit(){
        team = 0;
        name = "";
    }
    
    
    public Unit(int health, int newDamage, int range, String name, int cost, int speed, int team){
        this.health = health;
        this.damage = newDamage;
        this.range = range;
        this.name = name;
        this.cost = cost;
        this.speed = speed;
        this.team = team;
    }
    
    
    public void attackUnit(Unit enemyUnit){
    
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
    
    
    public void attackBase(Base enemyBase){
        enemyBase.setHealth(enemyBase.getHealth() - damage);
        System.out.printf("%s dealt %d damage to the %s base.\n", fromTeamNumberToTeamColor(), damage, enemyBase.fromTeamNumberToTeamColor());
        
    }
}
