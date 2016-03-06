import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SavedFileReader {
    private BufferedReader br;
    private String line;
    private JFrame noSavesFrame;
    private JFrame saves;
    private final String user;
    private String file;
    
    // [Level], [Score], [Points needed], [bricks]
    
    public SavedFileReader(String user, boolean checking){
        this.user = user;
        try {
            noSavesFrame = new JFrame("No Saved Games");
            noSavesFrame.setLocation(300, 300);
            noSavesFrame.setPreferredSize(new Dimension(300, 100));
            noSavesFrame.setResizable(false);
            JLabel lbl = new JLabel("You don't have any saved games");
            noSavesFrame.add(lbl);
            noSavesFrame.pack();
            noSavesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            br = new BufferedReader(new FileReader(user+"Saves.txt"));
            
            saves = new JFrame("Saved Games");
            saves.setLocation(400, 300);
            saves.setPreferredSize(new Dimension(300, 100));
            saves.setResizable(false);
            saves.pack();
            saves.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
        } catch (IOException e) {
            if(! checking){
                noSavesFrame.setVisible(true);
            }
        }
    }
    
    public String[] getUserFiles(String user) throws IOException{
        Set<String> files = new TreeSet<>();
        if(br == null){
            throw new IOException();
        }
        while((line = br.readLine()) != null){
            if(! line.equals("")){
                files.add(line);
            }
        }
        if(files == Collections.EMPTY_SET){
            noSavesFrame.setVisible(true);
        }
        return files.toArray(new String[files.size()]);
    }
    
    public void chooseFile(final Board board, final Stats stats){
        List<String> out = new ArrayList<>();
        final String[] files;
        file = null;
        try {
            files = getUserFiles(user);
        } catch (IOException e) {
            noSavesFrame.setVisible(true);
            return;
        }
        int i = 1;
        for(String f : files){
            String[] info = f.split(",");
            out.add("Save " + i + ". Level: " + info[0] + ", Score: " + info[1]);
            i++;
        }
        JButton ok = new JButton("OK");
        final JComboBox<String> saveFiles = new JComboBox<>(out.toArray(new String[out.size()]));

        saveFiles.setPreferredSize(new Dimension(200, 20));
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saves.setVisible(false);
                int index = saveFiles.getSelectedIndex();
                file = files[index];
                board.loadGame(loadLevel(), loadScore(), loadPts(), loadBricks());
                Stats.setPtsNeeded(loadPts());
                stats.loadStats();
            }
        });
        JPanel saves_pnl = new JPanel();
        saves_pnl.add(saveFiles);
        saves_pnl.add(ok);
        saves.add(saves_pnl);
        saves.setVisible(true);
        saves.getRootPane().setDefaultButton(ok);
        
    }
    
    public boolean contains(String str){
        try {
            br = new BufferedReader(new FileReader(user + "Saves.txt"));
            while((line = br.readLine()) != null){
                if(line.equals(str)){
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
    
    public int loadLevel(){
        if(file != null){
            return Integer.parseInt(file.split(",")[0]);
        } else {
            return Board.getLevel();
        }
    }
    
    public int loadScore(){
        if(file != null){
            return Integer.parseInt(file.split(",")[1]);
        } else {
            return Board.getScore();
        }
    }
    
    public int loadPts(){
        if(file != null){
            return Integer.parseInt(file.split(",")[2]);
        } else {
            return Board.getPts();
        }
    }
    
    public Brick[][] loadBricks(){
        if(file != null){
            Brick[][] bricks = new Brick[16][10];
            String[] colors = file.split(",")[3].split(" ");
            int r = 0;
            int c = 0;
            for(String cRow : colors){
                for(String cCol : cRow.split("-")){
                    if(cCol.length() == 1){
                        if(cCol.equals("n")){
                            bricks[r][c] = null;
                        } else {
                            bricks[r][c] = new Brick(cCol);
                        }
                    } else {
                        Effect e = Effect.getEffect(Integer.parseInt
                                   (((Character)cCol.charAt(1)).toString()));
                        if (e.equals(Effect.DOUBLE)){
                            bricks[r][c] = new DoubleBrick(((Character)cCol.
                                           charAt(0)).toString());
                        } else if (e.equals(Effect.REMOVE_COL)){
                            bricks[r][c] = new RemoveColBrick(((Character)cCol.
                                           charAt(0)).toString());
                        } else if (e.equals(Effect.REMOVE_ROW)){
                            bricks[r][c] = new RemoveRowBrick(((Character)cCol.
                                           charAt(0)).toString());
                        } else if (e.equals(Effect.ADD_ROW)){
                            bricks[r][c] = new AddRowBrick(((Character)cCol.
                                           charAt(0)).toString());
                        } else if (e.equals(Effect.BLAST)){
                            bricks[r][c] = new BlastBrick(((Character)cCol.
                                    charAt(0)).toString());
                        } else if (e.equals(Effect.RAINBOW)){
                            bricks[r][c] = new RainbowBrick(((Character)cCol.
                                    charAt(0)).toString());
                 }
                    }
                    c++;
                    if(c >= 10){
                        c = 0;
                    }
                }
                
                r++;
            }
            return bricks;
        } else {
            return Board.getBricks();
        }
    }
    
}
