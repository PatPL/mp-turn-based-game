package Game;

import Game.BuildingsGenerators.Base;
import Game.Units.Unit;
import Game.Units.UnitType;
import Game.interfaces.ITextSerializable;
import Webserver.Utility;

public class Game implements ITextSerializable {
	
	//Neccessary parameters
	private int rows;
	
	public int getRows() {
		return rows;
	}
	
	private int columns;
	
	public int getColumns() {
		return columns;
	}
	
	private Unit[][] unitMap;
	
	public Unit getUnit(int x, int y) {
		if(x < 0 || x >= columns || y < 0 || y >= rows) {
			return null;
		}
		
		return unitMap[y][x];
	}
	
	public void setUnit(int x, int y, Unit unit) {
		if(x < 0 || x >= columns || y < 0 || y >= rows) {
			throw new IndexOutOfBoundsException();
		}
		
		unitMap[y][x] = unit;
	}
	
	public void removeUnit(int x, int y) {
		setUnit(x, y, new Unit());
	}
	
	private final int defaultBaseHealth = 200;
	
	private Base redBase;
	
	public Base getRedBase() {
		return redBase;
	}
	
	private Base blueBase;
	
	public Base getBlueBase() {
		return blueBase;
	}
	
	private final boolean isPlayerRed;
	
	public boolean isPlayerRed() {
		return isPlayerRed;
	}
	
	private boolean isRedTurn;
	
	public boolean isRedTurn() {
		return isRedTurn;
	}
	
	public void setRedTurn(boolean isRedTurn) {
		this.isRedTurn = isRedTurn;
	}
	
	public boolean isFieldInBase(int x) {
		return x == 0 || x == columns - 1;
	}
	
	public boolean isLocalPlayerTurn() {
		// Returns false for an uninitialized game (rows or cols == 0)
		return (isRedTurn == isPlayerRed) && (rows * columns != 0);
	}
	
	public Base getLocalBase() {
		if(isPlayerRed) {
			return redBase;
		}
		else {
			return blueBase;
		}
	}
	
	public Base getCurrentTurnBase() {
		return isRedTurn ? redBase : blueBase;
	}
	
	private boolean isGameOver;
	
	private long serverWriteTimestamp = 0;
	
	public long getServerWriteTimestamp() {
		return serverWriteTimestamp;
	}
	
	public void setServerWriteTimestamp(long newTimestamp) {
		serverWriteTimestamp = newTimestamp;
	}
	
	public boolean buyUnit(UnitType unit, int row, Base base) {
		if(unit.defaultUnit.getCost() > base.getGold()) {
			// Not enough gold
			return false;
		}
		
		int baseFieldX = isPlayerRed ? 0 : columns - 1;
		if(getUnit(baseFieldX, row).getTeam() != 0) {
			// There's already a unit there
			return false;
		}
		
		base.addGold(-unit.defaultUnit.getCost());
		setUnit(baseFieldX, row, new Unit(unit, base));
		return true;
	}
	
	//Constructor
	public Game(int newRows, int newColumns, boolean isPlayerRed) {
		this.rows = newRows;
		this.columns = newColumns;
		this.isPlayerRed = isPlayerRed;
		
		this.unitMap = new Unit[rows][columns];
		//Setting every unit on map to be like "not existing"
		for(int i = 0; i < rows; i++) {
			for(int k = 0; k < columns; k++) {
				unitMap[i][k] = new Unit();
			}
		}
		
		this.redBase = new Base(defaultBaseHealth, 1);
		this.blueBase = new Base(defaultBaseHealth, 2);
		
		this.isGameOver = false;
		this.isRedTurn = true;
	}
	
	public Game() {
		this(8, 2, true);
	}
	
	
	@Override
	public String serialize() {
		StringBuilder output = new StringBuilder();
		
		output.append(rows);
		output.append(";");
		output.append(columns);
		output.append(";");
		for(int i = 0; i < rows * columns; ++i) {
			output.append(unitMap[i / columns][i % columns].serialize());
		}
		
		output.append(redBase.serialize());
		output.append(blueBase.serialize());
		
		output.append(isGameOver);
		output.append(";");
		
		output.append(serverWriteTimestamp);
		output.append(";");
		
		output.append(isRedTurn);
		output.append(";");
		
		return output.toString();
	}
	
	public void checkUnitDeath(Unit unit) {
		if(unit.getHealth() <= 0) {
			Base oponentBase = unit.getTeam() == 1 ? blueBase : redBase;
			oponentBase.addPowerBar(unit.getCost() / 10);
			unit.die();
		}
	}
	
	public void checkBaseDeath(Base base) {
		if(base.getHealth() <= 0) {
			isGameOver = true;
		}
	}
	
	public void checkUnitDeath(Unit unit1, Unit unit2) {
		checkUnitDeath(unit1);
		checkUnitDeath(unit2);
	}
	
	private boolean unitAttack(int x, int y) {
		Unit attackingUnit = getUnit(x, y);
		int forward = attackingUnit.getTeam() == 1 ? 1 : -1;
		
		if(attackingUnit.getDamage() <= 0) {
			// This unit can't attack.
			// Stop here to avoid unnecessary counterattack damage
			return false;
		}
		
		for(int i = 1; i <= attackingUnit.getRange(); ++i) {
			if(isFieldInBase(x + i * forward)) {
				// Attack the base
				Base targetBase = x + i * forward == 0 ? redBase : blueBase;
				targetBase.addHealth(-attackingUnit.getDamage());
				checkBaseDeath(targetBase);
				return true;
			}
			
			Unit probableTarget = getUnit(x + i * forward, y);
			if(
				probableTarget != null &&
					probableTarget.getTeam() != 0 &&
					probableTarget.getTeam() != attackingUnit.getTeam()
			) {
				// Valid unit target in range
				if(probableTarget.getRange() >= i && probableTarget.getRange() == 1 && !isFieldInBase(x)) {
					// Valid target has the attacker in range, and can attack the attacker
					probableTarget.addHealth(-attackingUnit.getDamage());
					attackingUnit.addHealth(-probableTarget.getDamage());
					checkUnitDeath(attackingUnit, probableTarget);
					return true;
				}
				
				// Valid target can't reach the attacker, or can't fight back (range > 1)
				probableTarget.addHealth(-attackingUnit.getDamage());
				checkUnitDeath(probableTarget);
				return true;
			}
		}
		
		// No valid target in range.
		return false;
	}
	
	private boolean unitMove(int x, int y) {
		Unit movingUnit = getUnit(x, y);
		int forward = movingUnit.getTeam() == 1 ? 1 : -1;
		int i;
		for(i = 1; i <= movingUnit.getSpeed(); ++i) {
			Unit nextUnit = getUnit(x + i * forward, y);
			if(nextUnit != null && nextUnit.getTeam() == 0 && !isFieldInBase(x + i * forward)) {
				// Next unit is actually there, and it's empty (not a base field either)
				continue;
			}
			// Can't go there
			break;
		}
		// Next field is not accessible...
		--i; // so the previous field should still be
		
		if(i == 0) {
			// Can't move even a single field forward
			return false;
		}
		
		// Move i tiles forward
		setUnit(x + i * forward, y, movingUnit);
		removeUnit(x, y);
		return true;
	}
	
	public void aiTurn() {
		Base base = getLocalBase();
		// AI's turn here
		// Examples of available methods:
		// All purchase-related methods have their own checks in place, you don't need to check if you can afford an
		// upgrade before calling the method. It will return false, if for whatever reason you can't buy the thing.
		//
		// >Tries to buy a unit, and tries to place it on a given row.
		// >Returns true if successful
		// buyUnit(UnitType.swordsman, [0:(rows-1)], base);
		//
		// >Tries to upgrade an aspect of a base
		// >Returns true if successful
		// base.upgradeGold();
		// base.upgradeHealth();
		// base.upgradeAttack();
		//
		// >Returns a unit in a given spot
		// >Returns null, if this field doesn't exist
		// >Check unit's team with Unit.getTeam()
		// >Unit.getTeam() == base.getTeamNumber() <- Unit belongs to your team
		// >Unit.getTeam() != base.getTeamNumber() <- Unit doesn't belong to your team
		// >Unit.getTeam() == 0 <- Empty space with no unit on it
		// game.getUnit([0:(columns-1)], [0:(rows-1)]);
		
		// Example: AI will always try to upgrade its gold income. It won't do anything else
		base.upgradeGold();
	}
	
	public void calculateTurn() {
		Base base = getCurrentTurnBase();
		
		base.addGold(base.getGoldIncome());
		
		// Unit calculations
		for(int y = 0; y < rows; ++y) {
			// Red units calculate in this order <- : 87654321
			//    Blue ones the other way around -> : 12345678
			int unitUpdateDirection = isRedTurn ? -1 : 1;
			for(
				int x = isRedTurn ? columns - 1 : 0;
				isRedTurn ? x >= 0 : x < columns;
				x += unitUpdateDirection
			) {
				if(getUnit(x, y).getTeam() != base.getTeamNumber()) {
					// Not a unit belonging to this team
					continue;
				}
				
				if(!unitAttack(x, y)) {
					unitMove(x, y);
				}
				
			}
		}
		
		// Keep changing the turn as the last change
		isRedTurn ^= true;
	}
	
	@Override
	public int deserialize(String rawText, int offset) {
		int addedOffset = 0;
		String tmp;
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.rows = Integer.parseInt(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.columns = Integer.parseInt(tmp);
		
		this.unitMap = new Unit[rows][columns];
		for(int i = 0; i < rows * columns; ++i) {
			unitMap[i / columns][i % columns] = new Unit();
			addedOffset += unitMap[i / columns][i % columns].deserialize(rawText, offset + addedOffset);
		}
		
		redBase = new Base();
		addedOffset += redBase.deserialize(rawText, offset + addedOffset);
		
		blueBase = new Base();
		addedOffset += blueBase.deserialize(rawText, offset + addedOffset);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.isGameOver = Boolean.parseBoolean(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.serverWriteTimestamp = Long.parseLong(tmp);
		
		tmp = Utility.readUntil(rawText, ";", offset + addedOffset);
		addedOffset += tmp.length() + 1;
		this.isRedTurn = Boolean.parseBoolean(tmp);
		
		return addedOffset;
	}
}
