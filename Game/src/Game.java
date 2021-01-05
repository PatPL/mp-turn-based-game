import BuildingsGenerators.Base;
import Units.Unit;

import javax.swing.*;

public class Game {
	
	//Setting up field
	private int rows;
	private int columns;
	private Unit[][] unitMap;
	
	//Setting up health for bases
	private int baseHealth;
	private Base redBase = new Base(baseHealth, 1);
	private Base blueBase = new Base(baseHealth, 2);
	
	JFrame frame = new JFrame("Game");
	
	
	//Constructor
	private Game(int newRows, int newColumns, int newBaseHealth){
		this.rows = newRows;
		this.columns = newColumns;
		this.baseHealth = newBaseHealth;
		this.unitMap = new Unit[rows][columns];
	}
	
	//Turn for one player
	private void turn(Base base){
		
		//Pop up window for information
		JOptionPane.showMessageDialog(null, "Player " + base.getTeamNumber() + " turn");
		
		//Setting up main frame to be visible
		frame.setContentPane(new GameGUI(base).getMainPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		//Win conditions
		if(redBase.getHealth() <= 0){
			JOptionPane.showMessageDialog(null, "Congratulations! Blue team wins!");
			System.exit(0);
		}
		if(blueBase.getHealth() <= 0){
			JOptionPane.showMessageDialog(null, "Congratulations! Red team wins!");
			System.exit(0);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//Code execution
	public static void main (String[] args) {
		try
		{
			Game obj = new Game(3, 10, 100);
			obj.run (args);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
	
	
	public void run (String[] args) throws Exception {
		
		//Setting every unit on map to be like "not existing"
		for(int i = 0 ; i < rows ; i++){
			for(int k = 0 ; k < columns; k++){
				unitMap[i][k] = new Unit();
			}
		}
		
		//Actual game
		while(true){
			
			turn(redBase);
			turn(blueBase);
			
		}
	}
}
