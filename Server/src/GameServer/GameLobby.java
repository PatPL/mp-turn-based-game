package GameServer;

import Game.Game;
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
    
    public GameLobby (
        String host,
        int length,
        int height,
        String name,
        boolean ai,
        boolean isPublic
    ) {
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
        this.length = length;
        this.height = height;
        this.name = name;
        this.ai = ai;
        this.isPublic = isPublic;
        
        this.game = new Game (height, length, false);
        this.game.setServerWriteTimestamp (createdAt);
    }
    
}
