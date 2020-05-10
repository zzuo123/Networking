// Copy both in and if using BlueJ, you put the SimpleTextTesterServer in one package (folder)
// and the SimpleTextTesterClient in another and run both.
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SimpleTextTesterServer {
    public static void main(String[] args) throws IOException {
        int port = 8000;
        ServerSocket server = new ServerSocket(port);

        System.out.println("SimpleTextTesterServer waiting on client.");
        Socket clientSocket = server.accept();
        System.out.println("Client connected to server.");
        Scanner in = new Scanner(clientSocket.getInputStream());
        //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            System.out.println(in.nextLine());
        }

    }
}
// ======================================================================