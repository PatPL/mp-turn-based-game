package Game;

import Game.BuildingsGenerators.Base;
import Game.Units.Unit;
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


	private boolean isGameOver;
	
	private long serverWriteTimestamp = 0;
	public long getServerWriteTimestamp() {
		return serverWriteTimestamp;
	}
	public void setServerWriteTimestamp(long newTimestamp) {
		serverWriteTimestamp = newTimestamp;
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
