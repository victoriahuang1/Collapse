import java.awt.Color;
import java.awt.Graphics;


public class BlastBrick extends Brick implements PowerUp{

    public BlastBrick(String c) {
        super(c);
    }

    @Override
    public void draw(Graphics g){
        super.draw(g);
        g.setColor(Color.BLACK);
        g.fillRect(Board.BOARD_LENGTH-LENGTH*(getCol()+1)+LENGTH/3, 
                Board.BOARD_HEIGHT-HEIGHT*(getRow()+1)+HEIGHT/3, LENGTH/3, HEIGHT/3);
    }
    
    @Override
    public void doEffect() {
        Brick[][] bricks = Board.getBricks();
        int bonus = 0;
        for(int r = this.getRow()-1; r <= this.getRow()+1; r++){
            for(int c = this.getCol()-1; c <= this.getCol()+1; c++){
                if(r<16 && r>=0 && c<10 && c>=0 && bricks[r][c] != null){
                    if(bricks[r][c] instanceof PowerUp && this != bricks[r][c]){
                        ((PowerUp)bricks[r][c]).doEffect();
                    }
                    bricks[r][c] = null;
                    bonus++;
                }
            }
        }
        Board.setBricks(bricks);
        Board.setBonus(bonus);
        
    }

    @Override
    public int getIntEffect() {
        return 4;
    }

}
