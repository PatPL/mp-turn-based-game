package GameServer;

import common.Settings;
import common.Utility;
import common.enums.KeyEnum;

import javax.swing.*;

public class ServerGUI {
    private JPanel mainPanel;
    private JTable gameTable;
    private JList requestList;
    private JButton startButton;
    private JButton stopButton;
    private JTextField serverAddressInput;
    
    private GameServer server;
    
    public ServerGUI () {
        setupInitialValues ();
        setupListeners ();
    }
    
    private void startServer () {
        try {
            String[] tmp = serverAddressInput.getText ().split (":");
            GameServer server = new GameServer (tmp[0], Integer.parseInt (tmp[1]));
            this.server = server;
        } catch (Exception e) {
            
            return;
        }
        
        server.start ();
        
        serverAddressInput.setEnabled (false);
        startButton.setEnabled (false);
        stopButton.setEnabled (true);
    }
    
    private void stopServer () {
        server.stop ();
        server = null;
    
        serverAddressInput.setEnabled (true);
        startButton.setEnabled (true);
        stopButton.setEnabled (false);
    }
    
    private void setupInitialValues () {
        serverAddressInput.setText (Settings.getSetting (KeyEnum.serverGuiAddress));
    }
    
    private void setupListeners () {
        Utility.applyDocumentListener (serverAddressInput, newValue -> Settings.setSetting (KeyEnum.serverGuiAddress, newValue));
        startButton.addActionListener (e -> startServer ());
        stopButton.addActionListener (e -> stopServer ());
    }
    
    public static void main (String[] args) {
        JFrame frame = new JFrame ("ServerGUI");
        frame.setContentPane (new ServerGUI ().mainPanel);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.pack ();
        frame.setVisible (true);
    }
}
