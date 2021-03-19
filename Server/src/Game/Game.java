package Game;

import Game.Units.Unit;
import Game.Units.UnitType;
import common.Utility;
import common.interfaces.ITextSerializable;

import java.util.*;

public class Game implements ITextSerializable {
    
    //Neccessary parameters
    private int rows;
    
    public int getRows () {
        return rows;
    }
    
    private int columns;
    
    public int getColumns () {
        return columns;
    }
    
    // two-dimensional array of units
    private Unit[][] unitMap;
    
    public Unit getUnit (int x, int y) {
        if (x < 0 || x >= columns || y < 0 || y >= rows) {
            return null;
        }
        
        return unitMap[y][x];
    }
    
    public void setUnit (int x, int y, Unit unit) {
        if (x < 0 || x >= columns || y < 0 || y >= rows) {
            throw new IndexOutOfBoundsException ();
        }
        
        unitMap[y][x] = unit;
    }
    
    public void removeUnit (int x, int y) {
        setUnit (x, y, new Unit ());
    }
    
    private final int defaultBaseHealth = 200;
    
    private Base redBase;
    
    public Base getRedBase () {
        return redBase;
    }
    
    private Base blueBase;
    
    public Base getBlueBase () {
        return blueBase;
    }
    
    private final boolean isPlayerRed;
    
    public boolean isPlayerRed () {
        return isPlayerRed;
    }
    
    private boolean isRedTurn;
    
    public boolean isRedTurn () {
        return isRedTurn;
    }
    
    public void setRedTurn (boolean isRedTurn) {
        this.isRedTurn = isRedTurn;
    }
    
    public boolean isFieldInBase (int x) {
        return x == 0 || x == columns - 1;
    }
    
    public boolean isLocalPlayerTurn () {
        // Returns false for an uninitialized game (rows or cols == 0)
        return (isRedTurn == isPlayerRed) && (rows * columns != 0);
    }
    
    public Base getLocalBase () {
        if (isPlayerRed) {
            return redBase;
        } else {
            return blueBase;
        }
    }
    
    public Base getCurrentTurnBase () {
        return isRedTurn ? redBase : blueBase;
    }
    
    private boolean isGameOver;
    
    public boolean isGameOver () {
        return this.isGameOver;
    }
    
    public boolean isRedWinner () {
        // Returns nonsense, if isGameOver == false
        return blueBase.getHealth () <= 0;
    }
    
    private long serverWriteTimestamp = 0;
    
    public long getServerWriteTimestamp () {
        return serverWriteTimestamp;
    }
    
    public void setServerWriteTimestamp (long newTimestamp) {
        serverWriteTimestamp = newTimestamp;
    }
    
    public boolean buyUnit (UnitType unit, int row, Base base) {
        if (unit.defaultUnit.getCost () > base.getGold ()) {
            // Not enough gold
            return false;
        }
        
        if (row >= rows) {
            // This field doesn't exits
            return false;
        }
        
        int baseFieldX = isPlayerRed ? 0 : columns - 1;
        if (getUnit (baseFieldX, row).getTeam () != 0) {
            // There's already a unit there
            return false;
        }
        
        base.addGold (-unit.defaultUnit.getCost ());
        setUnit (baseFieldX, row, new Unit (unit, base));
        return true;
    }
    
    //Constructor
    public Game (int newRows, int newColumns, boolean isPlayerRed) {
        this.rows = newRows;
        this.columns = newColumns;
        this.isPlayerRed = isPlayerRed;
        
        this.unitMap = new Unit[rows][columns];
        //Setting every unit on map to be like "not existing"
        for (int i = 0; i < rows; i++) {
            for (int k = 0; k < columns; k++) {
                unitMap[i][k] = new Unit ();
            }
        }
        
        this.redBase = new Base (defaultBaseHealth, 1);
        this.blueBase = new Base (defaultBaseHealth, 2);
        
        this.isGameOver = false;
        this.isRedTurn = true;
    }
    
    public Game () {
        this (8, 2, true);
    }
    
    @Override
    public String serialize () {
        StringBuilder output = new StringBuilder ();
        
        output.append (rows);
        output.append (";");
        output.append (columns);
        output.append (";");
        for (int i = 0; i < rows * columns; ++i) {
            output.append (unitMap[i / columns][i % columns].serialize ());
        }
        
        output.append (redBase.serialize ());
        output.append (blueBase.serialize ());
        
        output.append (isGameOver);
        output.append (";");
        
        output.append (serverWriteTimestamp);
        output.append (";");
        
        output.append (isRedTurn);
        output.append (";");
        
        return output.toString ();
    }
    
    private boolean checkUnitDeath (Unit unit) {
        if (unit.getHealth () <= 0) {
            Base opponentBase = unit.getTeam () == 1 ? blueBase : redBase;
            unit.die ();
            return true;
        }
        
        return false;
    }
    
    private boolean checkUnitDeath (Unit unit1, Unit unit2) {
        // IMPORTANT:
        // Use '|' operator instead of '||'.
        // '||' evaluates only the first expression, if it returns true. 2nd method may not be called.
        // '|' always evaluates the entire thing.
        // Check here: https://www.online-java.com/4JZlDjQo1W
        return checkUnitDeath (unit1) | checkUnitDeath (unit2);
    }
    
    private void checkBaseDeath (Base base) {
        if (base.getHealth () <= 0) {
            isGameOver = true;
        }
    }
    
    private boolean unitAttack (int x, int y) {
        Unit attackingUnit = getUnit (x, y);
        int forward = attackingUnit.getTeam () == 1 ? 1 : -1;
        
        if (attackingUnit.getDamage () <= 0) {
            // This unit can't attack.
            // Stop here to avoid unnecessary counterattack damage
            return false;
        }
        
        for (int i = 1; i <= attackingUnit.getRange (); ++i) {
            if (isFieldInBase (x + i * forward)) {
                // Attack the base
                Base targetBase = x + i * forward == 0 ? redBase : blueBase;
                targetBase.addHealth (-attackingUnit.getDamage ());
                checkBaseDeath (targetBase);
                return true;
            }
            
            Unit probableTarget = getUnit (x + i * forward, y);
            if (
                probableTarget != null &&
                    probableTarget.getTeam () != 0 &&
                    probableTarget.getTeam () != attackingUnit.getTeam ()
            ) {
                // Valid unit target in range
                if (probableTarget.getRange () >= i && probableTarget.getRange () == 1 && !isFieldInBase (x)) {
                    // Valid target has the attacker in range, and can attack the attacker
                    probableTarget.addHealth (-attackingUnit.getDamage ());
                    attackingUnit.addHealth (-probableTarget.getDamage ());
                    // Attack with a killing blow counts as no attack at all, so that the attacking unit
                    // (if it survived) can move after a lethal attack
                    return !checkUnitDeath (attackingUnit, probableTarget);
                }
                
                // Valid target can't reach the attacker, or can't fight back (range > 1)
                probableTarget.addHealth (-attackingUnit.getDamage ());
                // Attack with a killing blow counts as no attack at all, so that the attacking unit
                // (if it survived) can move after a lethal attack
                return !checkUnitDeath (probableTarget);
            }
        }
        
        // No valid target in range.
        return false;
    }
    
    private int unitMove (int x, int y) {
        Unit movingUnit = getUnit (x, y);
        
        if (movingUnit.getTeam () == 0) {
            // Empty unit tried moving
            return 0;
        }
        
        int forward = movingUnit.getTeam () == 1 ? 1 : -1;
        int i;
        for (i = 1; i <= movingUnit.getSpeed (); ++i) {
            Unit nextUnit = getUnit (x + i * forward, y);
            if (nextUnit != null && nextUnit.getTeam () == 0 && !isFieldInBase (x + i * forward)) {
                // Next unit is actually there, and it's empty (not a base field either)
                continue;
            }
            // Can't go there
            break;
        }
        // Next field is not accessible...
        --i; // so the previous field should still be
        
        if (i == 0) {
            // Can't move even a single field forward
            return 0;
        }
        
        // Move i tiles forward
        setUnit (x + i * forward, y, movingUnit);
        removeUnit (x, y);
        return i;
    }
    
    public void ai2Turn () {
        Base base = getLocalBase ();
        int forward = base.getTeamNumber () == 1 ? 1 : -1;
        int baseX = base.getTeamNumber () == 1 ? 0 : columns - 1;
        // First of all, the AI will always try to buy 2 income upgrades, and then one health and one attack upgrade
        // It's an optimal strategy regardless of anything else.
        
        if (base.getGoldIncome () < 30) {
            // (ROUND 1)
            // Buy 2 upgrades, always
            base.upgradeGold ();
            base.upgradeGold ();
            return;
        }
        
        if (base.getAttackModifier () < 1.5) {
            // (ROUND 2)
            // Always purchase the first attack upgrade
            base.upgradeAttack ();
            return;
        }
        
        if (base.getHealthModifier () < 1.5) {
            // (ROUND 3)
            // Always purchase the first health upgrade
            base.upgradeHealth ();
            return;
        }
        
        // (ROUND 4), leaves earlygame
        // We're left off with 35 gold. Now start the regular strategy
        // Strategy: Spawn units to exceed oponent's health value in each lane.
        int[] healthAdvantage = new int[rows];
        for (int y = 0; y < rows; ++y) {
            // AI's inner will to overwhelm the enemy by 1HP/lane
            healthAdvantage[y] = -1;
            for (int x = 0; x < columns; ++x) {
                Unit unit = getUnit (x, y);
                if (unit.getTeam () == 0) {
                    // Empty field
                    continue;
                }
                
                // Static HP value per enemy unit to prioritize attacked fields
                healthAdvantage[y] += unit.getTeam () == base.getTeamNumber () ? 0 : -10;
                healthAdvantage[y] += unit.getHealth () * (unit.getTeam () == base.getTeamNumber () ? 1 : -1);
            }
        }
        
        int totalRowLoss = 0;
        List<Integer> lossRows = new ArrayList<Integer> ();
        for (int i = 0; i < rows; ++i) {
            if (healthAdvantage[i] >= 0) {
                continue;
            }
            
            if (getUnit (baseX, i).getTeam () != 0) {
                // Field is not empty. Can't buy a unit there, so ignore the lane
                continue;
            }
            
            totalRowLoss -= healthAdvantage[i];
            lossRows.add (i);
        }
        lossRows.sort (Comparator.comparingInt (o -> -healthAdvantage[o]));
        
        // Boost to ranged unit's usefullness, if it would be spawed right behind another unit
        double rangedUsefulnessBoost = 1.6;
        // Units available to AI sorted by usefulness
        UnitType[] units = UnitType.values ();
        Arrays.sort (units, (a, b) -> b.defaultUnit.getCost () - a.defaultUnit.getCost ());
        
        int totalBudget = base.getGold ();
        for (int i : lossRows) {
            int lineBudget = (totalBudget * -healthAdvantage[i] / totalRowLoss) / 10;
            lineBudget *= 10;
            
            UnitType chosenUnit = UnitType.empty;
            int maxUsefulness = 0;
            for (UnitType j : units) {
                if (j.defaultUnit.getCost () > lineBudget) {
                    continue;
                }
                
                int usefulness = j.defaultUnit.getCost ();
                if (j.defaultUnit.getRange () > 1) {
                    Unit forwardUnit1 = getUnit (baseX + forward, i);
                    Unit forwardUnit2 = getUnit (baseX + forward * 2, i);
                    if (
                        (forwardUnit1.getTeam () == base.getTeamNumber () && forwardUnit1.getRange () == 1) ||
                            (forwardUnit2.getTeam () == base.getTeamNumber () && forwardUnit2.getRange () == 1)
                    ) {
                        // Send ranged units when they would spawn behind a friendly melee unit
                        usefulness *= rangedUsefulnessBoost;
                    } else {
                        // Don't send ranged units first to their death
                        usefulness /= rangedUsefulnessBoost;
                    }
                }
                
                if (usefulness > maxUsefulness) {
                    maxUsefulness = usefulness;
                    chosenUnit = j;
                }
            }
            
            if (chosenUnit != UnitType.empty) {
                buyUnit (chosenUnit, i, base);
            }
            
            totalRowLoss += healthAdvantage[i];
            totalBudget = base.getGold ();
        }
        
        // Try to upgrade the income, if possible. It's always worth it in the long run
        base.upgradeGold ();
        
        // Upgrade attack & health, but leave some breathing room to prioritize income upgrades, even if more expensive
        if (base.getGold () >= base.getAttackUpgradeCost () * 1.4) {
            base.upgradeAttack ();
        }
        
        if (base.getGold () >= base.getHealthUpgradeCost () * 1.6) {
            base.upgradeHealth ();
        }
    }
    
    public void calculateTurn () {
        Base base = getCurrentTurnBase ();
        
        base.addGold (base.getGoldIncome ());
        
        Set<Integer> performedNonLethalAttack = new HashSet<Integer> ();
        for (int y = 0; y < rows; ++y) {
            // Unit calculations - Attacks
            // Back-most units try to attack first
            // Red units calculate in this order -> : 12345678
            //    Blue ones the other way around <- : 87654321
            int unitUpdateDirection = !isRedTurn ? -1 : 1;
            performedNonLethalAttack.clear ();
            for (
                int x = !isRedTurn ? columns - 1 : 0;
                !isRedTurn ? x >= 0 : x < columns;
                x += unitUpdateDirection
            ) {
                if (getUnit (x, y).getTeam () != base.getTeamNumber ()) {
                    // Not a unit belonging to this team
                    continue;
                }
                
                if (unitAttack (x, y)) {
                    performedNonLethalAttack.add (x);
                }
                
            }
            
            // Unit calculations - Movement
            // Red units calculate in this order <- : 87654321
            //    Blue ones the other way around -> : 12345678
            unitUpdateDirection *= -1;
            for (
                int x = isRedTurn ? columns - 1 : 0;
                isRedTurn ? x >= 0 : x < columns;
                x += unitUpdateDirection
            ) {
                if (getUnit (x, y).getTeam () != base.getTeamNumber ()) {
                    // Not a unit belonging to this team
                    continue;
                }
                
                if (performedNonLethalAttack.contains (x)) {
                    // This unit attacked, and its target survived. This unit is not allowed to move forward
                    continue;
                }
                
                unitMove (x, y);
            }
        }
        
        // Keep changing the turn as the last change
        isRedTurn ^= true;
    }
    
    @Override
    public int deserialize (String rawText, int offset) {
        int addedOffset = 0;
        String tmp;
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.rows = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.columns = Integer.parseInt (tmp);
        
        this.unitMap = new Unit[rows][columns];
        for (int i = 0; i < rows * columns; ++i) {
            unitMap[i / columns][i % columns] = new Unit ();
            addedOffset += unitMap[i / columns][i % columns].deserialize (rawText, offset + addedOffset);
        }
        
        redBase = new Base ();
        addedOffset += redBase.deserialize (rawText, offset + addedOffset);
        
        blueBase = new Base ();
        addedOffset += blueBase.deserialize (rawText, offset + addedOffset);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.isGameOver = Boolean.parseBoolean (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.serverWriteTimestamp = Long.parseLong (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.isRedTurn = Boolean.parseBoolean (tmp);
        
        return addedOffset;
    }
}
