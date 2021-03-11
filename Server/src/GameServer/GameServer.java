package GameServer;

import Webserver.Request;
import Webserver.Response;
import Webserver.WebServer;
import Webserver.enums.Status;
import common.GameListing;
import common.NewGameParams;
import common.Utility;
import common.enums.KeyEnum;
import common.interfaces.IAction;

import javax.swing.*;
import javax.swing.Timer;
import java.io.IOException;
import java.util.*;

public class GameServer {
    
    private static final int defaultPort = 1234;
    private final static long idleGameLifetime = 30 * 60 * 1000; // 30 minutes
    private final static int gamePurgeInterval = 10 * 60 * 1000; // 10 minutes
    private Timer gamePurgeTimer;
    
    private WebServer currentWebServer = null;
    private final List<GameLobby> gameList = new ArrayList<GameLobby> ();
    private final Map<String, GameLobby> gameMap = new HashMap<String, GameLobby> ();
    private final Map<String, String> nicknameAssociation = new HashMap<String, String> ();
    
    public List<IAction> onGameListUpdate = new ArrayList<IAction> ();
    
    private void sendGameListUpdateEvents () {
        for (IAction i : onGameListUpdate) {
            i.invoke ();
        }
    }
    
    public String getNickname (String userID) {
        if (userID == "-") {
            return "<AI>";
        }
        
        return nicknameAssociation.getOrDefault (userID, "<unknown nickname>");
    }
    
    public GameServer (String address, int port) throws IOException {
        currentWebServer = buildServerObject (address, port);
    }
    
    public void start () {
        currentWebServer.start ();
        if (gamePurgeTimer == null) {
            gamePurgeTimer = new Timer (gamePurgeInterval, (e) -> purgeIdleGames ());
            gamePurgeTimer.start ();
        }
    }
    
    public void stop () {
        currentWebServer.stop ();
        if (gamePurgeTimer != null) {
            gamePurgeTimer.stop ();
            gamePurgeTimer = null;
        }
    }
    
    public int getGameCount () {
        return gameList.size ();
    }
    
    public GameLobby getGame (int index) {
        return gameList.get (index);
    }
    
    public GameLobby getGame (String id) {
        return gameMap.getOrDefault (id, null);
    }
    
    public String addGame (
        String hostID,
        NewGameParams gameParams
    ) {
        GameLobby newGameLobby = new GameLobby (hostID, gameParams);
        
        gameMap.put (newGameLobby.ID, newGameLobby);
        gameList.add (newGameLobby);
        
        sendGameListUpdateEvents ();
        
        return newGameLobby.ID;
    }
    
    public void removeGame (String id) {
        if (!gameMap.containsKey (id)) {
            // There is no game with this code
            return;
        }
        
        gameList.remove (gameMap.remove (id));
    
        sendGameListUpdateEvents ();
    }
    
    private boolean authorize (Request req) {
        String userID = req.headers.getOrDefault (KeyEnum.userID.key, null);
        String nickname = req.headers.getOrDefault (KeyEnum.nickname.key, null);
        
        if (userID != null && nickname != null) {
            nicknameAssociation.put (userID, nickname);
        }
        return userID != null;
    }
    
    private boolean addGameHandler (Request req, Response res) {
        if (!authorize (req)) {
            res.setStatus (Status.Unauthorized_401);
            res.setBody ("No userID provided", Response.BodyType.Text);
            return true;
        }
        
        NewGameParams gameParams = new NewGameParams ();
        gameParams.deserialize (req.body, 0);
        
        String gameID = addGame (req.headers.get (KeyEnum.userID.key), gameParams);
        
        res.setStatus (Status.Created_201);
        res.setBody (gameID, Response.BodyType.Text);
        
        return true;
    }
    
    private boolean gameListHandler (Request req, Response res) {
        res.setStatus (Status.OK_200);
        
        StringBuilder gameListString = new StringBuilder ();
        for (Map.Entry<String, GameLobby> i : gameMap.entrySet ()) {
            if (!i.getValue ().isPublic) {
                continue;
            }
            
            GameLobby lobby = i.getValue ();
            gameListString.append (new GameListing (
                lobby.ID,
                lobby.name,
                getNickname (lobby.host),
                lobby.length,
                lobby.height,
                lobby.connectedPlayers.size (),
                !lobby.password.equals ("")
            ).serialize ());
            
        }
        res.setBody (gameListString.toString (), Response.BodyType.Text);
        
        return true;
    }
    
    private boolean joinGameHandler (Request req, Response res) {
        if (!authorize (req)) {
            res.setStatus (Status.Unauthorized_401);
            res.setBody ("No userID provided", Response.BodyType.Text);
            return true;
        }
        
        String userID = req.headers.get (KeyEnum.userID.key);
        String sentPassword = req.headers.getOrDefault (KeyEnum.gamePassword.key, null);
        String gameCode = req.body;
        GameLobby gameLobby = gameMap.getOrDefault (gameCode, null);
        
        if (gameLobby == null) {
            res.setStatus (Status.NotFound_404);
            res.setBody (String.format ("Game with code %s doesn't exist", gameCode), Response.BodyType.Text);
            return true;
        }
        
        if (gameLobby.connectedPlayers.containsKey (userID)) {
            res.setStatus (Status.OK_200);
            res.setBody (gameLobby.connectedPlayers.get (userID).toString (), Response.BodyType.Text);
            return true;
        }
        
        if (!gameLobby.password.equals ("") && !gameLobby.password.equals (sentPassword)) {
            // Invalid or no password.
            res.setStatus (Status.Forbidden_403);
            res.setBody (String.format ("Invalid or no password to game %s", gameCode), Response.BodyType.Text);
            return true;
        }
        
        if (gameLobby.connectedPlayers.size () >= 2) {
            res.setStatus (Status.Conflict_409);
            res.setBody (String.format ("Game with code %s is full", gameCode), Response.BodyType.Text);
            return true;
        }
        
        Boolean isPlayerRed = (gameLobby.connectedPlayers.size () == 0);
        
        res.setStatus (Status.OK_200);
        res.setBody (isPlayerRed.toString (), Response.BodyType.Text);
        gameLobby.connectedPlayers.put (userID, isPlayerRed);
        
        if (gameLobby.ai) {
            if (gameLobby.connectedPlayers.size () != 1) {
                System.out.println ("ERROR: This shouldn't ever happen");
                return true;
            }
            
            gameLobby.connectedPlayers.put ("-", false);
        }
        
        // New player successfully joined
        sendGameListUpdateEvents ();
        
        return true;
    }
    
    private boolean fetchGameStateHandler (Request req, Response res) {
        if (!authorize (req)) {
            res.setStatus (Status.Unauthorized_401);
            res.setBody ("No userID provided", Response.BodyType.Text);
            return true;
        }
        
        String userID = req.headers.get (KeyEnum.userID.key);
        String gameCode = req.body;
        GameLobby gameLobby = gameMap.getOrDefault (gameCode, null);
        
        if (gameLobby == null) {
            res.setStatus (Status.NotFound_404);
            res.setBody (String.format ("Game with code %s doesn't exist", gameCode), Response.BodyType.Text);
            return true;
        }
        
        if (!gameLobby.connectedPlayers.containsKey (userID)) {
            res.setStatus (Status.Forbidden_403);
            res.setBody (String.format ("You're not in game %s", gameCode), Response.BodyType.Text);
            return true;
        }
        
        res.setStatus (Status.OK_200);
        res.setBody (gameLobby.game.serialize (), Response.BodyType.Text);
        return true;
    }
    
    private boolean updateGameStateHandler (Request req, Response res) {
        if (!authorize (req)) {
            res.setStatus (Status.Unauthorized_401);
            res.setBody ("No userID provided", Response.BodyType.Text);
            return true;
        }
        
        String userID = req.headers.get (KeyEnum.userID.key);
        String gameCode = Utility.readUntil (req.body, ";", 0);
        GameLobby gameLobby = gameMap.getOrDefault (gameCode, null);
        
        if (gameLobby == null) {
            res.setStatus (Status.NotFound_404);
            res.setBody (String.format ("Game with code %s doesn't exist", gameCode), Response.BodyType.Text);
            return true;
        }
        
        if (!gameLobby.connectedPlayers.containsKey (userID)) {
            res.setStatus (Status.Forbidden_403);
            res.setBody (String.format ("You're not in game %s", gameCode), Response.BodyType.Text);
            return true;
        }
        
        boolean isRequestorRed = gameLobby.connectedPlayers.get (userID);
        boolean isCurrentMoveRed = gameLobby.game.isRedTurn ();
        long previousWriteTimestamp = gameLobby.game.getServerWriteTimestamp ();
        
        if (isCurrentMoveRed != isRequestorRed) {
            // Player attempted to send game state update, while it's not his turn
            res.setStatus (Status.Forbidden_403);
            res.setBody (String.format ("You can't send game update to %s, as it's not your turn", gameCode), Response.BodyType.Text);
            return true;
        }
        
        if (gameLobby.game.isGameOver ()) {
            // Player attempted to send game state update, while it's not his turn
            res.setStatus (Status.Gone_410);
            res.setBody (String.format ("You can't send game update to %s. The game is over", gameCode), Response.BodyType.Text);
            return true;
        }
        
        res.setStatus (Status.OK_200);
        res.setBody ("-", Response.BodyType.Text);
        gameLobby.game.deserialize (req.body.substring (gameCode.length () + 1), 0);
        
        if (gameLobby.game.getServerWriteTimestamp () != previousWriteTimestamp) {
            System.out.println ("serverWriteTimestamp mismatched. Client side error?");
        }
        
        IAction processTurn = () -> {
            gameLobby.game.calculateTurn ();
            gameLobby.game.setServerWriteTimestamp (System.currentTimeMillis ());
            if (gameLobby.game.isGameOver ()) {
                Timer timer = new Timer (20 * 1000, e -> {
                    System.out.printf ("Removed finished game: %s\n", gameCode);
                    removeGame (gameCode);
                });
                timer.setRepeats (false);
                timer.start ();
            }
        };
    
        processTurn.invoke ();
        
        if (gameLobby.ai && !gameLobby.game.isGameOver ()) {
            // "Thinking" time so that a player has some time to see the result of his own turn
            // Handler runs on its own thread, so this doesn't block the server
            // Run after 1 second
            Timer timer = new Timer (1000, e -> {
                gameLobby.game.ai2Turn ();
    
                processTurn.invoke ();
            });
            timer.setRepeats (false);
            timer.start ();
        }
        
        
        
        return true;
    }
    
    private WebServer buildServerObject (String address, int port) throws IOException {
        WebServer output = new WebServer (address, port);
        
        output.addHandler ("/addGame", this::addGameHandler);
        output.addHandler ("/gameList", this::gameListHandler);
        output.addHandler ("/joinGame", this::joinGameHandler);
        output.addHandler ("/fetchGameState", this::fetchGameStateHandler);
        output.addHandler ("/updateGameState", this::updateGameStateHandler);
        
        return output;
    }
    
    private void purgeIdleGames () {
        // Removes all games, which didn't have an update in [idleGameLifetime]ms
        Set<String> toPurge = new HashSet<String> ();
        long now = System.currentTimeMillis ();
        for (Map.Entry<String, GameLobby> i : gameMap.entrySet ()) {
            if (now >= i.getValue ().game.getServerWriteTimestamp () + idleGameLifetime) {
                toPurge.add (i.getKey ());
            }
        }
        
        if (toPurge.size () > 0) {
            System.out.printf ("Removed %s idle games:\n", toPurge.size ());
            for (String i : toPurge) {
                System.out.printf (" -%s\n", i);
                removeGame (i);
            }
            System.out.println (" ");
        }
    }
    
    public static void main (String[] args) {
        String address = "127.0.0.1";
        int port = defaultPort;
        
        if (args.length >= 1) {
            String[] tmp = args[0].split (":", 2);
            if (tmp.length == 2) {
                try {
                    int tmpPort = Integer.parseInt (tmp[1]);
                    address = tmp[0];
                    port = tmpPort;
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
        } else {
            System.out.println ("[server] ip:port - start server with custom ip/port");
            System.out.println ("Example: 'java GameServer 192.168.0.1:22222'");
            System.out.println (" ");
        }
        
        GameServer server;
        try {
            server = new GameServer (address, port);
        } catch (IOException e) {
            System.out.printf ("Couldn't start the game server:\n%s\n", e);
            return;
        }
        
        server.start ();
        System.out.printf ("Game server listening at %s\n\n", server.currentWebServer.getAddress ());
    }
    
}
