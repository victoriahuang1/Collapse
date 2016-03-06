import java.awt.Color;
import java.awt.Graphics;


public class RainbowBrick extends Brick implements PowerUp{

    private Color groupColor;
    
    public RainbowBrick(String c) {
        super("a");
    }

    @Override
    public void draw(Graphics g){
        super.draw(g);
        g.setColor(Color.MAGENTA);
        g.fillRect(Board.BOARD_LENGTH-LENGTH*(getCol()+1)+40, 
                   Board.BOARD_HEIGHT-HEIGHT*(getRow()+1), 5, HEIGHT);
        g.setColor(Color.CYAN);
        g.fillRect(Board.BOARD_LENGTH-LENGTH*(getCol()+1),
                   Board.BOARD_HEIGHT-HEIGHT*(getRow()+1)+20, LENGTH, 5);
    }
    
    @Override
    public void doEffect() {
        groupColor = Board.getGroupColor();
    }

    public Color getGroupColor(){
        return groupColor;
    }
    
    @Override
    public int getIntEffect() {
        return 5;
    }
    
    @Override
    public boolean equals(Object obj){
        // if same group color, then true
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Brick))
            return false;
        Brick other = (Brick) obj;
        if (groupColor == null) {
            return false;
        } else if (!groupColor.equals(other.getColor())){
            return false;
        }
        return true;
    }

}
