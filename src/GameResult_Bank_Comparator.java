import java.util.*;
public class GameResult_Bank_Comparator implements Comparator<GameResult> {
    @Override
    public int compare(GameResult t1, GameResult t2) {
        return (t1.playerBank-t2.playerBank);
    }
}