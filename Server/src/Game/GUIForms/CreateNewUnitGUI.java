package Game.GUIForms;

import Game.BuildingsGenerators.Base;
import Game.CustomElements.JImage;
import Game.CustomElements.JUnit;
import Game.Game;
import Game.Units.UnitType;
import Game.Utilities.PlaySound;
import Game.Utilities.Sounds;
import Game.interfaces.IAction;
import Game.interfaces.IDualProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class CreateNewUnitGUI {
	
	//GUI components
	private JPanel mainPanel;
	private JPanel bottomPanel;
	private JButton cancelButton;
	private JButton createUnitButton;
	private JPanel leftPanel;
	private JPanel positionPanel;
	
	private boolean canBuyUnit = false;
	
	//Constructor
	public CreateNewUnitGUI(JDialog parentDialog, Game game, IDualProvider<UnitType, Integer> onConfirm) {
		JDialog gameWindow = new JDialog(parentDialog);
		
		gameWindow.setContentPane(mainPanel);
		gameWindow.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gameWindow.setSize(game.getRows() == 1 ? 400 : 500, 500);
		gameWindow.setLocation(-gameWindow.getWidth() / 2, -gameWindow.getHeight() / 2);
		gameWindow.setLocationRelativeTo(mainPanel);
		
		Base base = game.getLocalBase();
		final UnitType[] selectedUnit = {null};
		final int[] selectedPanel = {-1};
		List<JUnit> jUnits = new ArrayList<JUnit>();
		List<JImage> panels = new ArrayList<JImage>();
		
		Color panelColor = game.isPlayerRed() ? Color.decode("#FF4444") : Color.decode("#6666FF");
		Color panelHoverColor = panelColor.brighter();
		Color panelSelectColor = panelColor.darker();
		Color panelDisabledColor = Color.decode("#666666");
		int baseFieldX = game.isPlayerRed() ? 0 : game.getColumns() - 1;
		IAction resetPanels = () -> {
			for(int j = 0; j < panels.size(); ++j) {
				if(game.getUnit(baseFieldX, j).getTeam() != 0) {
					// A unit is already there
					panels.get(j).setBackground(panelDisabledColor);
					panels.get(j).setImage(game.getUnit(baseFieldX, j).getImage());
					continue;
				}
				
				if(j == selectedPanel[0]) {
					// This is the selected panel
					if(selectedUnit[0] != null) {
						// Draw selected unit
						panels.get(j).setImage(game.isPlayerRed() ? selectedUnit[0].redImage : selectedUnit[0].blueImage);
					}
					else {
						panels.get(j).setImage("null64.png");
					}
					
					panels.get(j).setBackground(panelSelectColor);
				}
				else {
					// Other not selected panels
					panels.get(j).setImage("null64.png");
					panels.get(j).setBackground(panelColor);
				}
			}
		};
		// Row choice panels
		for(int i = 0; i < game.getRows(); ++i) {
			JImage panel = null;
			try {
				panel = new JImage("null64.png", 96, 96);
			}
			catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			
			// panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
			panels.add(panel);
			positionPanel.add(panel);
			
			if(game.getUnit(baseFieldX, i).getTeam() != 0) {
				// A unit is already there
				panel.setBackground(panelDisabledColor);
				panel.setImage(game.getUnit(baseFieldX, i).getImage());
				continue;
			}
			
			if(selectedPanel[0] < 0) {
				selectedPanel[0] = i;
			}
			panel.setBackground(i == selectedPanel[0] ? panelSelectColor : panelColor);
			
			int finalI = i;
			JImage finalPanel = panel;
			panel.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(selectedPanel[0] != finalI) {
						PlaySound.playSound(Sounds.buttonPress);
					}
					selectedPanel[0] = finalI;
					resetPanels.invoke();
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
				}
				
				@Override
				public void mouseReleased(MouseEvent e) {
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					if(selectedPanel[0] != finalI) {
						finalPanel.setBackground(panelHoverColor);
					}
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					resetPanels.invoke();
				}
			});
		}
		
		if(game.getRows() == 1) {
			positionPanel.setVisible(false);
		}
		
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		IAction resetJUnits = () -> {
			for(JUnit i : jUnits) {
				if(i.getUnitType() == selectedUnit[0]) {
					continue;
				}
				
				i.reset();
			}
		};
		
		for(UnitType i : UnitType.values()) {
			if(i == UnitType.empty) {
				continue;
			}
			
			JUnit jUnit = new JUnit(i, base, () -> {
				if(base.getGold() < i.defaultUnit.getCost()) {
					JOptionPane.showMessageDialog(null, "Not enough gold!");
					return false;
				}
				selectedUnit[0] = i;
				createUnitButton.setEnabled(true);
				resetJUnits.invoke();
				resetPanels.invoke();
				return true;
			});
			leftPanel.add(jUnit.getMainPanel());
			jUnits.add(jUnit);
		}
		
		//Cancel Button
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlaySound.playSound(Sounds.buttonPress);
				gameWindow.dispose();
			}
		});

//		//Knight image "button"
//		knightImage.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				super.mouseClicked(e);
//				PlaySound.playSound(Sounds.buttonPress);
//				if(game.getLocalBase().getGold() >= knight.getCost()) {
//					archerImagePanel.setBackground(null);
//					swordsmanImagePanel.setBackground(null);
//					knightImagePanel.setBackground(Color.YELLOW);
//					createUnitButton.setEnabled(true);
//					selectedUnit[0] = UnitType.knight;
//					resetPanels.invoke();
//				}
//				else {
//					JOptionPane.showMessageDialog(null, "Not enough gold!");
//				}
//			}
//		});
		
		//Create unit button
		createUnitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlaySound.playSound(Sounds.buttonPress);
				if(selectedUnit[0] == null) {
					JOptionPane.showMessageDialog(null, "No unit has been selected", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(selectedPanel[0] < 0) {
					JOptionPane.showMessageDialog(null, "No free space to place a unit", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				onConfirm.invoke(selectedUnit[0], selectedPanel[0]);
				gameWindow.dispose();
			}
		});
		
		gameWindow.setVisible(true);
	}
	
	//For testing
	public static void main(String[] args) {
		JDialog dialog = new JDialog();
		new CreateNewUnitGUI(dialog, new Game(3, 8, false), (value1, value2) -> {
			System.out.printf("Choice: %s, %s\n", value1.name, value2);
		});
	}
	
}
