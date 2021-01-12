package Game.GUIForms;

import Game.BuildingsGenerators.Base;
import Game.CustomElements.JImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
	
	private void createUIComponents() throws IOException {
		redHealthImageLabel = new JImage("heart2.png");
		redGoldIconLabel = new JImage("money2.png");
		blueHealthImageLabel = new JImage("heart2.png");
		blueGoldIconLabel = new JImage("money2.png");
		backgroundPanel = new JImage("background.png", true);
	}
	
	//Game attributes
	private boolean endTurn;
	private Base redBase;
	private Base blueBase;
	
	public boolean isEndTurn() {
		return endTurn;
	}
	
	//Getters
	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	//Setters
	private void setEndTurn(boolean endTurn) {
		this.endTurn = endTurn;
	}
	
	//Refresh function, just like in car project
	public void refresh() {
		
		//For gold income
		redGoldIncomeLabel.setText("( +" + redBase.getGoldIncome() + " per turn)");
		blueGoldIncomeLabel.setText("( +" + redBase.getGoldIncome() + " per turn)");
		
		//For current gold
		redCurrentGoldLabel.setText(redBase.getGold() + " ");
		blueCurrentGoldLabel.setText(blueBase.getGold() + " ");
		
		//For current health
		redCurrentHealthLabel.setText(redBase.getHealth() + "");
		blueCurrentHealthLabel.setText(blueBase.getHealth() + "");
		
		
	}
	
	//Returns base from a team number
	private Base whosTurnIs(int number) {
		if(number == 1) return redBase;
		return blueBase;
	}
	
	
	//Run function to keep refreshing the values
	public void run() {
		while(true) {
			refresh();
			try {
				Thread.sleep(200);
			}
			catch(InterruptedException e) {
				return;
			}
		}
	}
	
	
	//Constructor
	public GameGUI(Base red, Base blue, int whosTurn) {
		
		this.endTurn = false;
		redBase = red;
		blueBase = blue;
		
		// Initializing power bars
		redPowerBar.setValue(redBase.getPowerBarValue());
		bluePowerBar.setValue(blueBase.getPowerBarValue());
		bluePowerBar.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		
		//End turn button
		endTurnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEndTurn(true);
			}
		});
		
		
		//Create unit button
		createUnitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFrame f = new CreateNewUnitGUI(whosTurnIs(whosTurn));
				
				//Current screen resolution
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				
				//Setting up size and centering Create New Unit window
				f.setSize(486, 860);
				f.setPreferredSize(new Dimension(486, 860));
				f.setLocation((dim.width - f.getSize().width) / 2, (dim.height - f.getSize().height) / 2);
				
				f.pack();
				f.setVisible(true);
				
			}
		});
		
	}
}