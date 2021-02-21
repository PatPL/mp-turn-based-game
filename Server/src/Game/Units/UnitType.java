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
    // C - Short ranged
    C_1 (
        "Novice archer",
        new Unit (40, 15, 2, 1, 35),
        "units/C-short-ranged/red-short-ranged-1.png",
        "units/C-short-ranged/blue-short-ranged-1.png"
    ),
    C_2 (
        "Archer",
        new Unit (45, 20, 2, 1, 55),
        "units/C-short-ranged/red-short-ranged-2.png",
        "units/C-short-ranged/blue-short-ranged-2.png"
    ),
    C_3 (
        "Veteran archer",
        new Unit (50, 25, 2, 1, 75),
        "units/C-short-ranged/red-short-ranged-2.png",
        "units/C-short-ranged/blue-short-ranged-2.png"
    ),
    // D - Long ranged
    D_1 (
        "Novice longbowman",
        new Unit (30, 15, 3, 1, 40),
        "units/D-long-ranged/red-long-ranged-1.png",
        "units/D-long-ranged/blue-long-ranged-1.png"
    ),
    D_2 (
        "Longbowman",
        new Unit (35, 15, 4, 1, 65),
        "units/D-long-ranged/red-long-ranged-2.png",
        "units/D-long-ranged/blue-long-ranged-2.png"
    ),
    D_3 (
        "Veteran longbowman",
        new Unit (40, 20, 4, 1, 85),
        "units/D-long-ranged/red-long-ranged-3.png",
        "units/D-long-ranged/blue-long-ranged-3.png"
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