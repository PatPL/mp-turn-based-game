package common;

import common.interfaces.ITextSerializable;

public class NewGameParams implements ITextSerializable {
    
    private int length;
    private int height;
    private String name;
    private boolean ai;
    private boolean isPublic;
    private String password;
    
    public int getLength () {
        return length;
    }
    
    public int getHeight () {
        return height;
    }
    
    public String getName () {
        return name;
    }
    
    public boolean isAi () {
        return ai;
    }
    
    public boolean isPublic () {
        return isPublic;
    }
    
    public String getPassword () {
        return password;
    }
    
    public NewGameParams () {
        this (
            -1, -1, "[DEFAULT]", false, false, "NOT-SHA1-HASH"
        );
    }
    
    public NewGameParams (
        int length,
        int height,
        String name,
        boolean ai,
        boolean isPublic,
        String password
    ) {
        this.length = length;
        this.height = height;
        this.name = name;
        this.ai = ai;
        this.isPublic = isPublic;
        this.password = password;
    }
    
    @Override
    public String serialize () {
        StringBuilder output = new StringBuilder ();
        
        output.append (length);
        output.append (";");
        output.append (height);
        output.append (";");
        output.append (name);
        output.append (";");
        output.append (ai);
        output.append (";");
        output.append (isPublic);
        output.append (";");
        output.append (password);
        output.append (";");
        
        return output.toString ();
    }
    
    @Override
    public int deserialize (String rawText, int offset) {
        int addedOffset = 0;
        String tmp;
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.length = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.height = Integer.parseInt (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.name = tmp;
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.ai = Boolean.parseBoolean (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.isPublic = Boolean.parseBoolean (tmp);
        
        tmp = Utility.readUntil (rawText, ";", offset + addedOffset);
        addedOffset += tmp.length () + 1;
        this.password = tmp;
        
        return addedOffset;
    }
}
