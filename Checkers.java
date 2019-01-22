package lab2_coms472;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Checkers {

	protected final static int SIZE = 8;
	private char[][] board;
	protected int White; 
	protected int Black; 
	protected char whosemove; 
	protected int wKings = 0; 
	protected int bKings = 0;
	protected int total_moves = 0;
	protected int last_attack = 0;
	protected int MAX_DEPTH = 16;
	protected boolean double_jump;
	protected static Scanner stdin;
	

	public Checkers() {
		stdin = new Scanner(System.in);
		board = new char[SIZE][SIZE];
		White = 12;
		Black = 12;
		whosemove = 'w';

		int i, j;
		for (i = 0; i < SIZE; i++)
			for (j = 0; j < SIZE; j++)
				board[i][j] = '-';

		for (i = 1; i < SIZE; i += 2) {
			board[i][1] = 'w';
			board[i][5] = 'b';
			board[i][7] = 'b';
		}
		for (i = 0; i < SIZE; i += 2) {
			board[i][0] = 'w';
			board[i][2] = 'w';
			board[i][6] = 'b';
		}
	}

	/**
	 *  prints Checkers board of current game
	 */
	public void printBoard() {
		int i, j;
		System.out.println("\n Turn: "+whosemove+" | Total moves: " + total_moves);
		System.out.println("White = " + White + "   |   Black = " + Black);
		System.out.println("   1  2  3  4  5  6  7  8  i");
		for (i = 0; i < SIZE; i++) {
			System.out.print((i + 1) + " ");
			for (j = 0; j < SIZE; j++) {
				System.out.print(" " + board[j][i] + " ");
			}
			System.out.println();
		}
		System.out.println("j");
		System.out.println("____________________________\n");
	}
	
	/**
	 *  gets the next move from the AI or the player depending on the turn.
	 *  AI plays as White and will always take the first move.
	 * @param player
	 * @throws IOException
	 */
	public void getNextMove(int player) throws IOException {
		boolean moved = false;

		if (whosemove == 'w'){
			System.out.println("\nThinking...... \n");
			Board temp_board = new Board(this.board, White, Black, wKings, bKings, 'w');
			LegalMove optimal_move = minimax(temp_board, MAX_DEPTH);
			
			execute_AI_Move(optimal_move);
			if(optimal_move.jump){
				//check for multiple jumps
				boolean still_jumping = true;
				while(still_jumping){
					printBoard();
					LegalMove previous = optimal_move;
					LegalMove next = AI_multi_jump(previous, board);
					if(next != null){
						still_jumping = true;
						execute_AI_Move(next);
						total_moves++;
						last_attack = total_moves;
						previous = next;
					} else {
						still_jumping = false;
					}
				}
			}
			moved = true;
		}

		while (!moved) {
			System.out.println("Enter from the square you would like to move from.");
			int movefrom = stdin.nextInt();
			
			System.out.print("Enter from the square you would like to move to, ");
			System.out.println("using the same convention.");
			int moveto = stdin.nextInt();

			if (validMove(movefrom, moveto)) {
				boolean jump = executeMove(movefrom, moveto);
				boolean still_jumping = true;
				if(jump){
					while(still_jumping){
						printBoard();
						//Gives a selection of possible moves if any
						int temp = moveto;
						ArrayList<Integer> multi = multi_jump(temp);
						if(multi.size() > 0){
							System.out.println("double jump active, choose next jump");
							for(int i = 0; i < multi.size(); i++){
								System.out.println("Option: " + i + " = " + multi.get(i));
							}
							int choice = stdin.nextInt();
							if(choice >= 0 && choice < multi.size()){
								still_jumping = executeMove(temp, multi.get(choice));
								total_moves++;
								last_attack = total_moves;
							}
						} else {
							still_jumping = false;
							break;
						}
					}
				}
				moved = true;
			} else
				System.out.println("That was an invalid move, try again.");
		}

		if (whosemove == 'w')
			whosemove = 'b';
		else
			whosemove = 'w';
	}

	/**
	 *  used to tell whether the human player has made a valid choice in movement
	 * @param movefrom
	 * @param moveto
	 * @return
	 */
	public boolean validMove(int movefrom, int moveto) {

		// Gets array indeces corresponding to the move, from parameters.
		int xfrom = movefrom / 10 - 1;
		int yfrom = movefrom % 10 - 1;
		int xto = moveto / 10 - 1;
		int yto = moveto % 10 - 1;
		
		char piece = board[xfrom][yfrom];
		
		// Check if indeces in range, if not, return false.
		if (xfrom < 0 || xfrom > 7 || yfrom < 0 || yfrom > 7 || xto < 0 || xto > 7 || yto < 0 || yto > 7)
			return false;

		// Check to see you are moving your piece to a blank square.
		else if ((board[xfrom][yfrom] == whosemove || board[xfrom][yfrom] == Character.toUpperCase(whosemove)) && board[xto][yto] == '-') {
			
			// Checks case of simple move
			if (Math.abs(xfrom - xto) == 1) {
				
				if ((whosemove == 'w' && (yto - yfrom == 1)) || (whosemove == 'w' && (yto-yfrom == -1) && piece == 'W'))
					return true;
				else if ((whosemove == 'b' && (yto - yfrom == -1)) || (whosemove == 'b' && (yto - yfrom == 1) && piece == 'B'))
					return true;
					
			}

			// Checks case of a jump
			else if (Math.abs(xfrom - xto) == 2) {
				if ((whosemove == 'w' && (yto - yfrom == 2) && board[(xfrom + xto) / 2][(yfrom + yto) / 2] == 'b' || board[(xfrom + xto) / 2][(yfrom + yto) / 2] == 'B' )|| 
					 (whosemove == 'w' && piece == 'W' && (yto-yfrom == -2) && board[(xfrom + xto) / 2][(yfrom + yto) / 2] == 'b' || board[(xfrom + xto) / 2][(yfrom + yto) / 2] == 'B'))
					return true;
				else if ((whosemove == 'b' && (yto - yfrom == -2) && board[(xfrom + xto) / 2][(yfrom + yto) / 2] == 'w' || board[(xfrom + xto) / 2][(yfrom + yto) / 2] == 'W') || 
						(whosemove == 'b' && (yto-yfrom == 2) && piece == 'B' && board[(xfrom + xto) / 2][(yfrom + yto) / 2] == 'w' || board[(xfrom + xto) / 2][(yfrom + yto) / 2] == 'W'))
					return true;
			}
		}
		// If move is neither a simple one or a jump, it is not legal.
		return false;
	}
	
	/**
	 *  Decides if AI has a multi jump and returns a move if any
	 * @param previous_move
	 * @param cur_board
	 * @return
	 */
	public LegalMove AI_multi_jump(LegalMove previous_move, char[][] cur_board){
		ArrayList<LegalMove> moves = new ArrayList<LegalMove>();
		int i = previous_move.xTo;
		int j = previous_move.yTo;
		char piece = cur_board[previous_move.xFrom][previous_move.yFrom];
		if(piece == 'w') {//check for jump bottom left/right
			if(i+2 < SIZE && j+2 < SIZE){ // BOTTOM RIGHT JUMP
				if(cur_board[i+2][j+2] == '-' && cur_board[i+1][j+1] == 'b' || cur_board[i+1][j+1] == 'B'){
					moves.add(new LegalMove(i,j,i+2,j+2,'w'));
				}
			} if(i-2 >= 0 && j+2 < SIZE){// BOTTOM LEFT JUMP
				if(cur_board[i-2][j+2] == '-' && cur_board[i+1][j+1] == 'b' || cur_board[i-1][j+1] == 'B'){
					moves.add(new LegalMove(i,j,i-2,j+2,'w'));
				}
			}
		} else {// CHECK FOR ALL POSSIBLE KING JUMPS
			if(i+2 < SIZE && j+2 < SIZE){ // BOTTOM RIGHT JUMP
				if(cur_board[i+2][j+2] == '-' && cur_board[i+1][j+1] == 'b' || cur_board[i+1][j+1] == 'B'){
					moves.add(new LegalMove(i,j,i+2,j+2,'w'));
				}
			} if(i-2 >= 0 && j+2 < SIZE){// BOTTOM LEFT JUMP
				if(cur_board[i-2][j+2] == '-' && cur_board[i-1][j+1] == 'b' || cur_board[i-1][j+1] == 'B'){
					moves.add(new LegalMove(i,j,i-2,j+2,'w'));
				}
			} if(i+2 < SIZE && j-2 < SIZE){ // TOP RIGHT JUMP
				if(cur_board[i+2][j-2] == '-' && cur_board[i+1][j-1] == 'b' || cur_board[i+1][j-1] == 'B'){
					moves.add(new LegalMove(i,j,i+2,j-2,'w'));
				}
			} if(i-2 >= 0 && j-2 < SIZE){// TOP LEFT JUMP
				if(cur_board[i-2][j-2] == '-' && cur_board[i-1][j-1] == 'b' || cur_board[i-1][j-1] == 'B'){
					moves.add(new LegalMove(i,j,i-2,j-2,'w'));
				}
			}
		}
		
		if(moves.size() == 0){
			return null;
		} else if(moves.size() == 1){
			return moves.get(0);
		} else {
			Random rand = new Random();
			return moves.get(rand.nextInt(moves.size()));
		}
	}
	
	/**
	 *  Returns a list of possible moves if any for the human player
	 * @param movefrom
	 * @return
	 */
	public ArrayList<Integer> multi_jump(int movefrom){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int i = movefrom / 10 - 1;
		int j = movefrom % 10 - 1;
		if(board[i][j] == 'b'){// check for top right/left moves
			if(i-2 >= 0 && j-2 >= 0){// TOP LEFT JUMP
				if(board[i-2][j-2] =='-' && board[i-1][j-1] == 'w' || board[i-1][j-1] == 'W'){
					int next = (i-2+1)*10 + (j-2)+1;
					moves.add(next);
				}
			} if(i+2 < SIZE && j-2 >= 0){// TOP Right jump
				if(board[i+2][j-2] == '-' && board[i+1][j-1] == 'w' || board[i+1][j-1] =='W'){
					int next = (i+2+1)*10 + (j-2)+1;
					moves.add(next);
				}
			}
		} else if(board[i][j] == 'B'){
			if(i-2 >= 0 && j-2 >= 0){// TOP LEFT JUMP
				if(board[i-2][j-2] =='-' && board[i-1][j-1] == 'w' || board[i-1][j-1] == 'W'){
					int next = (i-2+1)*10 + (j-2)+1;
					moves.add(next);
				}
			} if(i+2 < SIZE && j-2 >= 0){// TOP Right jump
				if(board[i+2][j-2] == '-' && board[i+1][j-1] == 'w' || board[i+1][j-1] =='W'){
					int next = (i+2+1)*10 + (j-2)+1;
					moves.add(next);
				}
			} if(i-2 >= 0 && j+2 >= 0){//  BOTTOM LEFT JUMP
				if(board[i-2][j+2] =='-' && board[i-1][j+1] == 'w' || board[i-1][j+1] == 'W'){
					int next = (i-2+1)*10 + (j+2)+1;
					moves.add(next);
				}
			} if(i+2 < SIZE && j+2 >= 0){// BOTTOM Right jump
				if(board[i+2][j+2] == '-' && board[i+1][j+1] == 'w' || board[i+1][j+1] =='W'){
					int next = (i+2+1)*10 + (j+2)+1;
					moves.add(next);
				}
			}
			
		}
		
		return moves;
	}
	
	/**
	 *  Generate a list of possible legal moves for the AI
	 * @param alpha_or_beta
	 * @param cur_board
	 * @return
	 */
	public ArrayList<LegalMove> move_generator(char alpha_or_beta, char[][] cur_board){
		ArrayList<LegalMove> list = new ArrayList<LegalMove>();
		
		if(alpha_or_beta == 'w'){
			boolean can_jump = false;
			for(int i = 0; i < SIZE; i++){
				for(int j = 0; j < SIZE; j++){
					if(cur_board[i][j] == 'w'){
						if((i+1 < SIZE && j+1 < SIZE)){ //POSSIBLE BOTTOM RIGHT MOVE
							if(cur_board[i+1][j+1] == '-'){
								list.add(new LegalMove(i,j,i+1,j+1,alpha_or_beta));
							} else if(cur_board[i+1][j+1] == 'b' || cur_board[i+1][j+1] == 'B'){
								if(i+2 < SIZE && j+2 < SIZE){ // POSSIBLE BOTTOM RIGHT JUMP
									if(cur_board[i+2][j+2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i+2,j+2,alpha_or_beta));
										if(cur_board[i+1][j+1] == 'b'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i+1][j+1] == 'B'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} if(i-1 >= 0 && j+1 < SIZE){ //POSSIBLE BOTTOM LEFT MOVE
							if(cur_board[i-1][j+1] == '-'){
								list.add(new LegalMove(i,j,i-1,j+1,alpha_or_beta));
							} else if(cur_board[i-1][j+1] == 'b' || board[i-1][j+1] == 'B'){
								if(i-2 >= 0 && j+2 < SIZE){ // POSSIBLE BOTTOM LEFT JUMP
									if(cur_board[i-2][j+2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i-2,j+2,alpha_or_beta));
										if(cur_board[i-1][j+1] == 'b'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i-1][j+1] == 'B'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} 	
					} else if(cur_board[i][j] == 'W'){
						if((i+1 < SIZE && j+1 < SIZE)){ //POSSIBLE BOTTOM RIGHT MOVE
							if(cur_board[i+1][j+1] == '-'){
								list.add(new LegalMove(i,j,i+1,j+1,alpha_or_beta));
							} else if(cur_board[i+1][j+1] == 'b' || cur_board[i+1][j+1] == 'B'){
								if(i+2 < SIZE && j+2 < SIZE){ // POSSIBLE BOTTOM RIGHT JUMP
									if(cur_board[i+2][j+2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i+2,j+2,alpha_or_beta));
										if(cur_board[i+1][j+1] == 'b'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i+1][j+1] == 'B'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} if(i-1 >= 0 && j+1 < SIZE){ //POSSIBLE BOTTOM LEFT MOVE
							if(cur_board[i-1][j+1] == '-'){
								list.add(new LegalMove(i,j,i-1,j+1,alpha_or_beta));
							} else if(cur_board[i-1][j+1] == 'b' || cur_board[i-1][j+1] == 'B'){
								if(i-2 >= 0 && j+2 < SIZE){ // POSSIBLE BOTTOM LEFT JUMP
									if(cur_board[i-2][j+2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i-2,j+2,alpha_or_beta));
										if(cur_board[i-1][j+1] == 'b'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i-1][j+1] == 'B'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} if(i+1 < SIZE && j-1 >= 0){ //POSSIBLE TOP RIGHT MOVE
							if(cur_board[i+1][j-1] == '-'){
								list.add(new LegalMove(i,j,i+1,j-1, alpha_or_beta));
							} else if(cur_board[i+1][j-1] == 'b' || cur_board[i+1][j-1] == 'B'){
								if(i+2 < SIZE && j-2 >= 0){ // POSSIBLE TOP RIGHT JUMP
									if(cur_board[i+2][j-2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i+2,j-2,alpha_or_beta));
										if(cur_board[i+1][j-1] == 'b'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i+1][j-1] == 'B'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} if(i-1 >= 0 && j-1 >= 0){ // POSSIBLE TOP LEFT MOVE 
							if(cur_board[i-1][j-1] == '-'){
								list.add(new LegalMove(i,j,i-1,j-1, alpha_or_beta));
							} else if(cur_board[i-1][j-1] == 'b' || cur_board[i-1][j-1] == 'B'){
								if(i-2 < SIZE && j-2 >= 0){ // POSSIBLE TOP LEFT JUMP
									if(cur_board[i-2][j-2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i-2,j-2,alpha_or_beta));
										if(cur_board[i-1][j-1] == 'b'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i-1][j-1] == 'B'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						}
					}
				}
			} 
			if(can_jump){
				for(int i = 0; i < list.size(); i++){
					if(list.get(i).jump == false){
						list.remove(i);
						i--;
					}
				}
			}
		} else{ // MAKING OPTIMAL DECISION FOR BLACK
			boolean can_jump = false;
			for(int i = 0; i < SIZE; i++){
				for(int j = 0; j < SIZE; j++){
					if(cur_board[i][j] == 'b'){
						if((i-1 >= 0 && j-1 >= 0)){ //POSSIBLE TOP LEFT MOVE
							if(cur_board[i-1][j-1] == '-'){
								list.add(new LegalMove(i,j,i-1,j-1,alpha_or_beta));
							} else if(cur_board[i-1][j-1] == 'w' || cur_board[i-1][j-1] == 'W'){
								if(i-2 >= 0 && j-2 >= 0){ // POSSIBLE TOP LEFT JUMP
									if(cur_board[i-2][j-2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i-2,j-2,alpha_or_beta));
										if(cur_board[i-1][j-1] == 'w'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i-1][j-1] == 'W'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} if(i+1 < SIZE && j-1 >= 0){ //POSSIBLE TOP RIGHT MOVE
							if(cur_board[i+1][j-1] == '-'){
								list.add(new LegalMove(i,j,i+1,j-1,alpha_or_beta));
							} else if(cur_board[i+1][j-1] == 'w' || cur_board[i+1][j-1] == 'W'){
								if(i+2 < SIZE && j-2 >= 0){ // POSSIBLE TOP RIGHT JUMP
									if(cur_board[i+2][j-2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i+2,j-2,alpha_or_beta));
										if(cur_board[i+1][j-1] == 'w'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i+1][j-1] == 'W'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} 	
					} else if(cur_board[i][j] == 'W'){
						if((i+1 < SIZE && j+1 < SIZE)){ //POSSIBLE BOTTOM RIGHT MOVE
							if(cur_board[i+1][j+1] == '-'){
								list.add(new LegalMove(i,j,i+1,j+1,alpha_or_beta));
							} else if(cur_board[i+1][j+1] == 'w' || cur_board[i+1][j+1] == 'W'){
								if(i+2 < SIZE && j+2 < SIZE){ // POSSIBLE BOTTOM RIGHT JUMP
									if(cur_board[i+2][j+2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i+2,j+2,alpha_or_beta));
										if(cur_board[i+1][j+1] == 'w'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i+1][j+1] == 'W'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} if(i-1 >= 0 && j+1 < SIZE){ //POSSIBLE BOTTOM LEFT MOVE
							if(cur_board[i-1][j+1] == '-'){
								list.add(new LegalMove(i,j,i-1,j+1,alpha_or_beta));
							} else if(cur_board[i-1][j+1] == 'w' || cur_board[i-1][j+1] == 'W'){
								if(i-2 >= 0 && j+2 < SIZE){ // POSSIBLE BOTTOM LEFT JUMP
									if(cur_board[i-2][j+2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i-2,j+2,alpha_or_beta));
										if(cur_board[i-1][j+1] == 'w'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i-1][j+1] == 'W'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} if(i+1 < SIZE && j-1 >= 0){ //POSSIBLE TOP RIGHT MOVE
							if(cur_board[i+1][j-1] == '-'){
								list.add(new LegalMove(i,j,i+1,j-1, alpha_or_beta));
							} else if(cur_board[i+1][j-1] == 'w' || cur_board[i+1][j-1] == 'W'){
								if(i+2 < SIZE && j-2 >= 0){ // POSSIBLE TOP RIGHT JUMP
									if(cur_board[i+2][j-2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i+2,j-2,alpha_or_beta));
										if(cur_board[i+1][j-1] == 'w'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i+1][j-1] == 'W'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						} if(i-1 >= 0 && j-1 >= 0){ // POSSIBLE TOP LEFT MOVE 
							if(cur_board[i-1][j-1] == '-'){
								list.add(new LegalMove(i,j,i-1,j-1, alpha_or_beta));
							} else if(cur_board[i-1][j-1] == 'w' || cur_board[i-1][j-1] == 'W'){
								if(i-2 >=0 && j-2 >= 0){ // POSSIBLE TOP LEFT JUMP
									if(cur_board[i-2][j-2] == '-'){
										can_jump = true;
										list.add(new LegalMove(i,j,i-2,j-2,alpha_or_beta));
										if(cur_board[i-1][j-1] == 'w'){
											list.get(list.size()-1).set_jump(false);
										} else if(cur_board[i-1][j-1] == 'W'){
											list.get(list.size()-1).set_jump(true);
										}
									}
								}
							}
						}
					}
				}
			} 
			if(can_jump){
				for(int i = 0; i < list.size(); i++){
					if(list.get(i).jump == false){
						list.remove(i);
						i--;
					}
				}
			}
			
		}
		
		return list;
	}

	/**
	 *  Minimax algorithm that finds the best move for the AI
	 *  calls minimax_help for recursion.
	 * @param board
	 * @param depth
	 * @return
	 */
	public LegalMove minimax(Board board, int depth){
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		Random rand = new Random();
		
		ArrayList<LegalMove> moves = move_generator(board.side,board.board);
		if(moves.size() == 1){
			return moves.get(0);
		}
		ArrayList<Integer> heuristics = new ArrayList<Integer>();
		Board temp = null;
		for(LegalMove move : moves){
			temp = gen_next_board(move, board);
			heuristics.add(minimax_help(temp, depth-1, false, alpha, beta));
		}
		
		int maxH = Integer.MIN_VALUE;
		for(Integer h : heuristics){
			if(h > maxH){
				maxH = h;
			}
		}
		for(int i = 0; i < heuristics.size(); i++){
			if(heuristics.get(i) < maxH){
				heuristics.remove(i);
				moves.remove(i);
				i--;
			}
		}
		return moves.get(rand.nextInt(moves.size()));
	}
	
	/**
	 *  helper function that provides the recursion for minimax
	 * @param cur_board
	 * @param depth
	 * @param isMaximizer
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public int minimax_help(Board cur_board, int depth, boolean isMaximizer, int alpha, int beta){
		if(depth == 0 || cur_board.game_over){
			return Evaluations.evaluate(cur_board);
		}
		if(isMaximizer){
			ArrayList<LegalMove> moves = move_generator('w', cur_board.board);
			int maxEval = Integer.MIN_VALUE;
			for(LegalMove move: moves){
				Board temp_board = gen_next_board(move, cur_board);
				maxEval = Math.max(minimax_help(temp_board, depth -1, false, alpha, beta),maxEval);
				alpha = Math.max(alpha, maxEval);
				if(alpha >= beta){
					break;
				} 
			}
			return maxEval;
			
		} else {
			ArrayList<LegalMove> moves = move_generator('b', cur_board.board);
			int minEval = Integer.MAX_VALUE;
			for(LegalMove move: moves){
				Board temp_board = gen_next_board(move, cur_board);
				minEval = Math.min(minimax_help(temp_board, depth-1, true, alpha, beta),minEval);	
				beta = Math.min(beta, minEval);
				if(alpha >= beta){
					break;
				}
			}
			return minEval;
		}
	}
	
	/**
	 *  provides a simple switch for the AI to change sides
	 * @param side
	 * @return
	 */
	public char switch_side(char side){
		if(side == 'w'){
			return 'b';
		} else {
			return 'w';
		}
	}
	
	/**
	 *  Generates the next board give the next move
	 * @param move
	 * @param cur_board
	 * @return
	 */
	public Board gen_next_board(LegalMove move, Board cur_board){
		char[][] next = new char[SIZE][SIZE];
		int w,b,wk,bk;
		w = White;
		b = Black;
		wk = wKings;
		bk = bKings;
		//copy the board
		for(int i = 0; i < SIZE; i++){
			for(int j = 0; j < SIZE; j++){
				next[i][j] = cur_board.board[i][j];
			}
		}
		char piece = next[move.xFrom][move.yFrom];
		next[move.xFrom][move.yFrom] = '-';
		next[move.xTo][move.yTo] = piece;
		if(Math.abs(move.xTo-move.xFrom) == 2){
			next[(move.xFrom+move.xTo)/2][(move.yFrom+move.yTo)/2] = '-';
			char piece_jumped = cur_board.board[(move.xFrom+move.xTo)/2][(move.yFrom+move.yTo)/2];
			if(piece_jumped == 'w'){
				w--;
			} else if(piece_jumped == 'W'){
				wk--;
			} else if(piece_jumped == 'b'){
				b--;
			} else if(piece_jumped == 'B'){
				bk--;
			}
		}
		Board new_board = new Board(next, w, b, wk, bk, switch_side(cur_board.side));
		
		return new_board;
	}
	
	/**
	 * checks to see if any pieces have entered a kinging area and need to be crowned 
	 */
	public void check_kings(){
		for(int i = 0; i < 8; i++){
			if(this.board[i][0] == 'b'){
				this.board[i][0] = 'B';
				this.bKings++;
			}
			if(this.board[i][7] == 'w'){
				this.board[i][7] = 'W';
				this.wKings++;
			}
		}
	}
	
	/**
	 *  Executes the AI move and updates the Checkers board
	 * @param move
	 */
	public void execute_AI_Move(LegalMove move){
		char piece = this.board[move.xFrom][move.yFrom];
		this.board[move.xFrom][move.yFrom] = '-';
		this.board[move.xTo][move.yTo] = piece;
		if(Math.abs(move.xTo - move.xFrom) == 2){
			this.board[(move.xFrom+move.xTo)/2][(move.yFrom+move.yTo)/2] = '-';
			this.Black--;
			this.last_attack = total_moves+1;
		}
		this.total_moves++;
	}
	
	/**
	 *  executes the humans move if valid
	 * @param movefrom
	 * @param moveto
	 * @return
	 */
	public boolean executeMove(int movefrom, int moveto) {
		int xfrom = movefrom / 10 - 1;
		int yfrom = movefrom % 10 - 1;
		int xto = moveto / 10 - 1;
		int yto = moveto % 10 - 1;
		char piece = board[xfrom][yfrom];
		board[xfrom][yfrom] = '-';
		board[xto][yto] = piece;
		if (Math.abs(xto - xfrom) == 2) {
			board[(xfrom + xto) / 2][(yfrom + yto) / 2] = '-';
			last_attack = total_moves+1;
			if (whosemove == 'w'){
				Black--;
			} else {
				White--;
			} return true;
		}
		total_moves++;
		return false;
	}
	
	/**
	 * checks to see if the current game is over
	 * @return
	 */
	public boolean gameOver() {
		return (White == 0 || Black == 0);
	}
	
	/**
	 * function to pick a winner
	 * @return
	 */
	public String winnerIs() {
		if (Black == 0)
			return "White";
		else
			return "Black";
	}

	public static void main(String args[]) throws IOException {
		Checkers game = new Checkers();
		System.out.println("To begin game enter start......\n");
		System.out.println("**    AI = White  |  Player = Black  **");
		System.out.println("        White makes first move         ");
		System.out.println("              Good Luck!               ");
		int draw_count = 80;

		game.printBoard();
		int cur_player = 1;
		while(!game.gameOver()){
			if(game.last_attack - game.total_moves == draw_count){
				System.out.println("\n*** Draw, Great Game! ***\n");
				return;
			}
			game.getNextMove(cur_player);
			if(cur_player == 1){
				cur_player = 2;
			} else {
				cur_player = 1;
			}
			game.check_kings();
			if(cur_player == 2){
				game.printBoard();
			}
		}
		System.out.println("The winner is " + game.winnerIs());
			
		stdin.close();
	}
}
