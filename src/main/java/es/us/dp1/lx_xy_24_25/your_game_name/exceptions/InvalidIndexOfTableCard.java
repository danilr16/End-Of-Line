package es.us.dp1.lx_xy_24_25.your_game_name.exceptions;

public class InvalidIndexOfTableCard extends Exception {

    public InvalidIndexOfTableCard() {
		super("Invalid index of rows and columns!");
	}
	
	public InvalidIndexOfTableCard(String message) {
		super(message);
	}
    
}
