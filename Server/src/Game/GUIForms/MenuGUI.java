package Game.GUIForms;

import Game.BuildingsGenerators.Base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGUI {
	
	private JPanel mainPanel;
	private JTabbedPane tabbedPane;
	private JPanel goldIncomePanel;
	private JPanel unitUpgradesPanel;
	private JLabel currentGoldIncomeLabel;
	private JButton closeButton;
	private JLabel currentGoldLabel;
	private JButton upgradeGoldIncomeButton;
	private JLabel costLabel;
	private JProgressBar powerbar;
	private JPanel powerbarPanel;
	private JLabel currentPBLabel;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JLabel currentAttackLabel;
	private JLabel currentHealthLabel;
	private JButton upgradeAttackButton;
	private JButton upgradeHealthButton;
	private JTextArea attackTextArea;
	private JTextArea healthTextArea;
	private JLabel costAttackLabel;
	private JLabel costHealthLabel;
	
	private final static String newline = "\n";
	
	private void errorMessage() {
		JOptionPane.showMessageDialog(null, "Not enough gold!", "Error",
			JOptionPane.ERROR_MESSAGE);
	}
	
	
	public MenuGUI(JDialog parentDialog, Base localBase) {
		
		JDialog menuWindow = new JDialog(parentDialog);
		
		menuWindow.setContentPane(mainPanel);
		menuWindow.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		menuWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		menuWindow.setSize(400, 300);
		menuWindow.setLocation(-menuWindow.getWidth() / 2, -menuWindow.getHeight() / 2);
		menuWindow.setLocationRelativeTo(mainPanel);
		
		//Setting up text
		currentGoldIncomeLabel.setText(String.format("Current Gold Income: +%s per turn", localBase.getGoldIncome()));
		currentGoldLabel.setText(String.format("Current Gold: %s", localBase.getGold()));
		costLabel.setText(String.format("Cost: %s gold", localBase.getGoldIncome() * 25 / 10));
		
		currentAttackLabel.setText(String.format("Current Attack Modifier: %sx", localBase.getAttackModifier()));
		currentHealthLabel.setText(String.format("Current Health Modifier: %sx", localBase.getHealthModifier()));
		attackTextArea.append("Attack modifier enhances" + newline + "damage dealt for all units." + newline +
			"It doesn't apply for already" + newline + "existing units.");
		healthTextArea.append("Health modifier enhances" + newline + "maximum health for all units." + newline +
			"It doesn't apply for already" + newline + "existing units.");
		costAttackLabel.setText(String.format("Cost: %s gold", localBase.getAttackUpgradeCost()));
		costHealthLabel.setText(String.format("Cost: %s gold", localBase.getHealthUpgradeCost()));
		
		powerbar.setValue(localBase.getPowerBarValue());
		currentPBLabel.setText(String.format("Power Bar Level: %s%% ", localBase.getPowerBarValue()));
		
		//Close button
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuWindow.dispose();
			}
		});
		
		//Upgrade gold income button
		upgradeGoldIncomeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(localBase.getGold() >= (localBase.getGoldIncome() * 25 / 10)) {
					
					//Changes value of gold and gold income after upgrading gold income
					localBase.setGold(localBase.getGold() - (localBase.getGoldIncome() * 25 / 10));
					localBase.setGoldIncome(localBase.getGoldIncome() + 10);
					
					//Message dialog
					JOptionPane.showMessageDialog(null, "Gold income upgraded!");
					
					//Refreshes text after upgrading
					currentGoldIncomeLabel.setText(String.format("Current Gold Income: +%s per turn",
						localBase.getGoldIncome()));
					currentGoldLabel.setText(String.format("Current Gold: %s", localBase.getGold()));
					costLabel.setText(String.format("Cost: %s gold", localBase.getGoldIncome() * 25 / 10));
				}
				else {
					errorMessage();
				}
			}
		});
		
		//Upgrade attack button
		upgradeAttackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(localBase.getGold() >= (localBase.getAttackUpgradeCost())) {
					
					//Changes value attack modifier and it's cost
					localBase.setGold(localBase.getGold() - (localBase.getAttackUpgradeCost()));
					localBase.setAttackModifier(localBase.getAttackModifier() + 0.5);
					localBase.setAttackUpgradeCost(localBase.getAttackUpgradeCost() + 40);
					
					//Message dialog
					JOptionPane.showMessageDialog(null, "Attack for your units upgraded!");
					
					//Refreshes text after upgrading
					costAttackLabel.setText(String.format("Cost: %s gold", localBase.getAttackUpgradeCost()));
					currentAttackLabel.setText(String.format("Current Attack Modifier: %sx", localBase.getAttackModifier()));
					
				}
				else {
					errorMessage();
				}
			}
		});
		
		//Upgrade health button
		upgradeHealthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(localBase.getGold() >= (localBase.getHealthUpgradeCost())) {
					
					//Changes value attack modifier and it's cost
					localBase.setGold(localBase.getGold() - (localBase.getHealthUpgradeCost()));
					localBase.setHealthModifier(localBase.getHealthModifier() + 0.5);
					localBase.setHealthUpgradeCost(localBase.getHealthUpgradeCost() + 40);
					
					//Message dialog
					JOptionPane.showMessageDialog(null, "Health for your units upgraded!");
					
					//Refreshes text after upgrading
					costHealthLabel.setText(String.format("Cost: %s gold", localBase.getHealthUpgradeCost()));
					currentHealthLabel.setText(String.format("Current Health Modifier: %sx", localBase.getHealthModifier()));
					
				}
				else {
					errorMessage();
				}
			}
		});
		
		menuWindow.setVisible(true);
	}
	
	public static void main(String[] args) {
		JDialog dialog = new JDialog();
		Base base = new Base(100, 1);
		new MenuGUI(dialog, base);
	}
}
