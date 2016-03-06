import java.awt.Color;
import java.awt.Graphics;


public class RemoveRowBrick extends Brick implements PowerUp{

    public RemoveRowBrick(String color) {
        super(color);
    }
    
    @Override
    public void draw(Graphics g){
        super.draw(g);
        g.setColor(Color.BLACK);
        g.fillRect(Board.BOARD_LENGTH-LENGTH*(getCol()+1),
                   Board.BOARD_HEIGHT-HEIGHT*(getRow()+1)+20, LENGTH, 5);
    }
    
    public void doEffect() {
        Brick[][] bricks = Board.getBricks();
        int bonus = 0;
        for(int c = 0; c < 10; c++){
            if(bricks[getRow()][c] != null){
                if(bricks[getRow()][c] instanceof PowerUp && c != getCol()){
                    ((PowerUp)bricks[getRow()][c]).doEffect();
                }
                bricks[getRow()][c] = null;
                bonus++;
            }
        }
        Board.setBricks(bricks);
        Board.setBonus(bonus);
    }

    public int getIntEffect() {
        return 1;
    }

}
