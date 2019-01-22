package lab2_coms472;

public class Board {
	int wPieces,bPieces,wKings,bKings;
	int evaluation;
	boolean game_over;
	char[][] board;
	char side;
	
	public Board(char[][] board, int white, int black, int white_kings, int black_kings, char side){
		this.wPieces = white - white_kings;
		this.bPieces = black - black_kings;
		this.wKings = white_kings;
		this.bKings = black_kings;
		this.board = board;
		this.side = side;
		game_over = check_game();
	}
	
	public boolean check_game(){
		if(wPieces == 0 || bPieces == 0){
			return true;
		}
		return false;
	}
}
