
public enum Effect {
    DOUBLE, REMOVE_ROW, REMOVE_COL, ADD_ROW, BLAST, RAINBOW;
    
    public static Effect getEffect(int i){
        if(i == 0){
            return DOUBLE;
        }
        if(i == 1){
            return REMOVE_ROW;
        }
        if(i == 2){
            return REMOVE_COL;
        }
        if(i == 3){
            return ADD_ROW;
        }
        if(i == 4){
            return BLAST;
        }
        if(i == 5){
            return RAINBOW;
        }
        return null;
    }
    
}
