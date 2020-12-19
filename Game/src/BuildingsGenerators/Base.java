package BuildingsGenerators;


public class Base {

    private int health;
    private int teamNumber;             // 1 - red team ; 2 - blue team
    private int gold;                          // current gold
    private static UnitGenerator unitGenerator;

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

    //Setters
    public void setHealth(int health) {
        this.health = health;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    //Constructor
    public Base(int health, int teamNumber){
        this.health = health;
        this.teamNumber = teamNumber;
        gold = 20;
    }
}
