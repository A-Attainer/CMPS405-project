import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket = new Socket("localhost", 13333);

        // Creating a thread for listening
        Runnable listenRunnable = () -> {
            String reply;
            try {
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));
                do {
                    reply = input.readLine();
                    System.out.println(reply);
                } while (reply != null);
                input.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Thread listen = new Thread(listenRunnable);
        listen.start();

        // Creating a thread for sending commands
        Runnable sendRunnable = () -> {
            String request;
            Scanner console = new Scanner(System.in);

            try {
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                do {
                    request = console.nextLine();
                    output.println(request);
                } while (request != null);
                output.close();
                console.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        Thread send = new Thread(sendRunnable);
        send.start();

        // Waiting for the threads to finish before terminating the connection
        send.join();
        listen.join();

        socket.close();
    }
}
