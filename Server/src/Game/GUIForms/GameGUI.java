package Game.GUIForms;

import Game.CustomElements.JImage;
import Game.CustomElements.JMap;
import Game.Game;
import Webserver.enums.StatusType;
import common.HTTPClient;
import common.PlaySound;
import common.enums.Sounds;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameGUI {
    
    //GUI attributes
    private JPanel mainPanel;
    private JButton menuButton;
    private JButton endTurnButton;
    private JPanel bottomPanel;
    private JButton createUnitButton;
    private JPanel topPanel;
    private JLabel redCurrentHealthLabel;
    private JLabel blueCurrentHealthLabel;
    
    private JLabel redCurrentGoldLabel;
    private JLabel blueCurrentGoldLabel;
    private JLabel redGoldIncomeLabel;
    private JLabel blueGoldIncomeLabel;
    private JImage redHealthImageLabel;
    private JImage redGoldIconLabel;
    private JImage blueHealthImageLabel;
    private JImage blueGoldIconLabel;
    private JImage backgroundPanel;
    private JMap gameMapPanel;
    private JImage textImage;
    
    private final String gameCode;
    private final Game game;
    private final boolean isRedPlayer;
    
    private final JDialog parentDialog;
    private final Clip backgroundMusicClip;
    
    private void createUIComponents () throws IOException {
        redHealthImageLabel = new JImage ("redHeart.png");
        redGoldIconLabel = new JImage ("money2.png");
        blueHealthImageLabel = new JImage ("blueHeart.png");
        blueGoldIconLabel = new JImage ("money2.png");
        backgroundPanel = new JImage ("background.png", true);
        textImage = new JImage ("null64.png");
        gameMapPanel = new JMap ();
    }
    
    //Getters
    public JPanel getMainPanel () {
        return mainPanel;
    }
    
    //Refresh function, just like in car project
    public void refresh () {
        
        //For gold income
        redGoldIncomeLabel.setText ("(+" + game.getRedBase ().getGoldIncome () + ")");
        blueGoldIncomeLabel.setText ("(+" + game.getBlueBase ().getGoldIncome () + ")");
        
        //For current gold
        redCurrentGoldLabel.setText (String.valueOf (game.getRedBase ().getGold ()));
        blueCurrentGoldLabel.setText (String.valueOf (game.getBlueBase ().getGold ()));
        
        //For current health
        redCurrentHealthLabel.setText (String.valueOf (game.getRedBase ().getHealth ()));
        blueCurrentHealthLabel.setText (String.valueOf (game.getBlueBase ().getHealth ()));
        
        //Message dialog when player's turn begins
        if (!menuButton.isEnabled () && game.isLocalPlayerTurn () && !game.isGameOver ()) {
            JOptionPane.showMessageDialog (mainPanel, "Your turn!");
        }
        
        menuButton.setEnabled ((game.isLocalPlayerTurn ()));
        createUnitButton.setEnabled (game.isLocalPlayerTurn ());
        endTurnButton.setEnabled (game.isLocalPlayerTurn ());
        
        gameMapPanel.repaint ();
        
    }
    
    // Refreshes game state
    public void update () {
        HTTPClient.send ("/fetchGameState", gameCode, res -> {
            if (res.getStatusType () != StatusType.Success_2xx) {
                System.out.printf ("fetchGameState error: ", res.getBody ());
                return;
            }
            
            refreshUpdateInterval ();
            
            Game tmp = new Game ();
            tmp.deserialize (res.getBody (), 0);
            if (game.getServerWriteTimestamp () >= tmp.getServerWriteTimestamp ()) {
                // System.out.println("Warning: Same/older game state fetched. Ignoring...");
                return;
            }
            
            if (game.isLocalPlayerTurn ()) {
                System.out.println ("Warning: Attempted to overwrite local player's game state during his turn");
                return;
            }
            
            game.deserialize (res.getBody (), 0);
            refreshUpdateInterval ();
            refresh ();
            
            if (game.isGameOver ()) {
                boolean isRedWinner = game.isRedWinner ();
                boolean isLocalWinner = game.isRedWinner () == game.isPlayerRed ();
                JOptionPane.showMessageDialog (
                    mainPanel,
                    String.format ("Game over!\nWinner: %s.\nYou %s.", isRedWinner ? "RED" : "BLUE", isLocalWinner ? "won" : "lost"),
                    "Game over",
                    JOptionPane.INFORMATION_MESSAGE
                );
                parentDialog.dispose ();
            }
        });
    }
    
    public void endTurn () {
        refreshUpdateInterval ();
        HTTPClient.send (
            "/updateGameState",
            String.format ("%s;%s", gameCode, game.serialize ()),
            res -> {
                if (res.getStatusType () != StatusType.Success_2xx) {
                    JOptionPane.showMessageDialog (
                        mainPanel,
                        String.format ("End turn error: %s", res.getBody ()),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                game.setRedTurn (!game.isPlayerRed ()); // To allow client to overwrite its state with server's calculated state
                update ();
            }
        );
    }
    
    public void showCreateNewUnitForm () {
        new CreateNewUnitGUI (parentDialog, game, (unit, row) -> {
            if (!game.buyUnit (unit, row, game.getLocalBase ())) {
                JOptionPane.showMessageDialog (
                    mainPanel,
                    String.format ("Failed to buy a unit"),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            
            refresh ();
        });
    }
    
    public void showMenuGUI () {
        new MenuGUI (parentDialog, game.getLocalBase (), this::refresh);
    }
    
    // For testing
    private GameGUI (Game gameState) {
        JDialog gameWindow = new JDialog ((Dialog) null);
        parentDialog = gameWindow;
        gameWindow.setContentPane (mainPanel);
        gameWindow.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
        gameWindow.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        gameWindow.setSize (800, 600);
        gameWindow.setLocationRelativeTo (null);
        
        this.isRedPlayer = true;
        this.gameCode = "-";
        this.game = gameState;
        this.backgroundMusicClip = null;
        gameMapPanel.setGame (gameState);
        
        gameWindow.setVisible (true);
    }
    
    //Constructor
    public GameGUI (String gameCode, boolean isPlayerRed) {
        JDialog gameWindow = new JDialog ((Dialog) null);
        parentDialog = gameWindow;
        gameWindow.setContentPane (mainPanel);
        gameWindow.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
        gameWindow.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        gameWindow.setSize (1000, 625);
        gameWindow.setLocationRelativeTo (null);
        this.backgroundMusicClip = PlaySound.repeat (Sounds.backgroundMusic);
        
        this.isRedPlayer = isPlayerRed;
        this.gameCode = gameCode;
        this.game = new Game (0, 0, isPlayerRed);
        textImage.setImage (isPlayerRed ? "redText.png" : "blueText.png");
        gameMapPanel.setGame (this.game);
        gameMapPanel.setTopPanel (topPanel);
        
        //End turn button
        endTurnButton.addActionListener (e -> {
            PlaySound.once (Sounds.buttonPress);
            endTurn ();
        });
        
        //Create unit button
        createUnitButton.addActionListener (e -> {
            PlaySound.once (Sounds.buttonPress);
            showCreateNewUnitForm ();
        });
        
        //Menu button
        menuButton.addActionListener (e -> {
            PlaySound.once (Sounds.buttonPress);
            showMenuGUI ();
        });
        
        // "Easter egg" to test AIvAI fight
        redGoldIconLabel.addMouseListener (new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                // Applies ai2Turn thinking to current game state
                // (AI makes a move for you)
                if (game.isLocalPlayerTurn ()) {
                    System.out.println ("ai2Turn move applied");
                    game.ai2Turn ();
                    refresh ();
                }
            }
        });
        
        // Window close event for cleanup
        gameWindow.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                stopUpdateInterval ();
                backgroundMusicClip.stop ();
                backgroundMusicClip.flush ();
                backgroundMusicClip.close ();
            }
        });

//		// Doesn't work after another dialog was opened and closed. No idea why.
//		mainPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "onEnter");
//		mainPanel.getActionMap().put("onEnter", new AbstractAction() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// ENTER was pressed
//				if (game.isLocalPlayerTurn()) {
//					endTurn();
//				}
//			}
//		});
        
        // Always force start the first update interval to fetch the correct initial game state
        startUpdateInterval ();
        update ();
        
        gameWindow.setVisible (true);
    }
    
    // First function call after this many [ms]
    private long intervalStartDelay = 500;
    // Next function call after this many [ms]
    private long intervalDelay = 2000;
    private Timer updateInterval = null;
    
    private void stopUpdateInterval () {
        if (updateInterval != null) {
            updateInterval.cancel ();
            updateInterval = null;
        }
    }
    
    private void startUpdateInterval () {
        if (updateInterval == null) {
            updateInterval = new Timer ();
            updateInterval.schedule (new TimerTask () {
                @Override
                public void run () {
                    update ();
                }
            }, intervalStartDelay, intervalDelay);
        }
    }
    
    private void refreshUpdateInterval () {
        if (game.isLocalPlayerTurn () || game.isGameOver ()) {
            // Local player's turn. Don't ask for updates as there can be none
            // either that or the game is over, either way don't seek updates.
            stopUpdateInterval ();
        } else {
            startUpdateInterval ();
        }
    }
    
    public static void main (String[] args) {
        Game a = new Game ();
        a.deserialize ("2;8;0;0;0;1;0;0;1;0;0;0;2;0;0;1;0;0;0;1;0;0;2;0;0;0;3;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0" +
            ";0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0" +
            ";0;0;0;0;0;0;3;0;0;2;0;0;0;2;0;0;2;200;1;100;10;30;1.0;1.0;40;40;200;2;10;20;30;1.0;1.0;40;40;false;1610" +
            "828116491;false;", 0);
        
        new GameGUI (a);
    }
    
}