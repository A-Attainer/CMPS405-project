
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;

public class User implements Runnable {
    private Socket client;
    private String ticket;
    private Room currentRoom;
    private String pseudonym;
    private PrintWriter output;

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Socket getClient() {
        return client;
    }

    public String getTicket() {
        return ticket;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public User(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            System.out.println(client.getInetAddress().getHostAddress());
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
            String request;
            ident(input);
            do {
                request = input.readLine(); // Read the client's request
                if (request != null) {
                    String[] parts = request.trim().split(" ", 2); // Split the command and arguments
                    String command = parts[0];
                    String arguments = parts.length > 1 ? parts[1] : null;
                    System.out.println(command);
                    switch (command) {
                        case "pseudo":
                            this.pseudonym = arguments;
                            ticket = generateTickets(arguments);
                            Server.addUser(this, ticket);
                            Server.addTicket(ticket, pseudonym);
                            output.println("ticket  " + ticket);
                            break;
                        case "ticket":
                            handleTicket(arguments, output);
                            break;
                        case "join":
                            handleJoin(arguments, output);
                            break;
                        case "leave":
                            handleLeave(output);
                            break;
                        case "kick":
                            handleKick(arguments, output);
                            break;
                        case "send":
                            handleSend(arguments, output);
                            break;
                        case "direct":
                            handleDirect(arguments, output);
                            break;
                        case "menu":
                            handleMenu(output);
                        default:
                            output.println("error unknown command, please try again.");
                    }
                }
            } while (true);

        } catch (IOException e) {
            System.out.println(e.getMessage());

        }
    }

    private void ident(BufferedReader input) throws IOException {
        sendMessage("ident");
        String reply = input.readLine();
        // check if username or ticket and handle it
    }

    private void handleMenu(PrintWriter output) {
        output.println("menu "
                + String.join(",", Server.getTickets().values()) +
                " "
                + String.join(",", Server.getActiveRooms())
        );
    }


    static String generateTickets(String seq) {
        byte[] hash = String.format("%32s", seq).getBytes();
        try {
            for (int i = 0; i < Math.random() * 64 + 1; ++i) {
                hash = MessageDigest.getInstance("SHA-256").digest(hash);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return HexFormat.ofDelimiter(":").formatHex(hash).toString().substring(78);
    }

    public void sendMessage(String message) {
        if (output != null) {
            output.println(message); // Sends the message to the client
        } else {
            System.err.println("Failed to send message: Client output stream is null.");
        }
    }

    private void handleJoin(String roomName, PrintWriter output) {
        Room room = Server.joinOrCreateRoom(roomName, this);
        currentRoom = room;
        output.println("join " + roomName);
    }

    private void handleLeave(PrintWriter output) {
        if (currentRoom != null) {
            currentRoom.removeUser(this);
            output.println("leave " + " " + currentRoom.getName() + " " + pseudonym);
            currentRoom = null;
        } else {
            output.println("error You're not in a room.");
        }
    }


    //Sending message to room users
    private void handleSend(String arguments, PrintWriter output) {
        if (currentRoom == null) {
            // If the user is not in a room, notify them
            sendMessage("error You are not in a room. Join a room to send a message.");
            return;
        }

        if (arguments == null || arguments.trim().isEmpty()) {
            // If the message is empty, notify the user
            sendMessage("error Cannot send an empty message.");
            return;
        }

        // Use the Room's broadcastMessage method to send the message
        currentRoom.broadcastMessage(arguments, this); // 'this' refers to the sender user
        sendMessage("info Message sent to the room.");
    }


    private void handleKick(String arguments, PrintWriter output) {
        // Ensure the arguments contain room name, username, and reason
        String[] parts = arguments.split(" ", 3); // Split into room, username, and reason
        if (parts.length < 3) {
            sendMessage("info Invalid command format. Use: kick <room_name> <username> <reason>");
            return;
        }

        String roomName = parts[0]; // Extract room name
        String targetUsername = parts[1]; // Extract target username
        String reason = parts[2]; // Extract reason

        // Check if the user is in the specified room
        if (currentRoom == null || !currentRoom.getName().equals(roomName)) {
            sendMessage("info You are not in the specified room.");
            return;
        }

        // Find the target user in the current room
        User targetUser = currentRoom.getUserByUsername(targetUsername);
        if (targetUser == null) {
            sendMessage("info User not found in this room.");
            return;
        }

        // Perform the kick action (room ensures only moderators can kick)
        currentRoom.kickUser(targetUser, reason, this);
    }

    public void handleTicket(String arguments, PrintWriter output) {

        Map<String, String> Tickets = Server.getTickets();
        String kValue = Tickets.get(arguments);
        if (kValue != null) {
            sendMessage("info welcome " + kValue);
            sendMessage("ticket " + arguments);
        } else {
            System.out.println("ticket does not exist ");
        }


    }

    public void handleDirect(String arguments, PrintWriter output) {
        if (arguments == null || arguments.trim().isEmpty()) {
            sendMessage("info Invalid command format. Use: direct <username> <message>");
            return;
        }
        String[] parts = arguments.split(" ", 2);
        if (parts.length < 2) {
            sendMessage("info Invalid command format. Use: direct <username> <message>");
            return;
        }

        String targetUsername = parts[0];
        String message = parts[1];
        String targetTicket = null;
        for (Map.Entry<String, String> entry : Server.getTickets().entrySet()) {
            if (entry.getValue().equals(targetUsername)) {
                targetTicket = entry.getKey();
                break;
            }
        }
        User targetUser = Server.getUsers().get(targetTicket);
        if (targetUser == null) {
            sendMessage("info User not found in this room.");
            return;
        }

        targetUser.sendMessage("direct " + pseudonym + " " + message);
        sendMessage("info Direct message sent to " + targetUsername);


    }
}
