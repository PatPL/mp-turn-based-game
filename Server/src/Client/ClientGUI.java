package Client;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ClientGUI {
	private JPanel panel1;
	private JList test;
	private JTable table1;
	private JButton aButton;
	
	private class GameListing {
		String gameCode;
		long createdAt;
	}
	
	public static void main(String[] args) {
		ClientGUI gui = new ClientGUI();
		
		JFrame frame = new JFrame("ClientGUI");
		frame.setContentPane(gui.panel1);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		// test
		gui.table1.setModel(new TableModel() {
			String[][] data = new String[][] {{"1", "2"}, {"3", "4"}, {"5", "6"}};
			String[] cols = new String[] {"a", "b"};
			
			@Override
			public int getRowCount() {
				return data.length;
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
				return data[rowIndex][columnIndex];
			}
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				data[rowIndex][columnIndex] = aValue.toString();
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {
			
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {
			
			}
		});
		
		// test
		HTTPClient.send("/gameList", "", res -> {
			System.out.println(res.getBody());
		});
		
	}
}
