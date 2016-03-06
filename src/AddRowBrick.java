import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;


public class AddRowBrick extends Brick implements PowerUp{

    public AddRowBrick(String color) {
        super(color);
    }

    @Override
    public void draw(Graphics g){
        super.draw(g);
        g.setColor(Color.CYAN);
        g.fillOval(Board.BOARD_LENGTH-LENGTH*(getCol()+1)+30, 
                   Board.BOARD_HEIGHT-HEIGHT*(getRow()+1)+10, 20, 20);
    }

    public void doEffect() {
        Brick[][] bricks = Board.getBricks();
        String[] colors = Board.getColors();
        Random rand = new Random();
        for(int c = 0; c < 10; c++){
            if(bricks[15][c] == null){
                bricks[15][c] = new Brick(colors[rand.nextInt(colors.length)]);
            }
        }
        Board.setBricks(bricks);
        Board.fall();
    }

    public int getIntEffect() {
        return 3;
    }

}
