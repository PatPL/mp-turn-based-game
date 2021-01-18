package Client;

import Game.GUIForms.GameGUI;
import Webserver.Utility;
import Webserver.enums.StatusType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

public class ClientGUI {
	
	private final JFrame parentFrame;
	
	private JPanel panel1;
	private JList test;
	private JTable table1;
	private JButton refreshGameListButton;
	private JButton hostGameButton;
	private JTextField nicknameInput;
	private JButton joinGameButton;
	
	private final List<GameListing> games = new ArrayList<GameListing>();
	private final Preferences userPrefs = Preferences.userNodeForPackage(this.getClass());
	
	public final static String[] gameListingCols = new String[] {"Name", "Code", "Size", "Players"};
	
	private class GameListing {
		String gameCode;
		String gameName;
		int length;
		int height;
		int connectedPlayerCount;
		
		public String getColumn(int col) {
			if(col == 0) {
				return this.gameName;
			}
			else if(col == 1) {
				return this.gameCode;
			}
			else if(col == 2) {
				return String.format("%sx%s", length, height);
			}
			else if(col == 3) {
				return String.format("%s/2", connectedPlayerCount);
			}
			else {
				return "";
			}
		}
		
	}
	
	private TableModel buildGameListTableModel() {
		return new TableModel() {
			@Override
			public int getRowCount() {
				return games.size();
			}
			
			@Override
			public int getColumnCount() {
				return gameListingCols.length;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return gameListingCols[columnIndex];
			}
			
			@Override
			public Class getColumnClass(int columnIndex) {
				return String.class;
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return games.get(rowIndex).getColumn(columnIndex);
			}
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {
			
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {
			
			}
		};
	}
	
	public ClientGUI(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		
		// Setting up an unique UserID used by server to identify users
		if(userPrefs.get(KeyEnum.userID.key, null) == null) {
			userPrefs.put(KeyEnum.userID.key, Utility.getRandomString(32));
		}
		// Setting up a default nickname
		if(userPrefs.get(KeyEnum.nickname.key, null) == null) {
			userPrefs.put(KeyEnum.nickname.key, "Player");
		}
		
		//
		boolean LOSOWE_ID_NA_OKNO_DLA_TESTOW = true;
		if(LOSOWE_ID_NA_OKNO_DLA_TESTOW) {
			HTTPClient.defaultHeaders.put(KeyEnum.userID.key, Utility.getRandomString(32));
			HTTPClient.defaultHeaders.put(KeyEnum.nickname.key, userPrefs.get(KeyEnum.nickname.key, null));
		}
		else {
			// Set a default header sent with every request to the server
			HTTPClient.defaultHeaders.put(KeyEnum.userID.key, userPrefs.get(KeyEnum.userID.key, null));
			HTTPClient.defaultHeaders.put(KeyEnum.nickname.key, userPrefs.get(KeyEnum.nickname.key, null));
		}
		
		nicknameInput.setText(userPrefs.get(KeyEnum.nickname.key, null));
		
		setupListeners();
		startRefreshInterval();
		refreshGameList();
	}
	
	public static void main(String[] args) {
		String address = "127.0.0.1";
		int port = 1234;
		
		if(args.length >= 1) {
			String[] tmp = args[0].split(":", 2);
			if(tmp.length == 2) {
				try {
					int tmpPort = Integer.parseInt(tmp[1]);
					address = tmp[0];
					port = tmpPort;
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("[client] ip:port - choose custom server ip/port");
			System.out.println("Example: 'java ClientGUI 192.168.0.1:22222'");
			System.out.println(" ");
		}
		
		HTTPClient.setServerAddress(address);
		HTTPClient.setServerPort(port);
		
		JFrame frame = new JFrame("ClientGUI");
		ClientGUI gui = new ClientGUI(frame);
		frame.setContentPane(gui.panel1);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void refreshGameList() {
		HTTPClient.send("/gameList", "", res -> {
			String[] lines = res.getBody().split("\n");
			
			games.clear();
			for(String line : lines) {
				GameListing newListing = new GameListing();
				String[] parts = line.strip().split(";");
				try {
					newListing.gameCode = parts[0];
					newListing.length = Integer.parseInt(parts[1]);
					newListing.height = Integer.parseInt(parts[2]);
					newListing.gameName = parts[3];
					newListing.connectedPlayerCount = Integer.parseInt(parts[4]);
				}
				catch(Exception e) {
					continue;
				}
				games.add(newListing);
			}
			
			// table1.invalidate();
			// table1.repaint();
			
			int selectedRow = table1.getSelectedRow();
			// Redraw the table. The methods above didn't work, though they should've worked.
			table1.tableChanged(new TableModelEvent(table1.getModel()));
			if(selectedRow >= 0 && selectedRow < table1.getModel().getRowCount()) {
				table1.setRowSelectionInterval(selectedRow, selectedRow);
			}
		});
	}
	
	private void hostNewGame() {
		new HostGameFormGUI((length, height, name, ai) -> {
			HTTPClient.send(
				"/addGame",
				String.format("%s;%s;%s;%s", length, height, name, ai),
				res -> {
					if(res.getStatusType() != StatusType.Success_2xx) {
						JOptionPane.showMessageDialog(
							this.panel1,
							String.format("Failed to create game: %s", res.getBody()),
							"Błąd",
							JOptionPane.ERROR_MESSAGE
						);
						return;
					}
					
					// <- connect
					refreshGameList();
					
				}
			);
		}, parentFrame);
	}
	
	private void joinSelectedGame() {
		String gameCode = games.get(table1.getSelectedRow()).gameCode;
		HTTPClient.send("/joinGame", gameCode, res -> {
			if(res.getStatusType() != StatusType.Success_2xx) {
				JOptionPane.showMessageDialog(
					this.panel1,
					String.format("Failed to join game %s: %s", gameCode, res.getBody()),
					"Błąd",
					JOptionPane.ERROR_MESSAGE
				);
				return;
			}
			
			Boolean isPlayerRed = Boolean.parseBoolean(res.getBody());
			
			// W tym miejscu serwer dołączył do odpowiedniej gry
			parentFrame.setVisible(false);
			stopRefreshInterval();
			
			// A dialog with no parent shows on windows taskbar
			// As the parent windows hides itself anyway, it doesn't affect anything, and still works as expected
			new GameGUI(gameCode, isPlayerRed);
			
			parentFrame.setVisible(true);
			startRefreshInterval();
			refreshGameList();
		});
	}
	
	// Run at most once
	private boolean setupListenersCalled = false;
	
	private void setupListeners() {
		// Run at most once
		if(setupListenersCalled) {
			return;
		}
		setupListenersCalled = true;
		
		table1.setModel(buildGameListTableModel());
		table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table1.getSelectionModel().addListSelectionListener(l -> {
			joinGameButton.setEnabled(
				table1.getSelectedRow() >= 0
				// && games.get(table1.getSelectedRow()).connectedPlayerCount < 2
			);
		});
		
		refreshGameListButton.addActionListener(l -> refreshGameList());
		hostGameButton.addActionListener(l -> hostNewGame());
		joinGameButton.addActionListener(l -> joinSelectedGame());
		
		applyDocumentListener(nicknameInput, newValue -> {
			userPrefs.put(KeyEnum.nickname.key, newValue);
			HTTPClient.defaultHeaders.put(KeyEnum.nickname.key, newValue);
		});
	}
	
	private interface Handler {
		void onChange(String newValue);
	}
	
	private void applyDocumentListener(JTextField element, Handler handler) {
		element.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				handler.onChange(element.getText());
			}
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				handler.onChange(element.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				handler.onChange(element.getText());
			}
		});
		
		element.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				element.selectAll();
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				handler.onChange(element.getText());
			}
		});
	}
	
	// First function call after this many [ms]
	private long intervalStartDelay = 1000;
	// Next function call after this many [ms]
	private long intervalDelay = 5000;
	private Timer refreshInterval = null;
	
	public void stopRefreshInterval() {
		if(refreshInterval != null) {
			refreshInterval.cancel();
			refreshInterval = null;
		}
	}
	
	private void startRefreshInterval() {
		if(refreshInterval == null) {
			refreshInterval = new Timer();
			refreshInterval.schedule(new TimerTask() {
				@Override
				public void run() {
					// Do zrobienia: Zatrzymaj ten timer kiedy okno główne znika
					refreshGameList();
				}
			}, intervalStartDelay, intervalDelay);
		}
	}
	
}
