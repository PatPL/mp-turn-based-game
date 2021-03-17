package Game.GUIForms;

import Game.Base;
import common.PlaySound;
import common.enums.Sounds;
import common.interfaces.IAction;

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
    
    private void errorMessage () {
        JOptionPane.showMessageDialog (null, "Not enough gold!", "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public MenuGUI (JDialog parentDialog, Base localBase, IAction onUpdate) {
        
        JDialog menuWindow = new JDialog (parentDialog);
        
        menuWindow.setContentPane (mainPanel);
        menuWindow.setTitle ("Base upgrades");
        menuWindow.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
        menuWindow.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        menuWindow.setSize (400, 300);
        menuWindow.setLocation (-menuWindow.getWidth () / 2, -menuWindow.getHeight () / 2);
        menuWindow.setLocationRelativeTo (mainPanel);
        
        //Setting up text for gold pane
        currentGoldIncomeLabel.setText (String.format ("Current Gold Income: +%s per turn", localBase.getGoldIncome ()));
        currentGoldLabel.setText (String.format ("Current Gold: %s", localBase.getGold ()));
        costLabel.setText (String.format ("Cost: %s gold", localBase.getGoldIncome () * 25 / 10));
        
        //For attack and health pane
        currentAttackLabel.setText (String.format ("Current Attack Modifier: %sx", localBase.getAttackModifier ()));
        currentHealthLabel.setText (String.format ("Current Health Modifier: %sx", localBase.getHealthModifier ()));
        attackTextArea.append ("Attack modifier enhances" + newline + "damage dealt by new units." + newline +
            "It doesn't apply for already" + newline + "existing units.");
        healthTextArea.append ("Health modifier enhances" + newline + "maximum health of new units." + newline +
            "It doesn't apply for already" + newline + "existing units.");
        costAttackLabel.setText (String.format ("Cost: %s gold", localBase.getAttackUpgradeCost ()));
        costHealthLabel.setText (String.format ("Cost: %s gold", localBase.getHealthUpgradeCost ()));
        
        //Close button
        closeButton.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed (ActionEvent e) {
                PlaySound.once (Sounds.buttonPress);
                menuWindow.dispose ();
            }
        });
        
        //Upgrade gold income button
        upgradeGoldIncomeButton.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed (ActionEvent e) {
                PlaySound.once (Sounds.buttonPress);
                if (localBase.upgradeGold ()) {
                    //Message dialog
                    JOptionPane.showMessageDialog (mainPanel, "Gold income upgraded!");
                    
                    //Refreshes text after upgrading
                    currentGoldIncomeLabel.setText (String.format ("Current Gold Income: +%s per turn", localBase.getGoldIncome ()));
                    currentGoldLabel.setText (String.format ("Current Gold: %s", localBase.getGold ()));
                    costLabel.setText (String.format ("Cost: %s gold", localBase.getGoldIncome () * 25 / 10));
                    onUpdate.invoke ();
                } else {
                    errorMessage ();
                }
            }
        });
        
        //Upgrade attack button
        upgradeAttackButton.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed (ActionEvent e) {
                PlaySound.once (Sounds.buttonPress);
                if (localBase.upgradeAttack ()) {
                    //Message dialog
                    JOptionPane.showMessageDialog (mainPanel, "Attack of new units upgraded!");
                    
                    //Refreshes text after upgrading
                    costAttackLabel.setText (String.format ("Cost: %s gold", localBase.getAttackUpgradeCost ()));
                    currentAttackLabel.setText (String.format ("Current Attack Modifier: %sx", localBase.getAttackModifier ()));
                    onUpdate.invoke ();
                } else {
                    errorMessage ();
                }
            }
        });
        
        //Upgrade health button
        upgradeHealthButton.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed (ActionEvent e) {
                PlaySound.once (Sounds.buttonPress);
                if (localBase.upgradeHealth ()) {
                    //Message dialog
                    JOptionPane.showMessageDialog (mainPanel, "Health of new units upgraded!");
                    
                    //Refreshes text after upgrading
                    costHealthLabel.setText (String.format ("Cost: %s gold", localBase.getHealthUpgradeCost ()));
                    currentHealthLabel.setText (String.format ("Current Health Modifier: %sx", localBase.getHealthModifier ()));
                    onUpdate.invoke ();
                } else {
                    errorMessage ();
                }
            }
        });
        
        menuWindow.setVisible (true);
    }
    
    public static void main (String[] args) {
        JDialog dialog = new JDialog ();
        Base base = new Base (100, 1);
        base.setPowerBar (100);
        new MenuGUI (dialog, base, () -> {
        });
    }
}
