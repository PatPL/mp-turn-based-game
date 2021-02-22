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
        "Squire",
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
        "Sergeant",
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
        "units/C-short-ranged/red-short-ranged-3.png",
        "units/C-short-ranged/blue-short-ranged-3.png"
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
    // E - Heavy infantry
    E_1 (
        "Recruit",
        new Unit (100, 25, 1, 1, 60),
        "units/E-heavy-infantry/red-heavy-infantry-1.png",
        "units/E-heavy-infantry/blue-heavy-infantry-1.png"
    ),
    E_2 (
        "Crusader",
        new Unit (130, 30, 1, 1, 95),
        "units/E-heavy-infantry/red-heavy-infantry-2.png",
        "units/E-heavy-infantry/blue-heavy-infantry-2.png"
    ),
    E_3 (
        "Elite crusader",
        new Unit (160, 35, 1, 1, 120),
        "units/E-heavy-infantry/red-heavy-infantry-3.png",
        "units/E-heavy-infantry/blue-heavy-infantry-3.png"
    ),
    // F - Cavalry
    F_1 (
        "Village peasant",
        new Unit (80, 35, 1, 2, 85),
        "units/F-cavalry/red-cavalry-1.png",
        "units/F-cavalry/blue-cavalry-1.png"
    ),
    F_2 (
        "Village militia",
        new Unit (100, 50, 1, 2, 135),
        "units/F-cavalry/red-cavalry-2.png",
        "units/F-cavalry/blue-cavalry-2.png"
    ),
    F_3 (
        "Militia sergeant",
        new Unit (120, 65, 1, 2, 190),
        "units/F-cavalry/red-cavalry-3.png",
        "units/F-cavalry/blue-cavalry-3.png"
    ),
    // G - Heavy cavalry
    G_1 (
        "Horseman",
        new Unit (110, 40, 1, 2, 115),
        "units/G-heavy-cavalry/red-heavy-cavalry-1.png",
        "units/G-heavy-cavalry/blue-heavy-cavalry-1.png"
    ),
    G_2 (
        "Armored horseman",
        new Unit (145, 60, 1, 1, 175),
        "units/G-heavy-cavalry/red-heavy-cavalry-2.png",
        "units/G-heavy-cavalry/blue-heavy-cavalry-2.png"
    ),
    G_3 (
        "Knight",
        new Unit (180, 80, 1, 1, 260),
        "units/G-heavy-cavalry/red-heavy-cavalry-3.png",
        "units/G-heavy-cavalry/blue-heavy-cavalry-3.png"
    ),
    // H - Heavy ranged
    H_1 (
        "Crossbowman",
        new Unit (50, 30, 2, 1, 75),
        "units/H-heavy-ranged/red-heavy-ranged-1.png",
        "units/H-heavy-ranged/blue-heavy-ranged-1.png"
    ),
    H_2 (
        "Armored crossbowman",
        new Unit (60, 35, 2, 1, 100),
        "units/H-heavy-ranged/red-heavy-ranged-2.png",
        "units/H-heavy-ranged/blue-heavy-ranged-2.png"
    ),
    H_3 (
        "Crossbow crusader",
        new Unit (70, 40, 2, 1, 120),
        "units/H-heavy-ranged/red-heavy-ranged-3.png",
        "units/H-heavy-ranged/blue-heavy-ranged-3.png"
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