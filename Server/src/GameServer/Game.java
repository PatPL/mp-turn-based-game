package GameServer;

import Webserver.Utility;

import java.util.HashSet;
import java.util.Set;

public class Game {
	
	private final static int defaultIDLength = 4;
	
	private final static Set<String> usedIDs = new HashSet<String>();
	
	public final String ID;
	public final String host;
	public final long createdAt;
	
	public Game(String host) {
		String newID;
		int IDLength = defaultIDLength;
		do {
			newID = Utility.getRandomString(IDLength);
			++IDLength; // Avoid deadlocks in case most codes are used up.
		}
		while(usedIDs.contains(newID));
		
		usedIDs.add(newID);
		ID = newID;
		
		createdAt = System.currentTimeMillis();
		this.host = host;
	}
	
}
