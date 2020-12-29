package GameServer;

import Client.KeyEnum;
import Webserver.Request;
import Webserver.Response;
import Webserver.WebServer;
import Webserver.enums.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServer {
	
	private static final int defaultPort = 1234;
	
	private WebServer currentWebServer = null;
	private final List<Game> gameList = new ArrayList<Game>();
	private final Map<String, String> nicknameAssociation = new HashMap<String, String>();
	
	private String getNickname(String userID) {
		return nicknameAssociation.getOrDefault(userID, "<unknown nickname>");
	}
	
	public GameServer() throws IOException {
		currentWebServer = buildServerObject(defaultPort);
	}
	
	public void start() {
		currentWebServer.start();
	}
	
	public void stop() {
		currentWebServer.stop();
	}
	
	public String addGame(String hostID) {
		Game newGame = new Game(hostID);
		gameList.add(newGame);
		return newGame.ID;
	}
	
	private boolean authorize(Request req) {
		String userID = req.headers.getOrDefault(KeyEnum.userID.key, null);
		String nickname = req.headers.getOrDefault(KeyEnum.nickname.key, null);
		
		if(userID != null && nickname != null) {
			nicknameAssociation.put(userID, nickname);
		}
		return userID != null;
	}
	
	private boolean addGameHandler(Request req, Response res) {
		if(!authorize(req)) {
			res.setStatus(Status.Unauthorized_401);
			res.setBody("No userID provided", Response.BodyType.Text);
			return true;
		}
		
		res.setStatus(Status.Created_201);
		res.setBody(addGame(req.headers.get(KeyEnum.userID.key)), Response.BodyType.Text);
		
		return true;
	}
	
	public boolean gameListHandler(Request req, Response res) {
		res.setStatus(Status.OK_200);
		
		StringBuilder gameListString = new StringBuilder();
		for(Game i : gameList) {
			gameListString.append(i.ID);
			gameListString.append(",");
			gameListString.append(getNickname(i.host));
			gameListString.append("\r\n");
		}
		res.setBody(gameListString.toString(), Response.BodyType.Text);
		
		return true;
	}
	
	private WebServer buildServerObject(int port) throws IOException {
		WebServer output = new WebServer(port);
		
		output.addHandler("/addGame", this::addGameHandler);
		output.addHandler("/gameList", this::gameListHandler);
		
		return output;
	}
	
	public static void main(String[] args) {
		GameServer server;
		try {
			server = new GameServer();
		}
		catch(IOException e) {
			System.out.printf("Couldn't start the game server:\n%s\n", e);
			return;
		}
		
		server.start();
		System.out.printf("Game server listening at %s\n\n", server.currentWebServer.getAddress());
	}
	
}
