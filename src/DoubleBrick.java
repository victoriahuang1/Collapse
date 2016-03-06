import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/** Doubles the points of the group **/

public class DoubleBrick extends Brick implements PowerUp{
    
    public DoubleBrick(String color) {
        super(color);
    }

    @Override
    public void draw(Graphics g){
        super.draw(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g.drawString("x2", Board.BOARD_LENGTH-LENGTH*(getCol()+1)+25, Board.BOARD_HEIGHT-HEIGHT*(getRow()+1)+30);
    }

    public void doEffect() {
        Board.setPtsPerBrick(20);
    }

    public int getIntEffect() {
        return 0;
    }


}
