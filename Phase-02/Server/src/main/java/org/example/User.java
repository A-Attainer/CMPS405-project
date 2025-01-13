package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class User implements Runnable {
    private Socket client;
    private String ticket;
    private Room currentRoom;
    private String nickName;
    private PrintWriter output;
    public Socket getClient() {
        return client;
    }

    public String getTicket() {
        return ticket;
    }

    public String getNickName() {
        return nickName;
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
            do {
                request = input.readLine(); // Read the client's request
                if (request != null) {
                    String[] parts = request.split(" ", 2); // Split the command and arguments
                    String command = parts[0];
                    String arguments = parts.length > 1 ? parts[1] : null;

                    switch (command) {
                        case "pseudo":
                            nickName=arguments;
                            ticket=generateTickets(arguments);
                            Server.addUser(this,ticket);
                            output.println("Your generated ticket : "+ ticket);
                            break;
//                            case "ticket":
//                                handleTicket(arguments, output);
//                                break;
                        case "join":
                            handleJoin(arguments, output);
                            break;
                        case "leave":
                            handleLeave(output);
                            break;
//                            case "kick":
//                                handleKick(arguments, output);
//                                break;
                        case "send":
                            handleSend(arguments, output);
                            break;
//                            case "direct":
//                                handleDirect(arguments, output);
//                                break;
                        default:
                            output.println("Unknown command. Please try again.");
                    }
                }
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        output.println("Joined room: " + roomName);
    }

    private void handleLeave(PrintWriter output) {
        if (currentRoom != null) {
            currentRoom.removeUser(this);
            output.println("You left the room.");
            currentRoom = null;
        } else {
            output.println("You're not in a room.");
        }
    }


    //Sending message to room users
    private void handleSend(String arguments, PrintWriter output) {
        if (currentRoom == null) {
            // If the user is not in a room, notify them
            sendMessage("You are not in a room. Join a room to send a message.");
            return;
        }

        if (arguments == null || arguments.trim().isEmpty()) {
            // If the message is empty, notify the user
            sendMessage("Cannot send an empty message.");
            return;
        }

        // Use the Room's broadcastMessage method to send the message
        currentRoom.broadcastMessage(arguments, this); // 'this' refers to the sender user
        sendMessage("Message sent to the room.");
    }













}
