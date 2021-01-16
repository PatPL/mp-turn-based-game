package Game.GUIForms;

import Game.BuildingsGenerators.Base;
import Game.CustomElements.JImage;
import Game.Units.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class CreateNewUnitGUI {
	
	//GUI components
	private JPanel mainPanel;
	private JPanel bottomPanel;
	private JButton cancelButton;
	private JButton createUnitButton;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private JPanel swordsmanImagePanel;
	private JPanel archerImagePanel;
	private JPanel knightImagePanel;
	private JPanel knightTextPanel;
	private JPanel swordsmanTextPanel;
	private JPanel archerTextPanel;
	private JLabel swordsmanNameLabel;
	private JLabel knightNameLabel;
	private JLabel archerNameLabel;
	private JLabel swordsmanHealthLabel;
	private JLabel swordsmanDamageLabel;
	private JLabel swordsmanRangeLabel;
	private JLabel swordsmanSpeedLabel;
	private JLabel swordsmanCostLabel;
	private JLabel knightCostLabel;
	private JLabel knightSpeedLabel;
	private JLabel knightRangeLabel;
	private JLabel knightDamageLabel;
	private JLabel knightHealthLabel;
	private JLabel archerCostLabel;
	private JLabel archerSpeedLabel;
	private JLabel archerRangeLabel;
	private JLabel archerDamageLabel;
	private JLabel archerHealthLabel;
	
	private JImage swordsmanImage;
	private JImage archerImage;
	private JImage knightImage;
	
	private boolean canBuyUnit = false;
	
	private void createUIComponents() throws IOException {
		swordsmanImage = new JImage("sword2.png");
		archerImage = new JImage("bow1.png");
		knightImage = new JImage("knight1.png");
	}
	
	//Constructor
	public CreateNewUnitGUI(JDialog parentDialog, Base localBase) {
		JDialog gameWindow = new JDialog(parentDialog);
		
		gameWindow.setContentPane(mainPanel);
		gameWindow.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gameWindow.setSize(800, 820);
		gameWindow.setLocation(-gameWindow.getWidth() / 2, -gameWindow.getHeight() / 2);
		gameWindow.setLocationRelativeTo(mainPanel);

//		//Default units created set to be abstract
		Unit swordsman = new Unit(50, 20, 1, "Swordsman", 20, 1, localBase.getTeamNumber());
		Unit archer = new Unit(20, 10, 3, "Archer", 30, 1, localBase.getTeamNumber());
		Unit knight = new Unit(70, 30, 1, "Knight", 40, 1, localBase.getTeamNumber());
		
		//Cancel Button
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameWindow.dispose();
			}
		});
		
		//Swordsman image "button"
		swordsmanImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(localBase.getGold() >= swordsman.getCost()) {
					archerImagePanel.setBackground(null);
					knightImagePanel.setBackground(null);
					swordsmanImagePanel.setBackground(Color.YELLOW);
					createUnitButton.setEnabled(true);
				}
				else {
					JOptionPane.showMessageDialog(null, "Not enough gold!");
				}
			}
		});
		
		//Archer image "button"
		archerImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(localBase.getGold() >= archer.getCost()) {
					swordsmanImagePanel.setBackground(null);
					knightImagePanel.setBackground(null);
					archerImagePanel.setBackground(Color.YELLOW);
					createUnitButton.setEnabled(true);
				}
				else {
					JOptionPane.showMessageDialog(null, "Not enough gold!");
				}
			}
		});
		
		//Knight image "button"
		knightImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(localBase.getGold() >= knight.getCost()) {
					archerImagePanel.setBackground(null);
					swordsmanImagePanel.setBackground(null);
					knightImagePanel.setBackground(Color.YELLOW);
					createUnitButton.setEnabled(true);
				}
				else {
					JOptionPane.showMessageDialog(null, "Not enough gold!");
				}
			}
		});
		
		gameWindow.setVisible(true);

		//Create unit button
		createUnitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameWindow.dispose();
			}
		});
	}

	//For testing
	public static void main(String[] args) {
		JDialog dialog = new JDialog();
		Base base = new Base(100,1);
		base.setGold(20);
		new CreateNewUnitGUI(dialog, base);
	}
	
}
