package GameServer;

import Game.Game;
import common.NewGameParams;
import common.Utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameLobby {
    
    private final static int defaultIDLength = 4;
    
    private final static Set<String> usedIDs = new HashSet<String> ();
    
    public final String ID;
    public final String host;
    public final long createdAt;
    public final int length;
    public final int height;
    public final String name;
    public final Map<String, Boolean> connectedPlayers = new HashMap<String, Boolean> ();
    public final Game game;
    public final boolean ai;
    public final boolean isPublic;
    public final String password;
    
    public GameLobby (String host, NewGameParams gameParams) {
        String newID;
        int IDLength = defaultIDLength;
        do {
            newID = Utility.getRandomString (IDLength);
            ++IDLength; // Avoid deadlocks in case most codes are used up.
        }
        while (usedIDs.contains (newID));
        
        usedIDs.add (newID);
        ID = newID;
        
        createdAt = System.currentTimeMillis ();
        this.host = host;
        this.length = gameParams.getLength ();
        this.height = gameParams.getHeight ();
        this.name = gameParams.getName ();
        this.ai = gameParams.isAi ();
        this.isPublic = gameParams.isPublic ();
        this.password = gameParams.getPassword ();
        
        this.game = new Game (height, length, false);
        this.game.setServerWriteTimestamp (createdAt);
    }
    
}
