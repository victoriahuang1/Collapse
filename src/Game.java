// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
    
    public final static boolean isSaved = false;
    public String user;
    public boolean insOpen = false; // true if instructions page is open
    private Map<String, Integer> usersToScores = new TreeMap<>();
    private boolean restart; // true if we want to restart the game
    private JLabel username;
    private JFrame change_frame = new JFrame("Select User");
    private JFrame frame = new JFrame("Collapse");
    private JPanel board_panel;
    private JPanel stats_panel;
    private JButton instructions;
    
	public void run() {
        username = new JLabel(" Current User: " + user);
	    final String[] users = getUsers();
	    
        change_frame.setLocation(300, 300);
        change_frame.setPreferredSize(new Dimension(350, 150));
        change_frame.setLayout(new BorderLayout());
        
        JPanel pnl = new JPanel();
        pnl.setLayout(new BorderLayout());
        final JLabel change_lbl = 
                new JLabel("Choose an existing user or make a new username");
        final JButton ok = new JButton("OK");
        final JComboBox<String> choices = new JComboBox<>(users);
        choices.setEditable(true);
        pnl.add(ok, BorderLayout.EAST);
        pnl.add(choices, BorderLayout.CENTER);
        JPanel p = new JPanel();
        p.add(pnl);
        
        change_frame.add(change_lbl, BorderLayout.NORTH);
        change_frame.add(p, BorderLayout.CENTER);
        
        change_frame.pack();
        change_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    change_frame.setVisible(true);
       
		frame.setLocation(100, 100);
		frame.setMinimumSize(new Dimension(1030, 725));
		
        
		// Stats panel
        final SpecialEffects se = new SpecialEffects();
        
		final Stats stats = new Stats();
        final JFrame gameOver = new JFrame();
        JLabel gameOver_lbl = new JLabel("GAME OVER!");
        gameOver.add(gameOver_lbl);
        gameOver.pack();
        gameOver.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameOver.setLocation(300, 300);
        gameOver.setSize(100, 100);
        gameOver.setResizable(false);
        
        final JFrame winFrame = new JFrame();
        winFrame.setLocation(300, 300);
        winFrame.setSize(200, 200);
        winFrame.setLayout(new BorderLayout());
        JLabel win = new JLabel("You Won!");
        JPanel pnl_win = new JPanel();
        pnl_win.add(win);
        winFrame.add(pnl_win, BorderLayout.CENTER);
        winFrame.pack();
        winFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        instructions = new JButton("Instructions");
        final Instructions ins = new Instructions();
        
        // Main playing area
        JPanel main = new JPanel();
        final Board board = new Board();
        board_panel = new JPanel();
        board_panel.setLayout(new CardLayout());
        board_panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        board_panel.add(board, "PLAY");
        main.add(board_panel);
        
		board.addMouseListener(new MouseAdapter(){
		    HighScores hs;
            @Override
            public void mouseClicked(MouseEvent e) {
                stats.updateStats();
                if(board.hasLost()){
                    if(Stats.hasSound()){
                        Sound.GAMEOVER.play();
                    }
                    addScore();
                    hs = new HighScores();
                    gameOver.setVisible(true);
                    board_panel.add(hs, "HS");
                    ((CardLayout)board_panel.getLayout()).show(board_panel, "HS");
                    instructions.setText("Back to Game");
                    insOpen = true;
                } else if(board.hasWon()){
                    addScore();
                    hs = new HighScores();
                    board_panel.add(hs, "HS");
                    winFrame.setVisible(true);
                    ((CardLayout)board_panel.getLayout()).show(board_panel, "HS");
                    instructions.setText("Back to Game");
                    insOpen = true;
                }
            }
		});
		stats_panel = new JPanel();
		stats_panel.setLayout(new CardLayout());
        stats_panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		stats_panel.add(stats, "STATS");
        stats_panel.add(se, "SE");
        main.add(stats_panel);
        frame.add(main, BorderLayout.CENTER);
		
        change_frame.getRootPane().setDefaultButton(ok);
		
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUser(change_lbl, choices, board, stats);
            }
            
        });
		
		final JPanel control_panel = new JPanel();
		JPanel pnl_north = new JPanel();
		pnl_north.setLayout(new BorderLayout());
		pnl_north.add(username, BorderLayout.WEST);
		pnl_north.add(control_panel, BorderLayout.CENTER);
		frame.add(pnl_north, BorderLayout.NORTH);
			
		final JButton saveGame = new JButton("Save Game");
        saveGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SaveGameToFile(user);
                addScore();
            }
        });
        
        final JButton loadGame = new JButton("Load Game");
        loadGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restart = false;
                if(isNotSaved(board, stats) && !board.hasLost() && !board.hasWon()){
                    new ConfirmClose(board, stats, user);
                    return;
                }
                SavedFileReader saves = new SavedFileReader(user, false);
                saves.chooseFile(board, stats);
                Stats.setPtsNeeded(saves.loadPts());
            }
            
        });
        
        final JButton newGame = new JButton("New Game");
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restart = true;
                if(isNotSaved(board, stats) && !board.hasLost() && !board.hasWon()){
                    new ConfirmClose(board, stats, user);
                    return;
                }
                board.newGame();
                stats.updateStats();
            }
        });
        
        board_panel.add(ins, "INS");
        
        instructions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(insOpen){
                    ((CardLayout)board_panel.getLayout()).show(board_panel, "PLAY");
                    ((CardLayout)stats_panel.getLayout()).show(stats_panel, "STATS");
                    instructions.setText("Instructions");
                    insOpen = false;
                } else {
                    ((CardLayout)board_panel.getLayout()).show(board_panel, "INS");
                    ((CardLayout)stats_panel.getLayout()).show(stats_panel, "SE");
                    instructions.setText("Back to Game");
                    insOpen = true;
                }
                frame.pack();

                
            }
            
        });
        
        final JButton changeUser = new JButton("Change User");
        changeUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                change_frame.setVisible(true);
            }
            
        });

        ((CardLayout)board_panel.getLayout()).show(board_panel, "PLAY");
        
        control_panel.add(instructions);
		control_panel.add(newGame);
		control_panel.add(saveGame);
		control_panel.add(loadGame);
		control_panel.add(changeUser);

		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(false);
		
	}
	
	private void changeUser(JLabel lbl, JComboBox<String> choices, Board board, Stats stats){
	    lbl.setText("Choose an existing user or make a new username");
        String name = (String)choices.getSelectedItem();
        if(name == null || name.equals("")){
            lbl.setText("You need to enter a name!");
            if(user != null){
                return;
            }
        } else if(name.contains(",")){
            lbl.setText("Invalid Name! You can't use the character \',\'");
            if(user != null){
                return;
            }
        }
        if(name != null && ! name.equals("") && !name.contains(",")){
            // changing users
            if(user != null && ! user.equals(name) && !board.hasLost() && !board.hasWon()){
                restart = true;
                if(isNotSaved(board, stats)){
                    new ConfirmClose(board, stats, name);
                } else {
                    user = name;
                    username.setText("Current User: " + name);
                    board.newGame();
                    stats.updateStats();
                }
                change_frame.setVisible(false);
                addScore();
                return;
            }
            
            // selecting name when starting game
            user = name;
            username.setText("Current User: " + user);
            change_frame.setVisible(false);
            frame.setVisible(true);
            Sound.BACKGROUND.loop();
            change_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
	}
	
	private void addScore(){
        try {
            File file = new File(("usersToScores.txt"));
            if(! file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(user + "," + Board.getScore());
            bw.newLine();
            usersToScores.put(user, Board.getScore());
            bw.close();
        } catch (IOException e1) {
        }
	}
	
	private String[] getUsers(){
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader("usersToScores.txt"));
            while((line = br.readLine()) != null){
                String[] info = line.split(",");
                if(info.length == 2){
                    usersToScores.put(info[0], Integer.parseInt(info[1]));
                } else {
                    usersToScores.put(info[0], 0);
                }
            }
            br.close();
        } catch (IOException e) {
            return new String[0];
        }
        return usersToScores.keySet().toArray(new String[usersToScores.size()]);
    }
	
	private boolean isNotSaved(Board board, Stats stats){
	    SavedFileReader saves = new SavedFileReader(user, true);
        Brick[][] bricks = Board.getBricks();
        String b = "";
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 10; c++){
                if(bricks[r][c] == null){
                    b += "n-";
                } else {
                    b += bricks[r][c].strColor();
                    if(bricks[r][c] instanceof PowerUp){
                        b += ((PowerUp)bricks[r][c]).getIntEffect();
                    }
                    b += "-";
                }
            }
            b += " ";
        }
        String curr = Board.getLevel() + "," + Board.getScore() + ","+ Stats.getPtsNeeded() +"," + b;
        if(! saves.contains(curr)){
            System.out.println(curr);
            return true;
        }
        return false;
	}
	

	@SuppressWarnings("serial")
    private class HighScores extends JPanel{
	    public HighScores(){
	        setLayout(new BorderLayout());
            setPreferredSize(new Dimension(Board.BOARD_LENGTH, Board.BOARD_HEIGHT));
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
	    }
	    // best 5 personal scores (out of the currently saved or finished games)
	    public List<Integer> getUserBest(String user){
	        LinkedList<Integer> best = new LinkedList<>();
	        best.add(0);
            best.add(0);
            best.add(0);
            best.add(0);
            best.add(0);
            String line;            
            try {
                BufferedReader br = new BufferedReader(new FileReader("usersToScores.txt"));
                while((line = br.readLine()) != null){
                    String[] info = line.split(",");
                    if(info.length == 2 && info[0].equals(user)){
                        int curr = Integer.parseInt(info[1]);
                        if(curr >= best.get(0)){
                            best.push(curr);
                            best.removeLast();
                        } else if(curr >= best.get(1)){
                            best.add(1, curr);
                            best.removeLast();
                        } else if(curr >= best.get(2)){
                            best.add(2, curr);
                            best.removeLast();
                        } else if(curr >= best.get(3)){
                            best.add(3, curr);
                            best.removeLast();
                        } else if(curr >= best.get(4)){
                            best.add(4, curr);
                            best.removeLast();
                        }
                    }
                }
                br.close();
                return best;
            } catch (IOException e) {
                return Collections.emptyList();
            }
	    }
	    // best 5 players (based on the best score of each player)
	    public List<String> getBestPlayers(){
	        LinkedList<String> out = new LinkedList<>();
	        LinkedList<Integer> scrs = new LinkedList<>();
	        LinkedList<String> users = new LinkedList<>();
	        for(int i = 0; i < 5; i++){
	            scrs.add(0);
	            users.add("___");
	        }
	        for(String p : getUsers()){
	            int best = getUserBest(p).get(0);
	            if(getUserBest(p).get(0) >= scrs.get(0)){
                    scrs.push(best);
                    users.push(p);
                    scrs.removeLast();
                    users.removeLast();
                } else if(getUserBest(p).get(0) >= scrs.get(1)){
                    scrs.add(1, best);
                    users.add(1, p);
                    scrs.removeLast();
                    users.removeLast();
                } else if(getUserBest(p).get(0) >= scrs.get(2)){
                    scrs.add(2, best);
                    users.add(2, p);
                    scrs.removeLast();
                    users.removeLast();
                } else if(getUserBest(p).get(0) >= scrs.get(3)){
                    scrs.add(3, best);
                    users.add(3, p);
                    scrs.removeLast();
                    users.removeLast();
                } else if(getUserBest(p).get(0) >= scrs.get(4)){
                    scrs.add(4, best);
                    users.add(4, p);
                    scrs.removeLast();
                    users.removeLast();
                }
	        }
	        for(int i = 0; i < 5; i++){
	            out.add((i+1) + ". "+users.get(i)+", score: "+ scrs.get(i));
	        }
	        return out;
	    }
	    
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
	            int px = Board.BOARD_LENGTH/2;
	            g.drawString(user+"'s top 5 scores:", px, 25);
	            for(int i = 0; i < 5; i++){
	                g.drawString((i+1)+". "+getUserBest(user).get(i), px, 75+50*i);
	            }

                g.drawString("Top 5 Players:", px, 325);
                for(int i = 0; i < 5; i++){
                    g.drawString(getBestPlayers().get(i), px, 375+50*i);
                }
            

        }
	    
	}
	
	@SuppressWarnings("serial")
    private class Instructions extends JPanel{
	    public Instructions(){
            setPreferredSize(new Dimension(Board.BOARD_LENGTH, Board.BOARD_HEIGHT));
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            repaint();
	    }
	    
	    @Override
	    public void paintComponent(Graphics g){
	        super.paintComponent(g);
	        g.drawString("Instructions", 0, 15);
	        g.drawString("1: Each brick is worth 10 points. You need to earn " +
	        		     "a certain number of points and", 0, 100);
	        g.drawString("exhaust all moves to move on to the next level.", 0, 150);
	        g.drawString("2: If there are at least 2 bricks of the same " +
	        		     "color, then you can click once to", 0, 250);
	        g.drawString("select this group of bricks. Click again to destroy" +
	        		     " these bricks and earn points.", 0, 300);
	        g.drawString("If you decide to not destroy these bricks, you can " +
	        		     "click on any brick that is ", 0, 350);
	        g.drawString("not selected.", 0, 400);
	        g.drawString("3: Each \"special\" brick has its own effects. " +
	        		     "Destroying the brick will activate it.", 0, 500);
	    }
	}
	
	   @SuppressWarnings("serial")
	    private class SpecialEffects extends JPanel{
	        public SpecialEffects(){
	            setPreferredSize(new Dimension(Stats.WIDTH, Stats.HEIGHT));
	            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
	            repaint();
	        }
	        
	        @Override
	        public void paintComponent(Graphics g){
	            super.paintComponent(g);
	            g.setColor(Color.BLACK);
	            g.drawString("Double Brick: when", 0, 15);
	            g.drawString("destroyed, its group", 0, 40);
	            g.drawString("will give you twice", 0, 65);
	            g.drawString("as many points", 0, 90);
	            g.setColor(Color.ORANGE);
	            g.fillRect(0, 105, Brick.LENGTH, Brick.HEIGHT);
	            g.setColor(Color.BLACK);
	            g.drawRect(0, 105, Brick.LENGTH, Brick.HEIGHT);
	            g.drawString("x2", Brick.HEIGHT/2+9, 100+Brick.HEIGHT);
	            g.drawString("Delete Row Brick: all", 0, 125+Brick.HEIGHT);
                g.drawString("bricks in its row will", 0, 150+Brick.HEIGHT);
                g.drawString("be destroyed", 0, 175+Brick.HEIGHT);
                g.setColor(Color.RED);
                g.fillRect(0, 185+Brick.HEIGHT, Brick.LENGTH, Brick.HEIGHT);
                g.setColor(Color.BLACK);
                g.drawRect(0, 185+Brick.HEIGHT, Brick.LENGTH, Brick.HEIGHT);
                g.fillRect(0, 210+Brick.HEIGHT, Brick.LENGTH, 5);
                g.drawString("Delete Column Brick:", 0, 250+Brick.HEIGHT);
                g.drawString("all bricks in its column", 0, 275+Brick.HEIGHT);
                g.drawString("will be destroyed", 0, 300+Brick.HEIGHT);
                g.setColor(Color.BLUE);
                g.fillRect(0, 310+Brick.HEIGHT, Brick.LENGTH, Brick.HEIGHT);
                g.setColor(Color.BLACK);
                g.drawRect(0, 310+Brick.HEIGHT, Brick.LENGTH, Brick.HEIGHT);
                g.fillRect(Brick.LENGTH/2, 310+Brick.HEIGHT, 5, Brick.LENGTH/2);
                g.drawString("Add Column Brick:", 0, 375+Brick.HEIGHT);
                g.drawString("a new row of bricks", 0, 400+Brick.HEIGHT);
                g.drawString("will fall down", 0, 425+Brick.HEIGHT);
                g.setColor(Color.GREEN);
                g.fillRect(0, 440+Brick.HEIGHT, Brick.LENGTH, Brick.HEIGHT);
                g.setColor(Color.BLACK);
                g.drawRect(0, 440+Brick.HEIGHT, Brick.LENGTH, Brick.HEIGHT);
                g.setColor(Color.CYAN);
                g.fillOval(Brick.HEIGHT/2+10, 445+Brick.HEIGHT, 20, 20);
                g.setColor(Color.BLACK);
                g.drawString("Rainbow Brick:", 0, 500+Brick.HEIGHT);
                g.drawString("it can join any group", 0, 525+Brick.HEIGHT);
                g.setColor(Color.WHITE);
                g.fillRect(0, 540+Brick.HEIGHT, Brick.LENGTH, Brick.HEIGHT);
                g.setColor(Color.CYAN);
                g.fillRect(0, 540+Brick.HEIGHT+Brick.HEIGHT/2, Brick.LENGTH, 5);
                g.setColor(Color.MAGENTA);
                g.fillRect(Brick.LENGTH/2, 540+Brick.HEIGHT, 5, Brick.LENGTH/2);
                g.setColor(Color.BLACK);
                g.drawRect(0, 540+Brick.HEIGHT, Brick.LENGTH, Brick.HEIGHT);
	        }
	    }
	
	@SuppressWarnings("serial")
    private class ConfirmClose extends JFrame{
        public ConfirmClose(final Board board, final Stats stats, final String name){
            setLocation(300, 300);
            setPreferredSize(new Dimension(300, 100));
            setLayout(new FlowLayout());
            setResizable(false);
            JPanel panelSouth = new JPanel();
            panelSouth.setLayout(new BorderLayout());
            JLabel lbl = new JLabel("Are you sure you want to quit without saving?");
            JButton btn_yes = new JButton("Yes");
            getRootPane().setDefaultButton(btn_yes);
            btn_yes.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    user = name;
                    username.setText("Current User: " + name);
                    ((CardLayout)board_panel.getLayout()).show(board_panel, "PLAY");
                    ((CardLayout)stats_panel.getLayout()).show(stats_panel, "STATS");
                    instructions.setText("Instructions");
                    insOpen = false;
                    if(restart && !board.hasLost() && !board.hasWon()){
                        board.newGame();
                        stats.updateStats();
                    } else {
                        SavedFileReader saves = new SavedFileReader(user, false);
                        saves.chooseFile(board, stats);
                        Stats.setPtsNeeded(saves.loadPts());
                    }
                }
            });
            JPanel pnl_yes = new JPanel();
            pnl_yes.add(btn_yes);
            JPanel pnl_no = new JPanel();
            JButton btn_no = new JButton("No");
            btn_no.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            pnl_yes.add(btn_no);
            panelSouth.add(pnl_yes, BorderLayout.WEST);
            panelSouth.add(pnl_no, BorderLayout.EAST);
            this.add(lbl);
            this.add(panelSouth);
            pack();
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }
    }
	
	/*
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it IMPORTANT: Do NOT delete! You MUST include
	 * this in the final submission of your game.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
	
}
