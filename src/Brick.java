import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;
import java.util.TreeMap;

public class Brick implements Comparable<Brick>{
    
    public static final int LENGTH = 80;
    public static final int HEIGHT = 40;
    private final String c;
    private final Color color;
    private final Color selectedColor;
    private final Color hintColor;
    private boolean isSelected = false;
    private boolean isHint = false;
    private int x;
    private int y;
    private Map<String, Color> colorMap = new TreeMap<>();

    public Brick(String c){
        this.c = c;
        mapBricks();
        color = colorMap.get(c);
        selectedColor = new Color (color.getRed(), color.getGreen(), color.getBlue(), 200);
        hintColor = new Color (color.getRed()/2, color.getGreen()/2, color.getBlue()/2, 200);
    }
    
    private void mapBricks(){
        colorMap.put("r", Color.RED);
        colorMap.put("g", Color.GREEN);
        colorMap.put("b", Color.BLUE);
        colorMap.put("o", Color.ORANGE);
        colorMap.put("p", Color.PINK);
        colorMap.put("a", Color.WHITE); // rainbow brick
    }
    
    public boolean isSelected(){
        return isSelected;
    }
    
    public void flipSelected(){
        isSelected = !isSelected;
    }
    
    public void flipHint(){
        isHint = !isHint;
    }
    
    public boolean getHint(){
        return isHint;
    }
    
    public String strColor(){
        return c;
    }
    
    public Color getColor(){
        return color;
    }
    
    public void draw(Graphics g){
        if(isSelected){
            g.setColor(selectedColor);
            g.fillRect(x, y, LENGTH, HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, LENGTH, HEIGHT);
        } else if(isHint){
            g.setColor(hintColor);
            g.fillRect(x, y, LENGTH, HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, LENGTH, HEIGHT);
        } else {
            g.setColor(color);
            g.fillRect(x, y, LENGTH, HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, LENGTH, HEIGHT);   
        }
    }
    
    public void setPosX(int x){
        this.x = x;
    }
    
    public void setPosY(int y){
        this.y = y;
    }
    
    public int getRow(){
        return (Board.BOARD_HEIGHT-HEIGHT-y)/HEIGHT;
    }
    
    public int getCol(){
        return (Board.BOARD_LENGTH-LENGTH-x)/LENGTH;
    }

    public boolean contains(int x, int y){
        if(x < this.x || y < this.y){
            return false;
        } else if (x <= this.x + 100 && y <= this.y + 50){
            return true;
        }
         return false;
    }

    @Override
    public String toString(){
        return "("+getRow()+","+getCol()+")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Brick))
            return false;
        Brick other = (Brick) obj;
        if(obj instanceof RainbowBrick){ // rainbow brick can be any color
            if(((RainbowBrick) obj).getGroupColor() == null){
                return false;
            }
            if(((RainbowBrick) obj).getGroupColor().equals(color)){
                return true;
            }
        }
        if (color == null) {
            if (other.color != null)
                return false;
        } else if (!color.equals(other.color)){
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Brick b) {
        if(this == b){
            return 0;
        }
        if(y > b.y){
            return 1;
        }
        if(y < b.y){
            return -1;
        }
        if(x > b.x){
            return 1;
        }
        return -1;
    }
    
}
