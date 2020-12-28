package Client;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI {
	private JPanel panel1;
	private JList test;
	private JTable table1;
	private JButton odświeżListęGierButton;
	private JButton dodajGre;
	
	private List<GameListing> games = new ArrayList<GameListing>();
	
	private class GameListing {
		String gameCode;
		long createdAt;
		
		public String getColumn(int col) {
			return switch(col) {
				case 0 -> this.gameCode;
				case 1 -> String.valueOf(this.createdAt);
				default -> "";
			};
		}
	}
	
	public ClientGUI() {
		table1.setModel(new TableModel() {
			String[] cols = new String[] {"Kod gry", "Czas utworzenia"};
			
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
		
		odświeżListęGierButton.addActionListener(l -> {
			HTTPClient.send("/gameList", "", res -> {
				String[] lines = res.getBody().split("\n");
				
				games.clear();
				for(String line : lines) {
					GameListing newListing = new GameListing();
					String[] parts = line.strip().split(",");
					try {
						newListing.gameCode = parts[0];
						newListing.createdAt = Long.parseLong(parts[1]);
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
		
		dodajGre.addActionListener(l -> {
			HTTPClient.send("/addGame", "", res -> {
				GameListing newListing = new GameListing();
				newListing.gameCode = res.getBody();
				// Just an approximation. Server value will be different
				newListing.createdAt = System.currentTimeMillis();
				
				games.add(newListing);
				table1.tableChanged(new TableModelEvent(table1.getModel()));
			});
		});
		
	}
	
	public static void main(String[] args) {
		ClientGUI gui = new ClientGUI();
		
		JFrame frame = new JFrame("ClientGUI");
		frame.setContentPane(gui.panel1);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}
}
