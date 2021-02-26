package Client;

import Game.GUIForms.GameGUI;
import Webserver.enums.StatusType;
import common.HTTPClient;
import common.Utility;
import common.enums.KeyEnum;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientGUI {
    
    private final JFrame parentFrame;
    
    private JPanel panel1;
    private JTable table1;
    private JButton refreshGameListButton;
    private JButton hostGameButton;
    private JButton joinGameButton;
    private JButton settingsButton;
    private JTextField gameCodeInput;
    
    private final List<GameListing> games = new ArrayList<GameListing> ();
    
    public final static String[] gameListingCols = new String[] {"Name", "Code", "Host", "Size", "Players"};
    
    private class GameListing {
        String gameCode;
        String gameName;
        String gameHost;
        int length;
        int height;
        int connectedPlayerCount;
        
        public String getColumn (int col) {
            if (col == 0) {
                return this.gameName;
            } else if (col == 1) {
                return this.gameCode;
            } else if (col == 2) {
                return this.gameHost;
            } else if (col == 3) {
                return String.format ("%sx%s", length, height);
            } else if (col == 4) {
                return String.format ("%s/2", connectedPlayerCount);
            } else {
                return "";
            }
        }
        
    }
    
    private TableModel buildGameListTableModel () {
        return new TableModel () {
            @Override
            public int getRowCount () {
                return games.size ();
            }
            
            @Override
            public int getColumnCount () {
                return gameListingCols.length;
            }
            
            @Override
            public String getColumnName (int columnIndex) {
                return gameListingCols[columnIndex];
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
                return games.get (rowIndex).getColumn (columnIndex);
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
    
    public ClientGUI (JFrame parentFrame) {
        this.parentFrame = parentFrame;
        
        if (KeyEnum.userID == KeyEnum.nickname) { /* An expression to force the KeyEnum to load. */ }
        
        setupListeners ();
        startRefreshInterval ();
        refreshGameList ();
    }
    
    public static void main (String[] args) {
        JFrame frame = new JFrame ("ClientGUI");
        ClientGUI gui = new ClientGUI (frame);
        frame.setContentPane (gui.panel1);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.pack ();
        frame.setLocationRelativeTo (null);
        frame.setVisible (true);
    }
    
    private boolean isRefreshing = false;
    
    private void refreshGameList () {
        HTTPClient.send ("/gameList", "", res -> {
            isRefreshing = true;
            String[] lines = res.getBody ().split ("\n");
            int selectedRow = table1.getSelectedRow ();
            String selectedCode = selectedRow >= 0 ? games.get (selectedRow).gameCode : "";
            
            games.clear ();
            for (String line : lines) {
                GameListing newListing = new GameListing ();
                String[] parts = line.strip ().split (";");
                try {
                    newListing.gameCode = parts[0];
                    newListing.length = Integer.parseInt (parts[1]);
                    newListing.height = Integer.parseInt (parts[2]);
                    newListing.gameName = parts[3];
                    newListing.connectedPlayerCount = Integer.parseInt (parts[4]);
                    newListing.gameHost = parts[5];
                } catch (Exception e) {
                    continue;
                }
                games.add (newListing);
            }
            
            // table1.invalidate();
            // table1.repaint();
            
            // Redraw the table. The methods above didn't work, though they should've worked.
            table1.tableChanged (new TableModelEvent (table1.getModel ()));
            if (selectedRow >= 0 && selectedRow < table1.getModel ().getRowCount ()) {
                table1.setRowSelectionInterval (selectedRow, selectedRow);
            }
            
            // If the previously selected code was the selected one, not one typed in by user, refresh the code.
            // Because the selection could've changed, and the listener will ignore the change because of the isRefreshing
            // TODO: Test this once serverGUI is finished
            int newSelection = table1.getSelectedRow ();
            if (selectedCode == gameCodeInput.getText ()) {
                gameCodeInput.setText (newSelection >= 0 ? games.get (newSelection).gameCode : "");
            }
            
            isRefreshing = false;
        });
    }
    
    private void hostNewGame () {
        new HostGameFormGUI ((length, height, name, ai) -> {
            HTTPClient.send (
                "/addGame",
                String.format ("%s;%s;%s;%s", length, height, name, ai),
                res -> {
                    if (res.getStatusType () != StatusType.Success_2xx) {
                        JOptionPane.showMessageDialog (
                            this.panel1,
                            String.format ("Failed to create game: %s", res.getBody ()),
                            "Błąd",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }
                    
                    joinGame (res.getBody ());
                }
            );
        }, parentFrame);
    }
    
    private void joinSelectedGame () {
        joinGame (gameCodeInput.getText ());
    }
    
    private void openSettings () {
        new SettingsGUI (this.parentFrame);
    }
    
    private void joinGame (String gameCode) {
        HTTPClient.send ("/joinGame", gameCode, res -> {
            if (res.getStatusType () != StatusType.Success_2xx) {
                JOptionPane.showMessageDialog (
                    this.panel1,
                    String.format ("Failed to join game %s: %s", gameCode, res.getBody ()),
                    "Błąd",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            Boolean isPlayerRed = Boolean.parseBoolean (res.getBody ());
            
            // W tym miejscu serwer dołączył do odpowiedniej gry
            parentFrame.setVisible (false);
            stopRefreshInterval ();
            
            // A dialog with no parent shows on windows taskbar
            // As the parent windows hides itself anyway, it doesn't affect anything, and still works as expected
            new GameGUI (gameCode, isPlayerRed);
            
            parentFrame.setVisible (true);
            startRefreshInterval ();
            refreshGameList ();
        });
    }
    
    // Run at most once
    private boolean setupListenersCalled = false;
    
    private void setupListeners () {
        // Run at most once
        if (setupListenersCalled) {
            return;
        }
        setupListenersCalled = true;
        
        table1.setModel (buildGameListTableModel ());
        table1.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        table1.getSelectionModel ().addListSelectionListener (l -> {
            int selection = table1.getSelectedRow ();
            boolean validSelection = selection >= 0;
            if (!isRefreshing) {
                // Only set this text, if this listener didn't get triggered by refreshing the game list
                gameCodeInput.setText (validSelection ? games.get (selection).gameCode : "");
            }
            
        });
        
        Utility.applyDocumentListener (gameCodeInput, newValue -> {
            joinGameButton.setEnabled (!newValue.equals (""));
            if (!newValue.equals (newValue.toUpperCase ())) {
                SwingUtilities.invokeLater (() -> {
                    gameCodeInput.setText (newValue.toUpperCase ());
                });
            }
        });
        
        refreshGameListButton.addActionListener (l -> refreshGameList ());
        hostGameButton.addActionListener (l -> hostNewGame ());
        joinGameButton.addActionListener (l -> joinSelectedGame ());
        settingsButton.addActionListener (l -> openSettings ());
        
    }
    
    // First function call after this many [ms]
    private long intervalStartDelay = 1000;
    // Next function call after this many [ms]
    private long intervalDelay = 5000;
    private Timer refreshInterval = null;
    
    public void stopRefreshInterval () {
        if (refreshInterval != null) {
            refreshInterval.cancel ();
            refreshInterval = null;
        }
    }
    
    private void startRefreshInterval () {
        if (refreshInterval == null) {
            refreshInterval = new Timer ();
            refreshInterval.schedule (new TimerTask () {
                @Override
                public void run () {
                    // Do zrobienia: Zatrzymaj ten timer kiedy okno główne znika
                    refreshGameList ();
                }
            }, intervalStartDelay, intervalDelay);
        }
    }
    
}
