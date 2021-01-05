import BuildingsGenerators.Base;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI {
	
	private JPanel mainPanel;
	private JButton availableUpgradesButton;
	private JProgressBar progressBar;
	private JButton endTurnButton;
	private JPanel optionPanel;
	private JLabel currentGoldLabel;
	private JLabel goldLabel;
	private JButton createUnitButton;
	private JPanel currentGoldPanel;
	
	
	//Getters
	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	//Constructor
	public GameGUI(Base base) {
		
		//Settings for progressBar
		progressBar.setString("Power Bar");
		progressBar.setValue(base.getPowerBar());
		progressBar.setStringPainted(true);
	}
	
}
