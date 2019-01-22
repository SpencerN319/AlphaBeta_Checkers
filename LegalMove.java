package lab2_coms472;

public class LegalMove {
	public int xFrom, yFrom, xTo, yTo, evaluation;
	public char player;
	public boolean king_jumped = false;
	public boolean jump = false;
	
	public LegalMove(int xfrom, int yfrom, int xto, int yto, char color){
		this.xFrom = xfrom;
		this.yFrom = yfrom;
		this.xTo = xto;
		this.yTo = yto;
		this.player = color;
	}
	
	public void set_eval(int num){
		this.evaluation = num;
	}
	
	public void set_jump(boolean jumped_king){
		this.jump = true;
		this.king_jumped = jumped_king;
	}

}
