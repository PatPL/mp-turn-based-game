package Game.GUIForms;

import Client.HTTPClient;
import Game.CustomElements.JImage;
import Game.CustomElements.JMap;
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
	private JButton menuButton;
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
	private JMap gameMapPanel;
	
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
		gameMapPanel = new JMap();
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
		
		if(!menuButton.isEnabled() && game.isLocalPlayerTurn()) {
			// This players turn begins now
		}
		menuButton.setEnabled((game.isLocalPlayerTurn()));
		createUnitButton.setEnabled(game.isLocalPlayerTurn());
		endTurnButton.setEnabled(game.isLocalPlayerTurn());
		
		gameMapPanel.repaint();
		
	}
	
	// Refreshes game state
	public void update() {
		HTTPClient.send("/fetchGameState", gameCode, res -> {
			if(res.getStatusType() != StatusType.Success_2xx) {
				System.out.printf("fetchGameState error: ", res.getBody());
				return;
			}
			
			refreshUpdateInterval();
			
			Game tmp = new Game();
			tmp.deserialize(res.getBody(), 0);
			if(game.getServerWriteTimestamp() >= tmp.getServerWriteTimestamp()) {
				// System.out.println("Warning: Same/older game state fetched. Ignoring...");
				return;
			}
			
			if(game.isLocalPlayerTurn()) {
				System.out.println("Warning: Attempted to overwrite local player's game state during his turn");
				return;
			}
			
			game.deserialize(res.getBody(), 0);
			refreshUpdateInterval();
			refresh();
		});
	}
	
	// For testing
	private GameGUI(Game gameState) {
		JDialog gameWindow = new JDialog((Dialog) null);
		parentDialog = gameWindow;
		gameWindow.setContentPane(mainPanel);
		gameWindow.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gameWindow.setSize(800, 600);
		gameWindow.setLocationRelativeTo(null);
		
		this.isRedPlayer = true;
		this.gameCode = "-";
		this.game = gameState;
		gameMapPanel.setGame(gameState);
		
		gameWindow.setVisible(true);
	}
	
	//Constructor
	public GameGUI(String gameCode, boolean isPlayerRed) {
		JDialog gameWindow = new JDialog((Dialog) null);
		parentDialog = gameWindow;
		gameWindow.setContentPane(mainPanel);
		gameWindow.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gameWindow.setSize(800, 600);
		gameWindow.setLocationRelativeTo(null);
		
		this.isRedPlayer = isPlayerRed;
		this.gameCode = gameCode;
		this.game = new Game(0, 0, isPlayerRed);
		gameMapPanel.setGame(this.game);
		
		// Initializing power bars
		
		bluePowerBar.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		
		//End turn button
		endTurnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.setRedTurn(!isPlayerRed);
				refreshUpdateInterval();
				HTTPClient.send(
					"/updateGameState",
					String.format("%s;%s", gameCode, game.serialize()),
					res -> {
						if(res.getStatusType() != StatusType.Success_2xx) {
							JOptionPane.showMessageDialog(
								mainPanel,
								String.format("Błąd końca tury: %s", res.getBody()),
								"Error",
								JOptionPane.ERROR_MESSAGE
							);
							return;
						}
					}
				);
			}
		});
		
		//Create unit button
		createUnitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateNewUnitGUI(parentDialog, game.getLocalBase());
			}
		});
		
		//Menu button
		menuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new MenuGUI(parentDialog, game.getLocalBase(), () -> refresh());
			}
		});
		
		// Always force start the fist update interval to fetch the correct initial game state
		startUpdateInterval();
		
		gameWindow.setVisible(true);
	}
	
	// First function call after this many [ms]
	private long intervalStartDelay = 500;
	// Next function call after this many [ms]
	private long intervalDelay = 2000;
	private Timer updateInterval = null;
	
	private void stopUpdateInterval() {
		if(updateInterval != null) {
			updateInterval.cancel();
			updateInterval = null;
		}
	}
	
	private void startUpdateInterval() {
		if(updateInterval == null) {
			updateInterval = new Timer();
			updateInterval.schedule(new TimerTask() {
				@Override
				public void run() {
					update();
				}
			}, intervalStartDelay, intervalDelay);
		}
	}
	
	private void refreshUpdateInterval() {
		if(game.isLocalPlayerTurn()) {
			// Local player's turn. Don't ask for updates as there can be none
			stopUpdateInterval();
		}
		else {
			startUpdateInterval();
		}
	}
	
	public static void main(String[] args) {
		Game a = new Game();
		a.deserialize("2;8;0;0;0;1;0;0;1;0;0;0;2;0;0;1;0;0;0;1;0;0;2;0;0;0;3;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0" +
			";0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0" +
			";0;0;0;0;0;0;3;0;0;2;0;0;0;2;0;0;2;200;1;100;10;30;1.0;1.0;40;40;200;2;10;20;30;1.0;1.0;40;40;false;1610" +
			"828116491;false;", 0);
		
		new GameGUI(a);
	}
	
}