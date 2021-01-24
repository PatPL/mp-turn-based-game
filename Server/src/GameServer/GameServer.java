package GameServer;

import Webserver.Request;
import Webserver.Response;
import Webserver.WebServer;
import Webserver.enums.Status;
import common.Utility;
import common.enums.KeyEnum;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameServer {
    
    private static final int defaultPort = 1234;
    private final static long idleGameLifetime = 30 * 60 * 1000; // 30 minutes
    private final static int gamePurgeInterval = 10 * 60 * 1000; // 10 minutes
    
    private WebServer currentWebServer = null;
    private final Map<String, GameLobby> gameList = new HashMap<String, GameLobby> ();
    private final Map<String, String> nicknameAssociation = new HashMap<String, String> ();
    
    private String getNickname (String userID) {
        return nicknameAssociation.getOrDefault (userID, "<unknown nickname>");
    }
    
    public GameServer (String address, int port) throws IOException {
        currentWebServer = buildServerObject (address, port);
    }
    
    public void start () {
        currentWebServer.start ();
    }
    
    public void stop () {
        currentWebServer.stop ();
    }
    
    public String addGame (
        String hostID,
        int length,
        int height,
        String name,
        boolean ai
    ) {
        GameLobby newGameLobby = new GameLobby (hostID, length, height, name, ai);
        gameList.put (newGameLobby.ID, newGameLobby);
        return newGameLobby.ID;
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
        
        String[] params = req.body.split (";");
        if (params.length != 4) {
            res.setStatus (Status.BadRequest_400);
            res.setBody ("Incorrect data in request body", Response.BodyType.Text);
            return true;
        }
        
        String gameID = null;
        try {
            gameID = addGame (
                req.headers.get (KeyEnum.userID.key),
                Integer.parseInt (params[0]),
                Integer.parseInt (params[1]),
                params[2],
                Boolean.parseBoolean (params[3])
            );
        } catch (NumberFormatException e) {
            res.setStatus (Status.BadRequest_400);
            res.setBody ("Error while parsing number data", Response.BodyType.Text);
            return true;
        }
        
        res.setStatus (Status.Created_201);
        res.setBody (gameID, Response.BodyType.Text);
        
        return true;
    }
    
    private boolean gameListHandler (Request req, Response res) {
        res.setStatus (Status.OK_200);
        
        StringBuilder gameListString = new StringBuilder ();
        for (Map.Entry<String, GameLobby> i : gameList.entrySet ()) {
            gameListString.append (i.getValue ().ID);
            gameListString.append (";");
            gameListString.append (i.getValue ().length);
            gameListString.append (";");
            gameListString.append (i.getValue ().height);
            gameListString.append (";");
            gameListString.append (i.getValue ().name);
            gameListString.append (";");
            gameListString.append (i.getValue ().connectedPlayers.size ());
            gameListString.append (";");
            gameListString.append (getNickname (i.getValue ().host));
            gameListString.append ("\r\n");
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
        String gameCode = req.body;
        GameLobby gameLobby = gameList.getOrDefault (gameCode, null);
        
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
        
        if (gameLobby.connectedPlayers.size () >= 2) {
            res.setStatus (Status.Forbidden_403);
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
        GameLobby gameLobby = gameList.getOrDefault (gameCode, null);
        
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
        GameLobby gameLobby = gameList.getOrDefault (gameCode, null);
        
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
        gameLobby.game.deserialize (req.body.substring (gameCode.length () + 1), 0);
        
        if (gameLobby.game.getServerWriteTimestamp () != previousWriteTimestamp) {
            System.out.println ("serverWriteTimestamp mismatched. Client side error?");
        }
        
        gameLobby.game.calculateTurn ();
        
        if (gameLobby.ai && !gameLobby.game.isGameOver ()) {
            // "Thinking" time so that a player has some time to see the result of his own turn
            // Handler runs on its own thread, so this doesn't block the server
            // Run after 1 second
            Timer timer = new Timer (1000, e -> {
                gameLobby.game.ai2Turn ();
                
                gameLobby.game.calculateTurn ();
                gameLobby.game.setServerWriteTimestamp (System.currentTimeMillis ());
            });
            timer.setRepeats (false);
            timer.start ();
        }
        
        gameLobby.game.setServerWriteTimestamp (System.currentTimeMillis ());
        if (gameLobby.game.isGameOver ()) {
            Timer timer = new Timer (20 * 1000, e -> {
                System.out.printf ("Removed finished game: %s\n", gameCode);
                gameList.remove (gameCode);
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
        for (Map.Entry<String, GameLobby> i : gameList.entrySet ()) {
            if (now >= i.getValue ().game.getServerWriteTimestamp () + idleGameLifetime) {
                toPurge.add (i.getKey ());
            }
        }
        
        if (toPurge.size () > 0) {
            System.out.printf ("Removed %s idle games:\n", toPurge.size ());
            for (String i : toPurge) {
                System.out.printf (" -%s\n", i);
                gameList.remove (i);
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
        new Timer (gamePurgeInterval, (e) -> server.purgeIdleGames ()).start ();
    }
    
}
