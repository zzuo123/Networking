import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChatServer extends Application {
    private final TextArea ta = new TextArea();
    private int clientNo = 0;
    private String broadCastMsg = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("Chat Server");
        primaryStage.setScene(scene);
        primaryStage.show();
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8000);
                ta.appendText("ChatServer started at " + new Date() + '\n');
                while (true) {
                    Socket socket = serverSocket.accept();
                    clientNo++;
                    Platform.runLater(() -> {
                        ta.appendText("Starting thread for client " + clientNo + " at " + new Date() + '\n');
                        InetAddress inetAddress = socket.getInetAddress();
                        ta.appendText("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
                        ta.appendText("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
                    });
                    new Thread(new HandleAClient(socket, clientNo)).start();
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

    class HandleAClient implements Runnable {
        private final Socket socket;
        private final int clientNum;
        private String currentString;

        public HandleAClient(Socket socket, int num) {
            this.socket = socket;
            this.clientNum = num;
            this.currentString = "";
        }

        public void run() {
            try {
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
                new Thread(() -> {
                    try {
                        outputToClient.writeUTF("You are connected to the server!");
                        while (true) {
                            TimeUnit.MILLISECONDS.sleep(100);
                            if (!currentString.equals(broadCastMsg)) {
                                outputToClient.writeUTF(broadCastMsg);
                                currentString = broadCastMsg;
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        System.err.println(e);
                        ;
                    }
                }).start();
                while (true) {
                    String messageFromClient = inputFromClient.readUTF();
                    Platform.runLater(() -> {
                        ta.appendText("Message received from client" + clientNum + ": " + messageFromClient + '\n');
                    });
                    broadCastMsg = "Client#" + clientNum + " sent: " + messageFromClient;
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}