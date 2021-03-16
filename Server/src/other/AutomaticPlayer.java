package other;

import Game.Game;
import Game.Units.UnitType;
import common.HTTPClient;
import common.NewGameParams;
import common.Utility;
import common.enums.KeyEnum;
import common.interfaces.IAction;

import java.util.Map;

import static common.Utility.randomRange;

public class AutomaticPlayer {
    
    private static int gameCounter = 0;
    
    private final String playerID = Utility.getRandomString (16);
    private final String playerName = String.format ("auto_%s", gameCounter);
    private final Game game = new Game (0, 0, true);
    private String gameCode = "<no game>";
    private final boolean logging;
    
    private void log (String message) {
        log (message, false);
    }
    
    private void log (String message, boolean forceLog) {
        if (logging || forceLog) {
            System.out.printf ("[%s@%s]: %s\n", playerName, gameCode, message);
        }
    }
    
    private void send (String URI, String body, HTTPClient.HTTPResponseHandler handler) {
        HTTPClient.send (URI, body, Map.of (KeyEnum.userID.key, playerID, KeyEnum.nickname.key, playerName), handler);
    }
    
    private void prepareGame (IAction onReady) {
        NewGameParams params = new NewGameParams (
            randomRange (4, 13),
            randomRange (1, 6),
            String.format ("automaticGame_%s", gameCounter++),
            true,
            true,
            ""
        );
        
        log ("Creating the game");
        send ("/addGame", params.serialize (), res1 -> {
            log (String.format ("Game [%s] created", res1.getBody ()));
            gameCode = res1.getBody ();
            send ("/joinGame", gameCode, res2 -> {
                log (String.format ("Joined game [%s]", gameCode));
                onReady.invoke ();
            });
        });
    }
    
    private void applyMove (IAction onGameOver) {
        // Bad pseudo-random AI
        int purpose = Utility.randomRange (0, 16);
        if ((purpose & 0b1000) != 0) {
            switch ((purpose & 0b0110) >> 1) {
                case 0:
                    // Do nothing
                    break;
                case 1:
                    game.getLocalBase ().upgradeGold ();
                    break;
                case 2:
                    game.getLocalBase ().upgradeHealth ();
                    break;
                case 3:
                    game.getLocalBase ().upgradeAttack ();
                    break;
            }
        }
        
        if ((purpose & 0b0001) != 0) {
            game.buyUnit (UnitType.B_1, Utility.randomRange (0, game.getRows ()), game.getLocalBase ());
        }
        
        // Your turn should take 2-8 seconds
        try {
            Thread.sleep (Utility.randomRange (2000, 10000));
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
        
        // Send game update
        if (game.isLocalPlayerTurn ()) {
            log ("Local player move applied. Sending update, and waiting for state update");
            send ("/updateGameState", String.format ("%s;%s", gameCode, game.serialize ()), res -> {
                game.setRedTurn (!game.isPlayerRed ());
                log ("Server state updated.");
                fetchGameState (() -> applyMove (onGameOver), onGameOver, 1);
            });
        } else {
            log ("Received recalculated game state. Waiting for the other player...");
            fetchGameState (() -> applyMove (onGameOver), onGameOver, 1);
        }
    }
    
    public AutomaticPlayer (IAction onGameOver, boolean logging) {
        if (KeyEnum.nickname.key.equals (KeyEnum.gamePassword.key)) { /* Initialize KeyEnum */ }
        
        // Player will connect to the server set in ClientGUI
        
        this.logging = logging;
        prepareGame (() -> fetchGameState (() -> applyMove (onGameOver), onGameOver, 1));
    }
    
    // AFAIK this can't be done with a lambda
    private static void infinitePlayer () {
        new AutomaticPlayer (AutomaticPlayer::infinitePlayer, false);
    }
    
    public static void main (String[] args) throws InterruptedException {
        long spoolupTimePerInstance = 500;
        int instances = 200;
        
        for (int i = 0; i < instances; ++i) {
            infinitePlayer ();
            System.out.printf ("||| Started %s/%s\n", i + 1, instances);
            Thread.sleep (spoolupTimePerInstance);
        }
        
        System.out.println ("||| All players spooled up");
    }
    
    private void fetchGameState (IAction onNewState, IAction onGameOver, int attempt) {
        send ("/fetchGameState", gameCode, res -> {
            log (String.format ("Fetch attempt: %s", attempt));
            Game tmp = new Game ();
            tmp.deserialize (res.getBody (), 0);
            if (game.getServerWriteTimestamp () >= tmp.getServerWriteTimestamp ()) {
                // Same/older game state
                log ("Stale server state. Retrying in 2 seconds...");
                try {
                    Thread.sleep (2000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                // Try again later
                fetchGameState (onNewState, onGameOver, attempt + 1);
                return;
            }
            
            if (game.isLocalPlayerTurn ()) {
                // Attempted to overwrite local game state
                log ("Attempted overwrite");
                System.out.println ("THIS SHOULDN'T HAPPEN (ATTEMPTED OVERWRITE)");
                return;
            }
            
            log ("Fetched fresh game state");
            game.deserialize (res.getBody (), 0);
            
            if (game.isGameOver ()) {
                log (String.format ("Game over. Won?: %s", game.isRedWinner () == game.isPlayerRed ()), true);
                onGameOver.invoke ();
            } else {
                onNewState.invoke ();
            }
            
        });
    }
    
}
