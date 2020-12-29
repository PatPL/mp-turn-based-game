package Client;

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
import java.util.prefs.Preferences;

public class ClientGUI {
	private JPanel panel1;
	private JList test;
	private JTable table1;
	private JButton refreshGameListButton;
	private JButton hostGameButton;
	private JTextField nicknameInput;
	
	private List<GameListing> games = new ArrayList<GameListing>();
	private final Preferences userPrefs = Preferences.userNodeForPackage(this.getClass());
	
	private class GameListing {
		String gameCode;
		String hostNickname;
		
		public String getColumn(int col) {
			return switch(col) {
				case 0 -> this.gameCode;
				case 1 -> this.hostNickname;
				default -> "";
			};
		}
	}
	
	public ClientGUI() {
		// Setting up an unique UserID used by server to identify users
		if(userPrefs.get(KeyEnum.userID.key, null) == null) {
			userPrefs.put(KeyEnum.userID.key, Utility.getRandomString(32));
		}
		// Setting up a default nickname
		if(userPrefs.get(KeyEnum.nickname.key, null) == null) {
			userPrefs.put(KeyEnum.nickname.key, "Player");
		}
		
		// Set a default header sent with every request to the server
		HTTPClient.defaultHeaders.put(KeyEnum.userID.key, userPrefs.get(KeyEnum.userID.key, null));
		HTTPClient.defaultHeaders.put(KeyEnum.nickname.key, userPrefs.get(KeyEnum.nickname.key, null));
		
		nicknameInput.setText(userPrefs.get(KeyEnum.nickname.key, null));
		
		setupListeners();
	}
	
	public static void main(String[] args) {
		ClientGUI gui = new ClientGUI();
		
		JFrame frame = new JFrame("ClientGUI");
		frame.setContentPane(gui.panel1);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	// Run at most once
	private boolean setupListenersCalled = false;
	
	private void setupListeners() {
		if(setupListenersCalled) {
			return;
		}
		setupListenersCalled = true;
		
		table1.setModel(new TableModel() {
			String[] cols = new String[] {"Kod gry", "Host"};
			
			@Override
			public int getRowCount() {
				return games.size();
			}
			
			@Override
			public int getColumnCount() {
				return cols.length;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return cols[columnIndex];
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
		});
		
		refreshGameListButton.addActionListener(l -> {
			HTTPClient.send("/gameList", "", res -> {
				String[] lines = res.getBody().split("\n");
				
				games.clear();
				for(String line : lines) {
					GameListing newListing = new GameListing();
					String[] parts = line.strip().split(",");
					try {
						newListing.gameCode = parts[0];
						newListing.hostNickname = parts[1];
					}
					catch(Exception e) {
						continue;
					}
					games.add(newListing);
				}
				
				// table1.invalidate();
				// table1.repaint();
				
				// Redraw the table. The methods above didn't work, though they should've worked.
				table1.tableChanged(new TableModelEvent(table1.getModel()));
			});
		});
		
		hostGameButton.addActionListener(l -> {
			HTTPClient.send("/addGame", "", res -> {
				if(res.getStatusType() != StatusType.Success_2xx) {
					System.out.printf("Unsuccessful response: \n%s\n", res);
					return;
				}
				
				GameListing newListing = new GameListing();
				newListing.gameCode = res.getBody();
				// Just an approximation. Server value will be different
				newListing.hostNickname = userPrefs.get(KeyEnum.nickname.key, "<unknown nickname>");
				
				games.add(newListing);
				table1.tableChanged(new TableModelEvent(table1.getModel()));
			});
		});
		
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
	
}
