package GameServer;

import Client.KeyEnum;
import Webserver.Request;
import Webserver.Response;
import Webserver.WebServer;
import Webserver.enums.Status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameServer {
	
	private static final int defaultPort = 1234;
	
	private WebServer currentWebServer = null;
	private final Map<String, GameLobby> gameList = new HashMap<String, GameLobby>();
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
	
	public String addGame(
		String hostID,
		int length,
		int height,
		String name
	) {
		GameLobby newGameLobby = new GameLobby(hostID, length, height, name);
		gameList.put(newGameLobby.ID, newGameLobby);
		return newGameLobby.ID;
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
		
		String[] params = req.body.split(";");
		if(params.length != 3) {
			res.setStatus(Status.BadRequest_400);
			res.setBody("Incorrect data in request body", Response.BodyType.Text);
			return true;
		}
		
		String gameID = null;
		try {
			gameID = addGame(
				req.headers.get(KeyEnum.userID.key),
				Integer.parseInt(params[0]),
				Integer.parseInt(params[1]),
				params[2]
			);
		}
		catch(NumberFormatException e) {
			res.setStatus(Status.BadRequest_400);
			res.setBody("Error while parsing number data", Response.BodyType.Text);
			return true;
		}
		
		res.setStatus(Status.Created_201);
		res.setBody(gameID, Response.BodyType.Text);
		
		return true;
	}
	
	public boolean gameListHandler(Request req, Response res) {
		res.setStatus(Status.OK_200);
		
		StringBuilder gameListString = new StringBuilder();
		for(Map.Entry<String, GameLobby> i : gameList.entrySet()) {
			gameListString.append(i.getValue().ID);
			gameListString.append(";");
			gameListString.append(i.getValue().length);
			gameListString.append(";");
			gameListString.append(i.getValue().height);
			gameListString.append(";");
			gameListString.append(i.getValue().name);
			gameListString.append(";");
			gameListString.append(i.getValue().connectedPlayers.size());
			gameListString.append("\r\n");
			
			System.out.printf("%s: %s\n", i.getValue().ID, i.getValue().connectedPlayers.size());
		}
		res.setBody(gameListString.toString(), Response.BodyType.Text);
		
		return true;
	}
	
	public boolean joinGameHandler(Request req, Response res) {
		if(!authorize(req)) {
			res.setStatus(Status.Unauthorized_401);
			res.setBody("No userID provided", Response.BodyType.Text);
			return true;
		}
		
		String userID = req.headers.get(KeyEnum.userID.key);
		String gameCode = req.body;
		GameLobby gameLobby = gameList.getOrDefault(gameCode, null);
		
		if(gameLobby == null) {
			res.setStatus(Status.NotFound_404);
			res.setBody(String.format("Game with code %s doesn't exist", gameCode), Response.BodyType.Text);
			return true;
		}
		
		if(gameLobby.connectedPlayers.containsKey(userID)) {
			res.setStatus(Status.OK_200);
			res.setBody(gameLobby.connectedPlayers.get(userID).toString(), Response.BodyType.Text);
			return true;
		}
		
		if(gameLobby.connectedPlayers.size() >= 2) {
			res.setStatus(Status.Forbidden_403);
			res.setBody(String.format("Game with code %s is full", gameCode), Response.BodyType.Text);
			return true;
		}
		
		Boolean isPlayerRed = (gameLobby.connectedPlayers.size() == 0);
		
		res.setStatus(Status.OK_200);
		res.setBody(isPlayerRed.toString(), Response.BodyType.Text);
		gameLobby.connectedPlayers.put(userID, isPlayerRed);
		
		return true;
	}
	
	public boolean fetchGameStateHandler(Request req, Response res) {
		if(!authorize(req)) {
			res.setStatus(Status.Unauthorized_401);
			res.setBody("No userID provided", Response.BodyType.Text);
			return true;
		}
		
		String userID = req.headers.get(KeyEnum.userID.key);
		String gameCode = req.body;
		GameLobby gameLobby = gameList.getOrDefault(gameCode, null);
		
		if(gameLobby == null) {
			res.setStatus(Status.NotFound_404);
			res.setBody(String.format("Game with code %s doesn't exist", gameCode), Response.BodyType.Text);
			return true;
		}
		
		if(!gameLobby.connectedPlayers.contains(userID)) {
			res.setStatus(Status.Forbidden_403);
			res.setBody(String.format("You're not in game %s", gameCode), Response.BodyType.Text);
			return true;
		}
		
		res.setStatus(Status.OK_200);
		res.setBody(gameLobby.game.serialize(), Response.BodyType.Text);
		gameLobby.game.getBlueBase().setGold((int) Math.floor(Math.random() * 100000));
		return true;
	}
	
	private WebServer buildServerObject(int port) throws IOException {
		WebServer output = new WebServer(port);
		
		output.addHandler("/addGame", this::addGameHandler);
		output.addHandler("/gameList", this::gameListHandler);
		output.addHandler("/joinGame", this::joinGameHandler);
		output.addHandler("/fetchGameState", this::fetchGameStateHandler);
		
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
