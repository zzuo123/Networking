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
    private TextArea ta = new TextArea();
    private int clientNo = 0;
    private String broadCastMsg = "";

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("Chat Server"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        new Thread( () -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);
                ta.appendText("ChatServer started at " + new Date() + '\n');

                while (true) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();
                    // Increment clientNo
                    clientNo++;
                    Platform.runLater(() -> {
                        // Display the client number
                        ta.appendText("Starting thread for client " + clientNo + " at " + new Date() + '\n');
                        // Find the client's host name, and IP address
                        InetAddress inetAddress = socket.getInetAddress();
                        ta.appendText("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
                        ta.appendText("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
                    });
                    // Create and start a new thread for the connection
                    new Thread(new HandleAClient(socket, clientNo)).start();
                }
            }
            catch(IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

    // Define the thread class for handling new connection
    class HandleAClient implements Runnable {
        private Socket socket; // A connected socket
        private int clientNum;
        private String currentString;

        /** Construct a thread */
        public HandleAClient(Socket socket, int num) {
            this.socket = socket;
            this.clientNum = num;
            this.currentString ="";
        }

        /** Run a thread */
        public void run() {
            try {
                // Create data input and output streams
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
                new Thread(()->{
                    try {
                        outputToClient.writeUTF("You are connected to the server!");
                        while (true){
                            TimeUnit.SECONDS.sleep(1);
                            if(!currentString.equals(broadCastMsg)){
                                outputToClient.writeUTF(broadCastMsg);
                                currentString = broadCastMsg;
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        System.err.println(e);;
                    }
                }).start();
                // Continuously serve the client
                while (true) {
                    // Receive message from the client
                    String messageFromClient = inputFromClient.readUTF();
                    Platform.runLater(() -> {
                        ta.appendText("Message received from client"+clientNum+": " + messageFromClient + '\n');
                    });
                    broadCastMsg = "Client#"+clientNum+" sent: "+messageFromClient;
                }
            }
            catch(IOException ex) {
                System.err.println(ex);
            }
        }
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}