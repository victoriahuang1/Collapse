import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class SaveGameToFile {
    public SaveGameToFile(String user){
        try{
            File file = new File(user+"Saves.txt");
 
            if(!file.exists()){
                file.createNewFile();
            }
 
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
            
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
        bufferWriter.newLine();
        bufferWriter.write(Board.getLevel() + "," + Board.getScore() + ","
                + Stats.getPtsNeeded() + "," + b);
        bufferWriter.close();
 
        }catch(IOException e){
        }
    }
}
