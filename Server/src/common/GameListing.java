package common;

import common.interfaces.ITextSerializable;

public class GameListing implements ITextSerializable {
    private String gameCode = null;
    private String gameName = null;
    private String gameHost = null;
    private int length = -1;
    private int height = -1;
    private int connectedPlayerCount = -1;
    private Boolean hasPassword = null;
    
    public String getGameCode () {
        return gameCode;
    }
    
    public String getGameName () {
        return gameName;
    }
    
    public String getGameHost () {
        return gameHost;
    }
    
    public int getLength () {
        return length;
    }
    
    public int getHeight () {
        return height;
    }
    
    public int getConnectedPlayerCount () {
        return connectedPlayerCount;
    }
    
    public Boolean getHasPassword () {
        return hasPassword;
    }
    
    public String getColumn (int col) {
        if (col == 0) {
            return (hasPassword ? "[\uD83D\uDD12] " : "") + this.gameName;
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
    
    public GameListing () {
    
    }
    
    public GameListing (
        String gameCode,
        String gameName,
        String gameHost,
        int length,
        int height,
        int connectedPlayerCount,
        Boolean hasPassword
    ) {
        this.gameCode = gameCode;
        this.gameName = gameName;
        this.gameHost = gameHost;
        this.length = length;
        this.height = height;
        this.connectedPlayerCount = connectedPlayerCount;
        this.hasPassword = hasPassword;
    }
    
    @Override
    public String serialize () {
        StringBuilder output = new StringBuilder ();
        
        output.append (gameCode);
        output.append (";");
        output.append (gameName);
        output.append (";");
        output.append (gameHost);
        output.append (";");
        output.append (length);
        output.append (";");
        output.append (height);
        output.append (";");
        output.append (connectedPlayerCount);
        output.append (";");
        output.append (hasPassword);
        output.append (";");
        
        return output.toString ();
    }
    
    @Override
    public int deserialize (String rawText, int offset) {
        int addedOffset = 0;
        String tmp;
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.gameCode = tmp;
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.gameName = tmp;
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.gameHost = tmp;
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.length = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.height = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.connectedPlayerCount = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.hasPassword = Boolean.parseBoolean (tmp);
        
        return addedOffset;
    }
}
