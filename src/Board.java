import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Board extends JPanel{

    public static final int BOARD_LENGTH = 10*Brick.LENGTH;
    public static final int BOARD_HEIGHT = 16*Brick.HEIGHT;
    public static final int INTERVAL = 15000;
    private static Brick[][] bricks = new Brick[16][10];
    private static int level = 0;
    private String line;
    private Random rand = new Random();
    private boolean hasGap = false;
    private static int ptsToNextLvl;
    private static int score = 0;
    private Timer timer;
    private int countBlinks = 0;
    private static boolean hintOn = true;
    private boolean hasWon = false;
    private static int ptsPerBrick = 10;
    private static int bonus = 0;
    private static String[] lvlColors;
    private static Color groupColor;
    
    public Board(){
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                clickBricks(e);
                advance();
            }
        });
        
        try {
            nextLevel();
        } catch (IOException e) {
            
        }
        timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(hintOn){
                    timer.setDelay(100);
                    tick();
                }
            }
        });
        timer.start();
        timer.setInitialDelay(15000);
        timer.setDelay(100);
    }
    
    public void loadGame(int lvl, int scr, int pts, Brick[][] b){
        level = lvl;
        score = scr;
        try {
            setPtsToNxtLvl();
            ptsToNextLvl = pts;
        } catch (IOException e1) {
        }
        bricks = b;
        repaint();
    }
    
    public static void setBricks(Brick[][] b){
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                bricks[r][c] = b[r][c];
            }
        }
    }
    
    public static void flipHint(){
        hintOn = ! hintOn;
    }
    
    public static boolean isHintOn(){
        return hintOn;
    }
    
    public static Color getGroupColor(){
        return groupColor;
    }
    
    public static Brick[][] getBricks(){
        Brick[][] b = new Brick[16][10];
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                b[r][c] = bricks[r][c];
            }
        }
        return b;
    }
    
    public static String[] getColors(){
        return lvlColors;
    }
    
    public void nextLevel() throws IOException{
        level += 1;
        setPtsToNxtLvl();
        if(hasWon){
            if(Stats.hasSound()){
                Sound.WIN.play();
            }
            level -= 1;
            return;
        }
        if(level > 1 && Stats.hasSound()){
            Sound.LEVELUP.play();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader
                (Board.class.getResourceAsStream("brickColors.txt")));
        while((line = br.readLine()) != null){
            if(! line.equals("")){
                String[] colors = line.split(",");
                lvlColors = new String[colors.length-1];
                for(int r = 0; r < 16; r++){
                    for(int c = 0; c < 10; c++){
                        if(r >= 8){
                            bricks[r][c] = null;
                        } else if(Integer.parseInt(colors[0]) >= level){
                            int color = rand.nextInt(colors.length-1)+1;
                            for(int i = 1; i < colors.length; i++){
                                lvlColors[i-1] = colors[i];
                                if(color == i){
                                    bricks[r][c] = new Brick(colors[i]);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if(bricks[0][0] != null){
                insertPowerUp();
                break;
            }
        }
        br.close();
        if(! hasMove() && ! hasWon){ // make sure it's not at the last level
            level -= 1;
            nextLevel();
        }
    }
    
    void tick(){
        for(Brick b : getHint()){
            b.flipHint();
        }
        repaint();
        if(countBlinks == 3){
            resetHint();
            return;
        } else {
            countBlinks++;
        }
    }
    
    private void resetHint(){
        timer.restart();
        timer.setInitialDelay(15000);
        countBlinks = 0;
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(bricks[r][c] != null && bricks[r][c].getHint()){
                    bricks[r][c].flipHint();
                }
            }
        }
        repaint();
    }
    
    public Collection<Brick> getHint(){
        int best = 2;
        Set<Brick> s = new TreeSet<>();
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(bricks[r][c] != null && getGroup(r, c, new boolean[16][10]).size() >= best){
                    s = (Set<Brick>) getGroup(r, c, new boolean[16][10]);
                    best = s.size();
                }
            }
        }
        return s;
    }
    
    public void setPtsToNxtLvl() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader
                (Board.class.getResourceAsStream("pointsToNextLevel.txt")));
        while((line = br.readLine()) != null){
            if(! line.equals("")){
                String[] info = line.split(",");
                if(Integer.parseInt(info[0]) == level){
                    ptsToNextLvl = Integer.parseInt(info[1]);
                    br.close();
                    hasWon = false;
                    return;
                }
            }
        }
        ptsToNextLvl = 0;
        hasWon = true;        
        br.close();
    }
    
    public void insertPowerUp() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader
                (Board.class.getResourceAsStream("powerUps.txt")));
        while((line = br.readLine()) != null){
            
            String[] info = line.split(",");
            if(Integer.parseInt(info[0]) == level){
                if(info.length <= 1){
                    br.close();
                    return;
                }
                for(int i = 1; i < info.length; i++){
                    Effect e = Effect.getEffect(Integer.parseInt(info[i]));
                    String c = bricks[0][0].strColor();
                    if (e.equals(Effect.DOUBLE)){
                        bricks[rand.nextInt(7)][rand.nextInt(9)] = new DoubleBrick(c);
                    } else if (e.equals(Effect.REMOVE_COL)){
                        bricks[rand.nextInt(7)][rand.nextInt(9)] = new RemoveColBrick(c);
                    } else if (e.equals(Effect.REMOVE_ROW)){
                        bricks[rand.nextInt(7)][rand.nextInt(9)] = new RemoveRowBrick(c);
                    } else if(e.equals(Effect.ADD_ROW)){
                        bricks[rand.nextInt(7)][rand.nextInt(9)] = new AddRowBrick(c);
                    } else if(e.equals(Effect.BLAST)){
                        bricks[rand.nextInt(7)][rand.nextInt(9)] = new BlastBrick(c);
                    } else if(e.equals(Effect.RAINBOW)){
                        bricks[rand.nextInt(7)][rand.nextInt(9)] = new RainbowBrick(c);
                    }
                }
                br.close();
                return;
            }
        }
        br.close();
    }
    
    public boolean hasWon(){
        return hasWon;
    }
    
    public void advance(){
        if(! hasMove() && Stats.getPtsNeeded() == 0){
            try {
                nextLevel();
            } catch (IOException e) {
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage img = null;
        try {
            if (img == null) {
                img = ImageIO.read(Board.class.getResource("026.jpg"));
                g.drawImage(img, 0, 0, BOARD_LENGTH, BOARD_HEIGHT, null);
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(bricks[r][c] != null){
                    bricks[r][c].setPosX(BOARD_LENGTH-Brick.LENGTH*(c+1));
                    bricks[r][c].setPosY(BOARD_HEIGHT-Brick.HEIGHT*(r+1));
                    bricks[r][c].draw(g);
                }
            }
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_LENGTH, BOARD_HEIGHT);
    }

    public Collection<Brick> getGroup(int r, int c, boolean[][] checked){
        Set<Brick> s = new TreeSet<>(Collections.singleton(bricks[r][c]));
        groupColor = bricks[r][c].getColor();
        checked[r][c] = true;
        if(c - 1 >= 0){ // check left
            if(bricks[r][c-1] != null && !checked[r][c-1]){
                checked[r][c-1] = true;
                if(bricks[r][c-1] instanceof RainbowBrick){
                    ((RainbowBrick) bricks[r][c-1]).doEffect();
                }
                if(bricks[r][c-1].equals(bricks[r][c])){
                    s.add(bricks[r][c-1]);
                    s.addAll(getGroup(r, c-1, checked));
                }
            }
        }
        if(c + 1 < 10){ // check right
            if(bricks[r][c+1] != null && !checked[r][c+1]){
                checked[r][c+1] = true;
                if(bricks[r][c+1] instanceof RainbowBrick){
                    ((RainbowBrick) bricks[r][c+1]).doEffect();
                }
                if(bricks[r][c+1].equals(bricks[r][c])){
                    s.add(bricks[r][c+1]);
                    s.addAll(getGroup(r, c+1, checked));
                }
            }
        }
        if(r + 1 < 16){ // check up
            if(bricks[r+1][c] != null && !checked[r+1][c]){
                checked[r+1][c] = true;
                if(bricks[r+1][c] instanceof RainbowBrick){
                    ((RainbowBrick) bricks[r+1][c]).doEffect();
                }
                if(bricks[r+1][c].equals(bricks[r][c])){
                    s.add(bricks[r+1][c]);
                    s.addAll(getGroup(r+1, c, checked));
                }
            }
        }
        if(r - 1 >= 0){ // check down
            if(bricks[r-1][c] != null && !checked[r-1][c]){
                checked[r-1][c] = true;
                if(bricks[r-1][c] instanceof RainbowBrick){
                    ((RainbowBrick) bricks[r-1][c]).doEffect();
                }
                if(bricks[r-1][c].equals(bricks[r][c])){
                    s.add(bricks[r-1][c]);
                    s.addAll(getGroup(r-1, c, checked));
                }
            }
        }
        return s;
    }
    
    public Brick getSelected(){
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(bricks[r][c] != null && bricks[r][c].isSelected() && 
                        ! (bricks[r][c] instanceof RainbowBrick)){
                    return bricks[r][c];
                }
            }
        }
        return null;
    }

    public static void fall(){
        for(int a = 0; a < 8; a++){ // maximum of 8 gaps in a row
            for(int r = 1; r < 16; r++){ // if r=0 then it's already at the bottom
                for(int c = 0; c < 10; c++){
                    if(bricks[r][c] != null){
                        for(int i = r; i >0; i--){
                            if(bricks[r-1][c] == null){
                                bricks[r-1][c] = bricks[r][c];
                                bricks[r][c] = null;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void regroup(){
        int n = 0;
        int start = -1;
        int end = 0;
        int gapSize;
        for(int c = 0; c < 10; c++){
            if(bricks[0][c] != null){
                start = c;
            }
            if(bricks[0][c] == null && start != -1){ // looking for first null
                n = c;
                break;
            }
        }
        if(n == 9 || start == -1){ // no gap
            hasGap = false;
            return;
        }
        
        for(int c = n+1; c < 10; c++){ // looking for first brick after null
            if(bricks[0][c] != null){
                end = c;
                break;
            }
        }
        gapSize = end-start-1;
        if(gapSize <= 0 || end <= 1){ // no gap
            hasGap = false;
            return;
        }
        hasGap = true;
        for(int r = 0; r < 16; r++){
            bricks[r][end-1] = bricks[r][start];
            bricks[r][start] = null;
        }
    }
    
    public static void setBonus(int b){
        bonus = b;
    }
    
    public void clickBricks(MouseEvent e) {
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(bricks[r][c] != null && bricks[r][c].contains(e.getX(), e.getY())){
                    Set<Brick> group = (TreeSet<Brick>)getGroup(r, c, new boolean[16][10]);
                    if(getSelected() == null && group.size() >= 2){ // nothing selected yet
                        if(Stats.hasSound()){
                            Sound.CLICK.play();
                        }
                        for(Brick b : group){
                            b.flipSelected();
                            if(b.getHint()){
                                b.flipHint();
                            }
                        }
                        timer.restart();
                    } else if (bricks[r][c].isSelected()){ // group selected
                        if(Stats.hasSound()){
                            Sound.BREAK.play();
                        }
                        hasGap = true;
                        for(Brick b : group){
                            if(b instanceof PowerUp){
                                ((PowerUp) b).doEffect();
                            }
                        }
                        for(Brick b : group){
                            bricks[b.getRow()][b.getCol()] = null;
                        }
                        fall();
                        score += ptsPerBrick * (group.size() + bonus);
                        ptsPerBrick = 10;
                        bonus = 0;
                        while(hasGap){
                            regroup();
                        }
                        timer.restart();
                    } else if(group.size() >= 2){ // different group selected
                        if(Stats.hasSound()){
                            Sound.CLICK.play();
                        }
                        Brick brick = getSelected(); // unselect the currently selected
                        for(Brick b : getGroup(brick.getRow(), brick.getCol(), new boolean[16][10])){
                            if(b instanceof RainbowBrick){
                                groupColor = null;
                                ((RainbowBrick) b).doEffect(); // no color if not in group
                            }
                            b.flipSelected();
                        }
                        for(Brick b : group){ // select
                            if(b instanceof RainbowBrick){
                                groupColor = bricks[r][c].getColor();
                                ((RainbowBrick) b).doEffect();
                            }
                           b.flipSelected();
                            if(b.getHint()){
                                b.flipHint();
                            }
                        }
                        timer.restart();
                    } else if(getSelected() != null){ //single brick selected
                        Brick brick = getSelected(); // unselect the currently selected
                        for(Brick b : getGroup(brick.getRow(), brick.getCol(), new boolean[16][10])){
                            b.flipSelected();
                            if(b instanceof RainbowBrick){
                                groupColor = null;
                                ((RainbowBrick) b).doEffect();
                            }
                        }
                    }
                repaint();
                return;
                }
            }
        }
    }

    public static int getLevel(){
        return level;
    }
    
    public static int getScore(){
        return score;
    }
    
    public static int getPts(){
        return ptsToNextLvl;
    }
    
    public boolean hasMove(){ // true if there is a group of at least 2
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(bricks[r][c] != null && getGroup(r, c, new boolean[16][10]).size() >= 2){
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void setPtsPerBrick(int p){
        ptsPerBrick = p;
    }
    
    public static int getPtsPerBrick(){
        return ptsPerBrick;
    }
    
    public boolean hasLost(){
        if(! hasMove() && Stats.getPtsNeeded() > 0){
            return true;
        }
        return false;
    }
    
    public void newGame(){
        level = 0;
        score = 0;
        timer.restart();
        try {
            nextLevel();
            repaint();
        } catch (IOException e) {
        }
    }
}
