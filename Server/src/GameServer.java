import Webserver.Request;
import Webserver.Response;
import Webserver.WebServer;
import Webserver.enums.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
	
	private static final int defaultPort = 1234;
	
	private WebServer currentWebServer = null;
	private List<Game> gameList = new ArrayList<Game>();
	
	public GameServer() throws IOException {
		currentWebServer = buildServerObject(defaultPort);
	}
	
	public void start() {
		currentWebServer.start();
	}
	
	public void stop() {
		currentWebServer.stop();
	}
	
	public String addGame() {
		Game newGame = new Game();
		gameList.add(newGame);
		return newGame.ID;
	}
	
	private boolean addGameHandler(Request req, Response res) {
		res.setStatus(Status.Created_201);
		res.setBody(addGame(), Response.BodyType.Text);
		
		return true;
	}
	
	public boolean gameListHandler(Request req, Response res) {
		res.setStatus(Status.OK_200);
		
		StringBuilder gameListString = new StringBuilder();
		for(Game i : gameList) {
			gameListString.append(i.ID);
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
		System.out.printf("Game server started at %s\n", server.currentWebServer.getAddress());
	}
	
}
