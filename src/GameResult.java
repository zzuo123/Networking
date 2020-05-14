import java.io.Serializable;

public class GameResult implements Serializable {
    public String name;
    public int actualNumber;
    public int guessNumber;
    public int guessCount;
    public int playerBank;
    public GameResult(int actualNumber, int guessNumber, int guessCount, int playersbank){
        this.actualNumber = actualNumber;
        this.guessCount = guessCount;
        this.guessNumber = guessNumber;
        this.playerBank = playersbank;
    }
}
