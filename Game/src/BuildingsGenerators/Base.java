package BuildingsGenerators;


import Units.Unit;

public class Base {

    private int health;
    private int teamNumber;                         // 1 - red team ; 2 - blue team
    private int gold;                               // current gold
    private static UnitGenerator unitGenerator;
    private int goldIncome;
    private int powerBar;

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
    
    public int getPowerBar() {
        return powerBar;
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
    
    //Constructor
    public Base(int health, int teamNumber){
        this.health = health;
        this.teamNumber = teamNumber;
        gold = 20;
        unitGenerator = new UnitGenerator();
        goldIncome = 10;
        powerBar = 0;
    }
    
    public String fromTeamNumberToTeamColor(){
        if(teamNumber == 2) return "Blue";
        return "Red";
    }
    
    public void addGoldIncome(){
        gold += goldIncome;
    }
    
    public Unit createUnit(String choice){
        return unitGenerator.createUnit(choice, teamNumber);
    }
}
