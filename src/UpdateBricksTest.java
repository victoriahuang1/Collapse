import static org.junit.Assert.*;
import org.junit.*;

public class UpdateBricksTest {
    /**
     * We will modify the bricks in board and compare them to bricks2
     **/
    Board board;
    Brick[][] bricks = new Brick[16][10];
    
    @Before
    public void setup(){
        board = new Board();
    }
    
    private boolean isSame(Brick[][] b1, Brick[][] b2){
        boolean check = true;
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(b1[r][c] == null && b2[r][c] != null){
                    System.out.println(r+","+c);
                    return false;
                }else if(b2[r][c] == null && b1[r][c] != null){
                    System.out.println(r+",,"+c);
                    return false;
                }else if(b1[r][c] != null && b2[r][c] != null && ! b1[r][c].equals(b2[r][c])){
                    check = false;
                }
            }
        }
        return check;
    }
    
    @Test
    public void noGapFall(){
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                bricks[r][c] = new Brick("r");
            }
        }
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        Brick[][] b = Board.getBricks();
        assertTrue(isSame(bricks, b)); // entire board filled
        
        for(int r = 8; r < 16; r++){
            for(int c = 0; c < 10; c++){
                bricks[r][c] = null;
            }
        }
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        assertTrue(isSame(bricks, b)); // missing bricks on top don't affect the others' positions
        
        bricks[7][8] = null;
        bricks[7][4] = null;
        bricks[6][4] = null;
        
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        assertTrue(isSame(bricks, b)); // not all columns are of the same height
    }
    
    @Test
    public void gapAtBottomFall(){ // falls to bottom
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                bricks[r][c] = new Brick("r");
            }
        }
        
        bricks[0][0] = null;
        
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        Brick[][] b = Board.getBricks();
        bricks[0][0] = new Brick("r");
        bricks[15][0] = null;
        assertTrue(isSame(bricks, b)); // gap in last column
        
        bricks[15][0] = new Brick("r");
        bricks[0][9] = null;
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        bricks[0][9] = new Brick("r");
        bricks[15][9] = null;
        assertTrue(isSame(bricks, b)); // gap in first column
        
        bricks[15][9] = new Brick("r");
        bricks[0][5] = null;
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        bricks[0][5] = new Brick("r");
        bricks[15][5] = null;
        assertTrue(isSame(bricks, b)); // gap in middle
    }
    
    @Test
    public void gapInMiddleFall(){ // falls on to another brick
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                bricks[r][c] = new Brick("r");
            }
        }
        
        bricks[5][0] = null;
        
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        Brick[][] b = Board.getBricks();
        bricks[5][0] = new Brick("r");
        bricks[15][0] = null;
        assertTrue(isSame(bricks, b)); // gap in last column
        
        bricks[15][0] = new Brick("r");
        bricks[5][9] = null;
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        bricks[5][9] = new Brick("r");
        bricks[15][9] = null;
        assertTrue(isSame(bricks, b)); // gap in first column
        
        bricks[15][9] = new Brick("r");
        bricks[5][5] = null;
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        bricks[5][5] = new Brick("r");
        bricks[15][5] = null;
        assertTrue(isSame(bricks, b)); // gap in middle
    }
    
    @Test
    public void multipleGapsFall(){
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                bricks[r][c] = new Brick("r");
            }
        }
        bricks[5][5] = null;
        bricks[5][6] = null;
        
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        Brick[][] b = Board.getBricks();
        bricks[5][5] = new Brick("r");
        bricks[15][5] = null;
        bricks[5][6] = new Brick("r");
        bricks[15][6] = null;
        assertTrue(isSame(bricks, b)); // adjacent gaps, fall to same row
        
        bricks[15][5] = new Brick("r");
        bricks[15][6] = new Brick("r");
        
        bricks[5][5] = null;
        bricks[5][6] = null;
        bricks[4][6] = null;
        
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        bricks[5][5] = new Brick("r");
        bricks[15][5] = null;
        bricks[5][6] = new Brick("r");
        bricks[15][6] = null;
        bricks[14][6] = null;
        assertTrue(isSame(bricks, b)); // adjacent gaps, fall to different rows
        
        bricks[15][5] = new Brick("r");
        bricks[15][6] = new Brick("r");
        bricks[14][6] = new Brick("r");
        
        bricks[5][5] = null;
        bricks[5][7] = null;

        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        bricks[5][5] = new Brick("r");
        bricks[15][5] = null;
        bricks[5][7] = new Brick("r");
        bricks[15][7] = null;
        assertTrue(isSame(bricks, b)); // not adjacent gaps, same row
        
        bricks[15][5] = new Brick("r");
        bricks[15][7] = new Brick("r");
        
        bricks[5][5] = null;
        bricks[4][7] = null;

        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        bricks[5][5] = new Brick("r");
        bricks[15][5] = null;
        bricks[4][7] = new Brick("r");
        bricks[15][7] = null;
        assertTrue(isSame(bricks, b)); // not adjacent gaps, different rows
        
        for(int r = 8; r < 16; r++){
            for(int c = 0; c < 10; c++){
                bricks[r][c] = null;
            }
        }

        bricks[14][2] = new Brick("r");
        bricks[13][8] = new Brick("r");
        
        board.loadGame(1, 0, 0, bricks);
        Board.fall();
        b = Board.getBricks();
        bricks[8][2] = new Brick("r");
        bricks[14][2] = null;
        bricks[8][8] = new Brick("r");
        bricks[13][8] = null;
        assertTrue(isSame(bricks, b)); // fall from top
        
    }
    
    @Test
    public void noGapRegroup(){
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                bricks[r][c] = new Brick("r");
            }
        }
        board.loadGame(1, 0, 0, bricks);
        board.regroup();
        Brick[][] b = Board.getBricks();
        assertTrue(isSame(bricks, b)); // entire board filled
        
        for(int r = 0; r < 16; r++){
            for(int c = 1; c < 9; c++){
                bricks[r][c] = null;
            }
        }
        board.loadGame(1, 0, 0, bricks);
        board.regroup();
        b = Board.getBricks();
        assertTrue(isSame(bricks, b)); // missing columns on the ends don't affect the others' positions
        
        bricks[7][8] = null;
        bricks[7][4] = null;
        bricks[6][4] = null;
        
        board.loadGame(1, 0, 0, bricks);
        board.regroup();
        b = Board.getBricks();
        assertTrue(isSame(bricks, b)); // not all columns are of the same height
    }
    
    @Test
    public void singleGapRegroup(){
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(c != 5){
                    bricks[r][c] = new Brick("r");
                }
            }
        }

        board.loadGame(1, 0, 0, bricks);

        for(int i = 0; i < 8; i++){
            board.regroup();
        }

        Brick[][] b = Board.getBricks();
        
        for(int r = 0; r < 16; r++){
            bricks[r][5] = new Brick("r");
            bricks[r][0] = null;
        }
        
        assertTrue(isSame(bricks, b)); // all columns are of same height
        
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(c != 5){
                    bricks[r][c] = new Brick("r");
                } else {
                    bricks[r][c] = null;
                }
            }
        }
        bricks[15][4] = null;
        bricks[15][6] = null;
        board.loadGame(1, 0, 0, bricks);

        for(int i = 0; i < 5; i++){
            board.regroup();
        }

        b = Board.getBricks();
        
        for(int r = 0; r < 15; r++){
            bricks[r][5] = new Brick("r");
            bricks[r][6] = new Brick("r");
        }
        
        assertTrue(isSame(bricks, b)); // all columns are of different height
        
    }
    
    @Test
    public void multipleGapsRegroup(){
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(c == 5 || c == 6){
                    bricks[r][c] = null;
                } else {
                    bricks[r][c] = new Brick("r");
                }
            }
        }

        board.loadGame(1, 0, 0, bricks);

        for(int i = 0; i < 10; i++){
            board.regroup();
        }

        Brick[][] b = Board.getBricks();
        
        for(int r = 0; r < 16; r++){
            bricks[r][5] = new Brick("r");
            bricks[r][6] = new Brick("r");
            bricks[r][0] = null;
            bricks[r][1] = null;
        }
        
        assertTrue(isSame(bricks, b));
        
    }
    
    @Test
    public void gapInRowAndCol(){
        // we should fall first and then regroup
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(c != 5){
                    bricks[r][c] = new Brick("r");
                }
            }
        }
        
        for(int c = 0; c < 10; c++){
            bricks[0][c] = null;
        }
        
        board.loadGame(1, 0, 0, bricks);

        
        Board.fall();
        for(int i = 0; i < 10; i++){
            board.regroup();
        }

        Brick[][] b = Board.getBricks();
        
        for(int r = 0; r < 15; r++){
            for(int c = 1; c < 10; c++){
                bricks[r][c] = new Brick("r");
            }
        }
        
        assertTrue(isSame(bricks, b));
    }
    
}
