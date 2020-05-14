import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class GuessingGameShowClient {
    private static final int max = 1000000;
    //----------------------------------------------
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 8000);
        System.out.println("-------- Connection with server is established --------------");
        int playersActualNumber = (int) (Math.random() * max + 1);
        new Thread(new GuessingGameShow_PlayerThread_withBank("player name", playersActualNumber, max, 100, socket)).start();
    }
    public static class GuessingGameShow_PlayerThread_withBank implements Runnable {
        private final String name;
        private int guessNumber;
        private int guessCount;
        private int high;
        private int low;
        private final int actualNumber;
        private int playersBet;
        private int playersBank;
        private final ObjectOutputStream objOut;
        private final ObjectInputStream objIn;
        private final DataOutputStream dataOut;

        public GuessingGameShow_PlayerThread_withBank(String name, int pan, int max, int psb, Socket socket) throws IOException {
            this.name = name;
            this.actualNumber = pan;
            this.high = max;
            this.low = 1;
            this.guessCount = 0;
            this.playersBank = psb;
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objIn = new ObjectInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        }

        // ------------------------------------------------------------
        //               get() methods go here
        // ------------------------------------------------------------

        public int getGuessNumber() {
            return guessNumber;
        }

        public int getActualNumber() {
            return actualNumber;
        }

        public int getPlayersBet() {
            return playersBet;
        }

        public int getPlayersBank() {
            return playersBank;
        }

        public void addToPlayersBank(int amount) {
            playersBank += amount;
        }

        public void subtractFromPlayersBank(int amount) {
            playersBank -= amount;
        }

        public void run() {
            String resultingString = "";
            while (guessNumber != actualNumber && playersBank > 0) {
                try {
                    dataOut.writeInt(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bank bank = null;
                try {
                    bank = (Bank) objIn.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                guessCount++;
                guessNumber = (low + high) / 2;
                // NEW: figure playersBet and call bank's bet.
                // >>>>>>>>> YOUR CODE HERE <<<<<<<<<<
                playersBet = (int) (((int) (Math.random() * 10) + 1) / 100.0 * playersBank);
                assert bank != null;
                bank.bet(this);
                // >>>>>>>>> END OF YOUR CODE <<<<<<<<<<
                resultingString = name + ": guessNumber = " + guessNumber + "\t guessCount = " + guessCount + "\t playersBet = " + playersBet;
                if (guessNumber < actualNumber) {
                    low = guessNumber;
                    resultingString += " Guess too LOW! You lose " + playersBet + " playersBank = " + playersBank;
                } else if (guessNumber > actualNumber) {
                    high = guessNumber;
                    resultingString += " Guess too HIGH! You lose " + playersBet;
                } else {  // (guessNumber == actualNumber)
                    resultingString += " CORRECT! You win " + playersBank;
                }
                try {
                    objOut.writeObject(bank);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(resultingString + " Thread: " + this);
            } // while (guessNumber!=actualNumber)
            if (guessNumber == actualNumber)
                System.out.println(">>>>> " + name + ": You got with guessCount = " + guessCount + " actualNumber = " + actualNumber + " playersBank = " + playersBank);
            else
                System.out.println(">>>>> " + name + ": You lost the game with guessCount = " + guessCount + " actualNumber = " + actualNumber + " playersBank = " + playersBank);
            try {
                dataOut.writeInt(2);
                objOut.writeObject(new GameResult(actualNumber, guessNumber, guessCount, playersBank));
                objOut.close();
                objIn.close();
                dataOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // run - thread dies
    }  // GuessingGameShow_PlayerThread_withBank
}
