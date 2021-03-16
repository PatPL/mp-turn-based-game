package GameServer;

import common.Settings;
import common.Utility;
import common.enums.KeyEnum;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class ServerGUI {
    private JPanel mainPanel;
    private JTable lobbyTable;
    private JButton startButton;
    private JButton stopButton;
    private JTextField serverAddressInput;
    private JButton TEMPORARY_refresh;
    
    private GameServer server;
    
    public ServerGUI () {
        setupModels ();
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
        
        // Perhaps move this initial setup to another method?
        server.onGameListUpdate.add (this::refreshLobbyTable);
        
        server.start ();
        
        serverAddressInput.setEnabled (false);
        startButton.setEnabled (false);
        stopButton.setEnabled (true);
    }
    
    private void stopServer () {
        server.stop ();
        server = null;
        
        refreshLobbyTable ();
        
        serverAddressInput.setEnabled (true);
        startButton.setEnabled (true);
        stopButton.setEnabled (false);
    }
    
    public void refreshLobbyTable () {
        lobbyTable.tableChanged (new TableModelEvent (lobbyTable.getModel ()));
    }
    
    private void setupModels () {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer ();
        centerRenderer.setHorizontalAlignment (SwingConstants.CENTER);
        
        lobbyTable.setModel (buildLobbyListTableModel ());
        lobbyTable.setDefaultRenderer (String.class, centerRenderer);
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
        frame.setSize (1000, 800);
        frame.setLocationRelativeTo (null);
        frame.setVisible (true);
    }
    
    //
    // JTable/JList Model factories
    //
    
    private final static String[] gameLobbiesCols = new String[] {"Name", "Code", "Size", "Players", "AI", "Unlisted", "Password", "Creation time"};
    
    private TableModel buildLobbyListTableModel () {
        return new TableModel () {
            @Override
            public int getRowCount () {
                return server == null ? 0 : server.getGameCount ();
            }
            
            @Override
            public int getColumnCount () {
                return gameLobbiesCols.length;
            }
            
            @Override
            public String getColumnName (int columnIndex) {
                return gameLobbiesCols[columnIndex];
            }
            
            @Override
            public Class getColumnClass (int columnIndex) {
                return String.class;
            }
            
            @Override
            public boolean isCellEditable (int rowIndex, int columnIndex) {
                return false;
            }
            
            @Override
            public Object getValueAt (int rowIndex, int columnIndex) {
                GameLobby game = server.getGame (rowIndex);
                switch (columnIndex) {
                    case 0:
                        return game.name;
                    case 1:
                        return game.ID;
                    case 2:
                        return String.format ("%sx%s", game.length, game.height);
                    case 3:
                        return String.format ("(%s): ", game.connectedPlayers.size ()).concat (
                            game.connectedPlayers.keySet ().stream ().reduce ("", (output, element) -> {
                                return output.concat (output.equals ("") ? "" : ";").concat (String.format ("%s [%s]", server.getNickname (element), element));
                            })
                        );
                    case 4:
                        return game.ai ? "✓" : "";
                    case 5:
                        return !game.isPublic ? "✓" : "";
                    case 6:
                        return !game.password.equals ("") ? "✓" : "";
                    case 7:
                        return Utility.epochMillisToString (game.createdAt);
                    default:
                        return "";
                }
            }
            
            @Override
            public void setValueAt (Object aValue, int rowIndex, int columnIndex) {
            
            }
            
            @Override
            public void addTableModelListener (TableModelListener l) {
            
            }
            
            @Override
            public void removeTableModelListener (TableModelListener l) {
            
            }
        };
    }
    
}
