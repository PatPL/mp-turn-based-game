import BuildingsGenerators.Base;
import Units.Unit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI {
	
	//GUI Attributes
	private JPanel mainPanel;
	private JButton availableUpgradesButton;
	private JProgressBar progressBar;
	private JButton endTurnButton;
	private JPanel optionPanel;
	private JLabel currentGoldLabel;
	private JLabel goldValue;
	private JButton createUnitButton;
	private JPanel currentGoldPanel;
	private JPanel goldIncomePanel;
	private JLabel goldIncomeLabel;
	private JLabel goldIncomeValue;
	
	private boolean endTurn;
	
	//Getters
	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	private void setEndTurn(boolean endTurn) {
		this.endTurn = endTurn;
	}
	
	public boolean isEndTurn() {
		return endTurn;
	}
	
	//Constructor
	public GameGUI(){
		this.endTurn = false;
		
		endTurnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEndTurn(true);
			}
		});
	}
	
}