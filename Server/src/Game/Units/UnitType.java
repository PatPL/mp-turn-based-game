package Game.Units;

import javax.imageio.ImageIO;
import java.awt.*;

public enum UnitType {
    // A - Light infantry
    A_1 (
        "Spotter",
        new Unit (30, 30, 1, 2, 40),
        "units/A-light-infantry/red-light-infantry-1.png",
        "units/A-light-infantry/blue-light-infantry-1.png"
    ),
    A_2 (
        "Scout",
        new Unit (35, 35, 1, 2, 60),
        "units/A-light-infantry/red-light-infantry-2.png",
        "units/A-light-infantry/blue-light-infantry-2.png"
    ),
    A_3 (
        "Spy",
        new Unit (40, 40, 1, 2, 80),
        "units/A-light-infantry/red-light-infantry-3.png",
        "units/A-light-infantry/blue-light-infantry-3.png"
    ),
    // B - Infantry
    B_1 (
        "Recruit",
        new Unit (50, 20, 1, 1, 25),
        "units/B-infantry/red-infantry-1.png",
        "units/B-infantry/blue-infantry-1.png"
    ),
    B_2 (
        "Soldier",
        new Unit (60, 25, 1, 1, 45),
        "units/B-infantry/red-infantry-2.png",
        "units/B-infantry/blue-infantry-2.png"
    ),
    B_3 (
        "Veteran",
        new Unit (70, 30, 1, 1, 65),
        "units/B-infantry/red-infantry-3.png",
        "units/B-infantry/blue-infantry-3.png"
    ),
    empty (
        "-",
        new Unit (0, 0, 0, 0, 0),
        "null64.png",
        "null64.png"
    );
    
    public final String name;
    public final Unit defaultUnit;
    public final Image redImage;
    public final Image blueImage;
    
    UnitType (String name, Unit defaultUnit, String redImagePath, String blueImagePath) {
        this.name = name;
        this.defaultUnit = defaultUnit;
        // For whatever reason enum can't access its static stuff in the constructor
        // so it needs to use another class...                         java, i guess
        this.defaultUnit.setType (UnitTypeEnumCounterContainer.indexCounter++);
        
        Image tmpRedImage = null;
        Image tmpBlueImage = null;
        try {
            tmpRedImage = ImageIO.read (getClass ().getClassLoader ().getResource (redImagePath));
            tmpBlueImage = ImageIO.read (getClass ().getClassLoader ().getResource (blueImagePath));
        } catch (Exception e) {
            System.out.println ("Error in UnitType initializer");
            e.printStackTrace ();
        }
        this.redImage = tmpRedImage;
        this.blueImage = tmpBlueImage;
    }
}

class UnitTypeEnumCounterContainer {
    public static int indexCounter = 0;
}