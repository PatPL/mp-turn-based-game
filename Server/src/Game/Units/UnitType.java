package Game.Units;

import javax.imageio.ImageIO;
import java.awt.*;

public enum UnitType {
    empty (
        "-",
        new Unit (0, 0, 0, 0, 0),
        "null64.png",
        "null64.png"
    ),
    swordsman (
        "Swordsman",
        new Unit (40, 15, 1, 20, 1),
        "units/redSwordsman.png",
        "units/blueSwordsman.png"
    ),
    archer (
        "Archer",
        new Unit (20, 10, 3, 30, 1),
        "units/redArcher.png",
        "units/blueArcher.png"
    ),
    knight (
        "Knight",
        new Unit (60, 25, 1, 50, 1),
        "units/redKnight.png",
        "units/blueKnight.png"
    ),
    scout (
        "Scout",
        new Unit (25, 10, 1, 30, 2),
        "units/A-light-infantry/red-light-infantry-1.png",
        "units/A-light-infantry/blue-light-infantry-1.png"
    ),
    mage (
        "Mage",
        new Unit (40, 20, 2, 40, 1),
        "units/redMage.png",
        "units/blueMage.png"
    ),
    tank (
        "Tank",
        new Unit (120, 0, 1, 70, 1),
        "units/redTank.png",
        "units/blueTank.png"
    ),
    horseman (
        "Horseman",
        new Unit (70, 20, 1, 80, 2),
        "units/redHorseman.png",
        "units/blueHorseman.png"
    ),
    lancer (
        "Lancer",
        new Unit (85, 30, 1, 125, 2),
        "units/redLancer.png",
        "units/blueLancer.png"
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