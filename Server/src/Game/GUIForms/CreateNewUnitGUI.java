package Game.GUIForms;

import Game.BuildingsGenerators.Base;
import Game.Units.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreateNewUnitGUI extends JFrame {
	
	//GUI components
	private JPanel mainPanel;
	private JPanel bottomPanel;
	private JButton cancelButton;
	private JButton createUnitButton;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private JLabel swordsmanImageLabel;
	private JLabel archerImageLabel;
	private JLabel knightImageLabel;
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
	
	private boolean canBuyUnit = false;
	
	
	//Constructorr
	public CreateNewUnitGUI(Base base) {
		
		//Default units created to be abstract
		Unit swordsman = new Unit(50, 20, 1, "Swordsman", 20, 1, base.getTeamNumber());
		Unit archer = new Unit(20, 10, 3, "Archer", 30, 1, base.getTeamNumber());
		Unit knight = new Unit(70, 30, 1, "Knight", 40, 1, base.getTeamNumber());
		
		setContentPane(mainPanel);
		
		//Cancel Button
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		//Swordsman image "button"
		swordsmanImageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(base.getGold() >= swordsman.getCost()) {
					archerImagePanel.setBackground(null);
					knightImagePanel.setBackground(null);
					swordsmanImagePanel.setBackground(Color.YELLOW);
				}
				else {
					JOptionPane.showMessageDialog(null, "Not enough gold!");
				}
			}
		});
		
		//Archer image "button"
		archerImageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(base.getGold() >= archer.getCost()) {
					swordsmanImagePanel.setBackground(null);
					knightImagePanel.setBackground(null);
					archerImagePanel.setBackground(Color.YELLOW);
				}
				else {
					JOptionPane.showMessageDialog(null, "Not enough gold!");
				}
			}
		});
		
		//Knight image "button"
		knightImageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(base.getGold() >= knight.getCost()) {
					archerImagePanel.setBackground(null);
					swordsmanImagePanel.setBackground(null);
					knightImagePanel.setBackground(Color.YELLOW);
				}
				else {
					JOptionPane.showMessageDialog(null, "Not enough gold!");
				}
			}
		});
		
		
	}
	
}
