import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GuessingGameShow_Server {
    private static Bank publicBank = new Bank(100);
    private static boolean bankIsHere = true;
    private static final Lock lock = new ReentrantLock();
    private static final Condition bankHere = lock.newCondition();
    private static final int numPlayers = 3;
    private static GameResult[] gr = new GameResult[numPlayers];

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8000);
        System.out.println("------------ Server is created !!! ---------------");
        int playerNum = 1;
        ExecutorService executor = Executors.newFixedThreadPool(numPlayers);
        HandleAClient[] clientPool = new HandleAClient[numPlayers];
        while(playerNum <= numPlayers){
            Socket socket = server.accept();
            System.out.println("Player"+playerNum+" established connection, waiting for "+(numPlayers-playerNum)+" more players to join...");
            clientPool[playerNum-1] = new HandleAClient(socket, playerNum);
            playerNum++;
        }
        for(HandleAClient client : clientPool){
            executor.execute(client);
        }
        executor.shutdown();
        while (!executor.isTerminated()) ; // stall / wait for all threads to stop
        System.out.println("\n\n\t Sort and Print threadArray: ");
        Arrays.sort(gr, new GameResult_Bank_Comparator());
        for(GameResult gpt : gr){
            System.out.println("Name = " + gpt.name + ": " +
                    "ActualNumber = " + gpt.actualNumber + "   " +
                    "GuessNumber = " + gpt.guessNumber + "   " +
                    "GuessCount = " + gpt.guessCount + "   " +
                    "PlayersBank = " + gpt.playerBank + "   ");
        }
    }

    // pass a bank to client to guess and then make it passed back
    private static class HandleAClient implements Runnable {
        private final Socket socket;
        private final String playerName;
        private final int playerNum;
        public HandleAClient(Socket socket, int playerNum) {
            this.socket = socket;
            this.playerName = "Player"+playerNum;
            this.playerNum = playerNum;
        }

        public void run() {
            try {
                ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                boolean playing = true;
                while(playing){
                    int request = dataIn.readInt();
                    if(request == 1){   //request for a bank
                        System.out.println(playerName+" requested Bank");
                        objOut.writeObject(getPublicBank(playerName));
                        Bank temp = (Bank) objIn.readObject();
                        putBackBank(temp, playerName);
                    }else if(request == 2){  //user finished guessing
                        gr[playerNum-1] = (GameResult) objIn.readObject();
                        gr[playerNum-1].name = playerName;
                        playing = false;
                        System.out.println(playerName+" Finished Guessing!");
                    }
                }
                objOut.close();
                objIn.close();
                dataIn.close();
                Thread.currentThread().interrupt();
            } catch (IOException | ClassNotFoundException | InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    private static Bank getPublicBank(String playerName) throws InterruptedException {
        lock.lock();
        while(!bankIsHere)
            bankHere.await();
        bankIsHere = false;
        lock.unlock();
        System.out.println("Gave Bank to "+playerName);
        return publicBank;
    }
    public static void putBackBank(Bank bank, String playerName){
        lock.lock();
        publicBank = bank;
        System.out.println(playerName+" has put bank back");
        bankIsHere = true;
        bankHere.signalAll();
        lock.unlock();
    }
}
