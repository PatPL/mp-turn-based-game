package Field;

import Units.Unit;

public class Field {
    private Unit unit; //each field can store up to 1 unit;

    public void removeUnit(){
    
    }

    public void addUnit(Unit newUnit){
        removeUnit();
        unit = newUnit;
    }
}
