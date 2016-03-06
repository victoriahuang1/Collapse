import java.awt.Color;
import java.awt.Graphics;


public class RemoveColBrick extends Brick implements PowerUp{

    public RemoveColBrick(String color) {
        super(color);
    }
    
    @Override
    public void draw(Graphics g){
        super.draw(g);
        g.setColor(Color.BLACK);
        g.fillRect(Board.BOARD_LENGTH-LENGTH*(getCol()+1)+40, 
                   Board.BOARD_HEIGHT-HEIGHT*(getRow()+1), 5, HEIGHT);
    }

    public void doEffect() {
        Brick[][] bricks = Board.getBricks();
        int bonus = 0;
        for(int r = 0; r < 16; r++){
            if(bricks[r][getCol()] != null){
                if(bricks[r][getCol()] instanceof PowerUp && r != getRow()){
                    ((PowerUp)bricks[r][getCol()]).doEffect();
                }
                bricks[r][getCol()] = null;
                bonus++;
            }
        }
        Board.setBricks(bricks);
        Board.setBonus(bonus);
    }

    public int getIntEffect() {
        return 2;
    }

}
