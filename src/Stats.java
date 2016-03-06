import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


@SuppressWarnings("serial")
public class Stats extends JPanel{

    public static final int WIDTH = 200;
    public static final int HEIGHT = 16*Brick.HEIGHT;
    public static final int INTERVAL = 35;
    private static int levelScr  = 0;
    private static int prevLvl = 0;
    private static int prevScr = 0;
    private static int ptsNeeded = 0;
    private JTextArea lvl;
    private JTextArea scr;
    private JTextArea nxt;
    private JCheckBox hint;
    private static JCheckBox sound;
    
    public Stats(){
        setLayout(new GridLayout(5, 1));
        
        JPanel lvl_panel = new JPanel();
        lvl = new JTextArea("CURRENT LEVEL: " + Board.getLevel(), 1, 8);
        lvl.setLineWrap(true);
        lvl.setWrapStyleWord(true);
        lvl.setEditable(false);
        lvl.setFont(new Font(lvl.getFont().getName(), Font.BOLD, 20));
        lvl_panel.add(lvl);
        this.add(lvl_panel);
        
        JPanel scr_panel = new JPanel();
        scr = new JTextArea("CURRENT SCORE: " + Board.getScore(), 2, 8);
        scr.setLineWrap(true);
        scr.setWrapStyleWord(true);
        scr.setEditable(false);
        scr.setFont(new Font(scr.getFont().getName(), Font.BOLD, 20));
        scr_panel.add(scr);
        this.add(scr_panel);
        
        JPanel nxt_panel = new JPanel();
        nxt = new JTextArea("POINTS NEEDED TO GET TO NEXT LEVEL: " + Board.getPts(), 3, 8);
        nxt.setLineWrap(true);
        nxt.setWrapStyleWord(true);
        nxt.setEditable(false);
        nxt.setFont(new Font(nxt.getFont().getName(), Font.BOLD, 20));
        nxt_panel.add(nxt);
        this.add(nxt_panel);
        
        JPanel hint_panel = new JPanel();
        JLabel hint_lbl = new JLabel("Hints: ");
        hint = new JCheckBox();
        if(Board.isHintOn()){
            hint.setSelected(true);
        } else {
            hint.setSelected(false);
        }
        hint.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Board.flipHint();    
                if(Board.isHintOn()){
                    hint.setSelected(true);
                } else {
                    hint.setSelected(false);
                }
            }
        });
        hint_lbl.setFont(new Font(nxt.getFont().getName(), Font.BOLD, 20));
        hint_panel.add(hint_lbl);
        hint_panel.add(hint);
        this.add(hint_panel);
        
        JPanel sound_panel = new JPanel();
        JLabel sound_lbl = new JLabel("Sound: ");
        sound = new JCheckBox();
        sound.setSelected(true);
        sound.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sound.isSelected()){
                    Sound.BACKGROUND.loop();
                } else {
                    Sound.BACKGROUND.stop();
                }
            }
        });
        sound_lbl.setFont(new Font(nxt.getFont().getName(), Font.BOLD, 20));
        sound_panel.add(sound_lbl);
        sound_panel.add(sound);
        this.add(sound_panel);
    }
    
    public static boolean hasSound(){
        return sound.isSelected();
    }
    
    public void updateStats(){
        lvl.setText("CURRENT LEVEL: " + Board.getLevel());
        ptsNeeded = getPtsNeeded();
        scr.setText("CURRENT SCORE: " + Board.getScore());
        nxt.setText("POINTS NEEDED TO GET TO NEXT LEVEL: " + ptsNeeded);
        repaint();
    }
    
    public void loadStats(){
        lvl.setText("CURRENT LEVEL: " + Board.getLevel());
        scr.setText("CURRENT SCORE: " + Board.getScore());
        nxt.setText("POINTS NEEDED TO GET TO NEXT LEVEL: " + ptsNeeded);
        repaint();
    }
    
    public static void setPtsNeeded(int i){
        prevLvl = Board.getLevel();
        prevScr = Board.getScore();
        ptsNeeded = i;
    }
    
    public static int getPtsNeeded(){
        if(Board.getLevel() != prevLvl){
            prevLvl = Board.getLevel();
            levelScr = 0;
            prevScr = Board.getScore();
            ptsNeeded = Board.getPts();
            levelScr += Board.getScore() - prevScr;
        } else {
            if(Board.getScore() != prevScr){
                levelScr += Board.getScore() - prevScr;
                prevScr = Board.getScore();
            }
            ptsNeeded = Board.getPts() - levelScr;
            if(ptsNeeded < 0){
                ptsNeeded = 0;
            }
        }
        return ptsNeeded;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
}
