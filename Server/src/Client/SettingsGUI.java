package Client;

import common.Settings;
import common.enums.KeyEnum;

import javax.swing.*;

import static common.Utility.applyDocumentListener;

public class SettingsGUI {
    JDialog dialog;
    
    private JTextField nicknameInput;
    private JTextField serverAddressInput;
    private JPanel mainPanel;
    
    public SettingsGUI (JFrame parent) {
        JDialog dialog = new JDialog (parent);
        this.dialog = dialog;
        
        dialog.setContentPane (mainPanel);
        dialog.setTitle ("Settings");
        dialog.setAlwaysOnTop (true);
        dialog.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable (false);
        dialog.pack ();
        dialog.setLocation (-dialog.getWidth () / 2, -dialog.getHeight () / 2);
        dialog.setLocationRelativeTo (parent);
        
        setupListeners ();
        setupInputs ();
        
        dialog.setVisible (true);
    }
    
    private void setupListeners () {
        applyDocumentListener (nicknameInput, newValue -> {
            Settings.setSetting (KeyEnum.nickname, newValue);
        });
        
        applyDocumentListener (serverAddressInput, newValue -> {
            Settings.setSetting (KeyEnum.serverAddress, newValue);
            // Handle the address change here
        });
    }
    
    private void setupInputs () {
        nicknameInput.setText (Settings.getSetting (KeyEnum.nickname));
        serverAddressInput.setText (Settings.getSetting (KeyEnum.serverAddress));
    }
    
}
