package lab2_coms472;



public class Evaluations {
	//Pieces worth 1, Kings worth 2
	
	public static int evaluate(Board cur_board){
		int pieces = eval_pieces(cur_board);
		int location = eval_location(cur_board);
		int safety = eval_safety(cur_board);
		
		int total = pieces + location + safety;
		
		return total;
	}
	
	private static int eval_pieces(Board cur_board){
		int val = 0;
		int wpieces, wkings;
		int bpieces, bkings;
		
		
		wpieces = ((cur_board.wPieces - cur_board.wKings)*1); 
		wkings = (cur_board.wKings * 2); 
		bpieces = ((cur_board.bPieces - cur_board.bKings) * 1); 
		bkings = (cur_board.bKings * 2); 
		
		if(cur_board.side == 'w'){
			val += (wpieces + wkings - bpieces - bkings);
			if(val == 0){
				return val;
			}
			if((wpieces + wkings) > (bpieces + bkings)){
				val += 10;
			} else if((wpieces + wkings) < (bpieces + bkings)){
				val -= 10;
			}
		} else if(cur_board.side == 'b'){
			val += (bpieces + bkings - wpieces - wkings);
			if((wpieces + wkings) > (bpieces + bkings)){
				val += 10;
			} else if((wpieces + wkings) > (bpieces + bkings)){
				val -= 10;
			}
		}
		return val;
	}
	
	// If pieces are closer to becoming kings++ 
	//White wants the highest possible score
	//Black wants a lowest possible score
	public static int eval_location(Board cur_board){
		int wtotal = 0;
		int btotal = 0;
		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if(cur_board.board[i][j] == 'w'){
					wtotal += j;
				} else if(cur_board.board[i][j] == 'b'){
					btotal += j;
				}
			}
		}
	
		if(cur_board.side == 'w'){
			return (wtotal / cur_board.wPieces);
		} else {
			return (btotal / cur_board.bPieces);
		}
	}
	
	//SAFE POSITIONS //Safe Positions [0][0..7] 
	public static int eval_safety(Board cur){
		int safety_total = 0;
		for(int x = 0; x < 8; x++){
			if(cur.side == 'w'){
				//check count ++ if w is in safe space -- for b
				if(cur.board[0][x] == 'w' || cur.board[0][x] == 'W'){
					safety_total++;
				} else if(cur.board[0][x] == 'b' || cur.board[0][x] == 'B'){
					safety_total--;
				} if(cur.board[7][x] == 'w' || cur.board[7][x] == 'W'){
					safety_total++;
				} else if(cur.board[7][x] == 'b' || cur.board[7][x] == 'B'){
					safety_total--;
				}
				
				//Home row safety, leave home row as long as possible
				if(cur.board[0][x] == 'w'){
					safety_total += 2;
				}
			} else if(cur.side == 'b'){
				if(cur.board[0][x] == 'w' || cur.board[0][x] == 'W'){
					safety_total--;
				} else if(cur.board[0][x] == 'b' || cur.board[0][x] == 'B'){
					safety_total++;
				} if(cur.board[7][x] == 'w' || cur.board[7][x] == 'W'){
					safety_total--;
				} else if(cur.board[7][x] == 'b' || cur.board[7][x] == 'B'){
					safety_total++;
				}
				
				//Home row safety, leave home row as long as possible
				if(cur.board[0][x] == 'b'){
					safety_total += 2;
				}
			}
		}
		
		return safety_total;
	} 
	

}
