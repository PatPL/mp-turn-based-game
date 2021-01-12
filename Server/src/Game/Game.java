package Game;

import Game.BuildingsGenerators.Base;
import Game.GUIForms.GameGUI;
import Game.Units.Unit;

import javax.swing.*;

public class Game {
	
	//Neccessary parameters
	private int rows;
	private int columns;
	private Unit[][] unitMap;
	
	private Base redBase;
	private Base blueBase;
	
	private boolean isGameOver;
	
	
	//Constructor
	private Game(int newRows, int newColumns, int baseHealth) {
		this.rows = newRows;
		this.columns = newColumns;
		this.unitMap = new Unit[rows][columns];
		this.redBase = new Base(baseHealth, 1);
		this.blueBase = new Base(baseHealth, 2);
		this.isGameOver = false;
	}
	
	//Turn for one player
	private void turn(int whosTurn) {
		
		//Pop up window for information
		JOptionPane.showMessageDialog(null, "Player " + whosTurn + " turn");
		
		//Initializing game window
		JFrame frame = new JFrame("Game");
		GameGUI gameGUI = new GameGUI(redBase, blueBase, whosTurn);
		frame.setContentPane(gameGUI.getMainPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(800, 600);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		//Keeps the player turn until End Turn button is pressed
		while(!gameGUI.isEndTurn()) {
			
			//Keeps refreshing current gold and other things
			gameGUI.refresh();
			try {
				Thread.sleep(100);
			}
			catch(InterruptedException e) {
				System.out.println("O, jakis blad.");
			}
		}
		frame.dispose();
		
		if(blueBase.getHealth() <= 0) {
			JOptionPane.showMessageDialog(null, "Congratulations! Red team wins!");
		}
		
		if(redBase.getHealth() <= 0) {
			JOptionPane.showMessageDialog(null, "Congratulations! Blue team wins!");
		}
		
	}
	
	
	//Code execution
	public static void main(String[] args) {
		try {
			Game obj = new Game(3, 10, 100);
			obj.run(args);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void run(String[] args) throws Exception {
		
		
		//Setting every unit on map to be like "not existing"
		for(int i = 0; i < rows; i++) {
			for(int k = 0; k < columns; k++) {
				unitMap[i][k] = new Unit();
			}
		}
		
		//Actual game
		while(!isGameOver) {
			
			//Turn-based game, remember?
			turn(1);
			if(isGameOver) break;
			turn(2);
		}
	}
}
