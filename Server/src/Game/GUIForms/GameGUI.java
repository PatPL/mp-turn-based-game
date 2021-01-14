package Game.GUIForms;

import Client.HTTPClient;
import Game.CustomElements.JImage;
import Game.Game;
import Webserver.enums.StatusType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameGUI {
	
	//GUI attributes
	private JPanel mainPanel;
	private JButton availableUpgradesButton;
	private JButton endTurnButton;
	private JPanel bottomPanel;
	private JButton createUnitButton;
	private JPanel topPanel;
	private JLabel redCurrentHealthLabel;
	private JLabel blueCurrentHealthLabel;
	
	private JLabel redCurrentGoldLabel;
	private JLabel blueCurrentGoldLabel;
	private JLabel redGoldIncomeLabel;
	private JLabel blueGoldIncomeLabel;
	private JLabel redPowerbarLabel;
	private JLabel bluePowerbarLabel;
	private JProgressBar redPowerBar;
	private JProgressBar bluePowerBar;
	private JImage redHealthImageLabel;
	private JImage redGoldIconLabel;
	private JImage blueHealthImageLabel;
	private JImage blueGoldIconLabel;
	private JImage backgroundPanel;
	
	private final String gameCode;
	private final Game game;
	private final boolean isRedPlayer;
	
	private final JDialog parentDialog;
	
	private void createUIComponents() throws IOException {
		redHealthImageLabel = new JImage("heart2.png");
		redGoldIconLabel = new JImage("money2.png");
		blueHealthImageLabel = new JImage("heart2.png");
		blueGoldIconLabel = new JImage("money2.png");
		backgroundPanel = new JImage("background.png", true);
	}
	
	//Getters
	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	//Refresh function, just like in car project
	public void refresh() {
		
		//For gold income
		redGoldIncomeLabel.setText("( +" + game.getRedBase().getGoldIncome() + " per turn)");
		blueGoldIncomeLabel.setText("( +" + game.getBlueBase().getGoldIncome() + " per turn)");
		
		//For current gold
		redCurrentGoldLabel.setText(game.getRedBase().getGold() + " ");
		blueCurrentGoldLabel.setText(game.getBlueBase().getGold() + " ");
		
		//For current health
		redCurrentHealthLabel.setText(game.getRedBase().getHealth() + "");
		blueCurrentHealthLabel.setText(game.getBlueBase().getHealth() + "");
		
		redPowerBar.setValue(game.getRedBase().getPowerBarValue());
		bluePowerBar.setValue(game.getBlueBase().getPowerBarValue());
		
	}
	
	// Refreshes game state
	public void update() {
		HTTPClient.send("/fetchGameState", gameCode, res -> {
			if(res.getStatusType() != StatusType.Success_2xx) {
				System.out.printf("fetchGameState error: ", res.getBody());
				return;
			}
			
			game.deserialize(res.getBody(), 0);
			refresh();
		});
	}

//	//Returns base from a team number
//	private Base whosTurnIs(int number) {
//		if(number == 1) return redBase;
//		return blueBase;
//	}
	
	
	//Run function to keep refreshing the values
//	public void run() {
//		while(true) {
//			refresh();
//			try {
//				Thread.sleep(200);
//			}
//			catch(InterruptedException e) {
//				return;
//			}
//		}
//	}
	
	
	//Constructor
	public GameGUI(String gameCode, JDialog parentDialog, boolean isPlayerRed) {
		this.isRedPlayer = isPlayerRed;
		this.gameCode = gameCode;
		this.game = new Game();
		this.parentDialog = parentDialog;
		
		// Initializing power bars
		
		bluePowerBar.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		
		//End turn button
//		endTurnButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				setEndTurn(true);
//			}
//		});
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// Do zrobienia: Zatrzymaj ten timer kiedy okno główne znika
				update();
			}
		}, 500, 2000);
		
		//Create unit button
		createUnitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateNewUnitGUI(parentDialog);
			}
		});
		
	}
}