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
	
	public boolean isLocalPlayerTurn() {
		return isRedTurn == isPlayerRed;
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
		/* TEST */
		this.blueBase.setGold(35);
		this.isGameOver = false;
		this.isRedTurn = true;
	}
	
	public Game() {
		this(8, 2, true);
	}
	
	//Turn for one player
	private void turn(int whosTurn) {

//		//Pop up window for information
//		JOptionPane.showMessageDialog(null, "Player " + whosTurn + " turn");
//
//		//Initializing game window
//		JFrame frame = new JFrame("Game");
//		GameGUI gameGUI = new GameGUI();
//		frame.setContentPane(gameGUI.getMainPanel());
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setVisible(true);
//		frame.setSize(800, 600);
//		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//
//		//Keeps the player turn until End Turn button is pressed
//		while(!gameGUI.isEndTurn()) {
//
//			//Keeps refreshing current gold and other things
//			gameGUI.refresh();
//			try {
//				Thread.sleep(100);
//			}
//			catch(InterruptedException e) {
//				System.out.println("O, jakis blad.");
//			}
//		}
//		frame.dispose();
//
//		if(blueBase.getHealth() <= 0) {
//			JOptionPane.showMessageDialog(null, "Congratulations! Red team wins!");
//		}
//
//		if(redBase.getHealth() <= 0) {
//			JOptionPane.showMessageDialog(null, "Congratulations! Blue team wins!");
//		}
		
	}
	
	
	//Code execution
	public static void main(String[] args) {
		try {
			Game obj = new Game(3, 10, true);
			obj.run(args);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void run(String[] args) throws Exception {
		//Actual game
		while(!isGameOver) {
			
			//Turn-based game, remember?
			turn(1);
			if(isGameOver) break;
			turn(2);
		}
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
		
		return addedOffset;
	}
}
