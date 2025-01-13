

import java.io.*;
        import java.net.*;
        import java.util.Scanner;

public class TestClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 13337;
    private static String ticket;

    public static void main(String[] args) {
        try { Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner console = new Scanner(System.in);

//            ticket = getTickets();


            Thread readerThread = new Thread(() -> readFromServer(input));
            Thread writerThread = new Thread(() -> writeToServer(output, console));

            readerThread.start();
            writerThread.start();

            // Wait for both threads to finish
            readerThread.join();
            writerThread.join();



        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


//    private static String getTicket() {
//
//    }


    private static void readFromServer(BufferedReader input) {
        try {
            while (true) {
                String response = input.readLine();
                if (response == null) {
                    System.out.println("Server closed the connection.");
                    break; // Exit the loop if the server closes the connection
                }
                System.out.println("Response from server: " + response);
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    // Step 2: Write messages to the server
    private static void writeToServer(PrintWriter output, Scanner console) {
        while (true) {
            String input = console.nextLine(); // Read user input
            System.out.println("Sending to server: " + input); // Debugging
            output.println(input); // Send to server

        }
    }


//    private static String timestamp() {
//    }
}
