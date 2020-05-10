import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SimpleTextTesterClient {
    public static void main(String[] args) throws IOException {
        int port = 8000;
        String host = "localhost" ; // "localhost"; // "209.6.90.131"

        System.out.println("SimpleTextTesterClient attempting to connect to server.");
        Socket serverSocket = new Socket(host, port);
        System.out.println("Successfully connected to host " + host + " at port " + port + ".");
        Scanner in = new Scanner(System.in);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream());

        while (true) {
            out.println(in.nextLine());
            out.flush();
        }
    }
}

