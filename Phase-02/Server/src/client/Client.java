import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    static String ticket;
    private static class listenRunnable implements Runnable {
        Scanner console;
        BufferedReader input;
        public listenRunnable(Scanner console, BufferedReader input) {
            this.console = console;
            this.input = input;
        }
        @Override
        public void run() {
            String reply;
            try {
                do {
                    reply = input.readLine();
                    String command = reply.split(" ", 1)[0];

                    if (!command.equals("ticket")) {
                        System.out.println();
                    }

                    switch (command) {
                        case "ticket": Client.setTicket(reply.split(" ")[1]); break;
                        case "menu": Client.printWithPrefix(Client.getMenu(
                                reply.split(" ")[1].split(","),
                                reply.split(" ")[2].split(",")
                        )); break;
                        case "list": Client.printWithPrefix(Client.getList(
                                reply.split(" ")[1],
                                reply.split(" ")[2].split(",")
                        )); break;
                        case "join": Client.printWithPrefix(
                                "User '" + reply.split(" ")[2]
                                        + "' has joined room '" + reply.split(" ")[1] + "'"
                        ); break;
                        case "leave": Client.printWithPrefix(
                                "User '" + reply.split(" ")[2]
                                        + "' has left room '" + reply.split(" ")[1] + "'"
                        ); break;
                        case "kick": Client.printWithPrefix(
                                "User '" + reply.split(" ")[2]
                                        + "' was kicked from room '" + reply.split(" ")[1] + "'"
                        ); break;
                        case "room": Client.printWithPrefix(
                                "Room message from user " + reply.split(" ")[1] + ": " + reply.split(" ")[2]
                        ); break;
                        case "direct": Client.printWithPrefix(
                                "Direct message from user " + reply.split(" ")[1] + ": " + reply.split(" ")[2]
                        ); break;
                        case "info": Client.printWithPrefix("Info: " + reply.split(" ")[1]); break;
                        case "error": System.err.println("Error: " + reply.split(" ")[1]); break;
                        default:
                            System.err.println("Unknown command received! Command: " + reply);
                    }
                    System.out.println(">");
                } while (reply != null);
                System.out.println("Exited while loop");
            } catch (SocketException e) {
                Client.printWithPrefix(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class sendRunnable implements Runnable {
        Scanner console;
        PrintWriter output;
        public sendRunnable(Scanner console, PrintWriter output) {
            this.console = console;
            this.output = output;
        }
        @Override
        public void run() {
            String request;

            do {
                System.out.println("Supported Commands: \n" +
                        "menu\n" +
                        "list [room]\n" +
                        "join [room]\n" +
                        "leave [room]\n" +
                        "kick [room] [user] [reason]\n" +
                        "send [room] [message]\n" +
                        "direct [user] [message]\n");
                System.out.print(">");
                request = console.nextLine();
                output.println(request);
            } while (request != null);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket;
        while (true) {
            try {
                Client.printWithPrefix("Connecting...");
                socket = new Socket("localhost", 13337);
                break;
            } catch (ConnectException e) {
                Client.printWithPrefix(e.getMessage());
                Thread.sleep(2500);
            }
        }
        Scanner console = new Scanner(System.in);
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader input =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Identification
        String command;
        do {
            command = input.readLine();
        } while (!command.equals("ident"));

        String ticket = getTicket();

        if (ticket == null) {
            System.out.print("Enter your nickname: ");
            String nickname = console.nextLine();
            output.println(nickname);
        } else {
            output.println(ticket);
        }


        Thread listen = new Thread(new listenRunnable(console, input));
        Thread send = new Thread(new sendRunnable(console, output));

        // Starting the threads for listening and sending commands
        listen.start();
        send.start();

        // Waiting for the threads to finish before terminating the connection
        send.join();
        listen.join();

        // Closing resources
        console.close();
        input.close();
        output.close();
        socket.close();
    }

    public static String getTicket() {
        return ticket;
    }

    public static void setTicket(String ticket) {
        Client.ticket = ticket;
    }

    static void printWithPrefix(String message) {
        System.out.println(timestamp() + " " + message);
    }

    static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static String getMenu(String[] users, String[] rooms) {
        StringBuilder menu = new StringBuilder("Available Rooms:\n");
        for (String room: rooms) {
            menu.append(room);
        }

        menu.append("Connected Users:");
        for (String user: users) {
            menu.append(user);
        }

        return menu.toString();
    }

    private static String getList(String room, String[] users) {
        StringBuilder list = new StringBuilder("Room: " + room);

        list.append("Connected Users:");
        for (String user: users) {
            list.append(user);
        }

        return list.toString();
    }
}
