package org.example;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 13337;
    private static  Map<String, User> users = Collections.synchronizedMap(new HashMap<>());
    private static  Map<String, String> tickets = Collections.synchronizedMap(new HashMap<>());
    private static  Map<String, Room> rooms = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket client = server.accept();
                System.out.println("Client connected");
                Thread user = new Thread(new User(client));
                user.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<String> getActiveRooms() {
        synchronized (rooms) {
            return new ArrayList<>(rooms.keySet());
        }
    }

    // Join or create a room
    public static Room joinOrCreateRoom(String roomName, User user) {
        synchronized (rooms) {
            Room room = rooms.get(roomName);
            if (room == null) {
                room = new Room(roomName, user);
                rooms.put(roomName, room);
                System.out.println("Room '" + roomName + "' created.");
                room.addUser(user);
            } else {
                room.addUser(user);
                System.out.println("User '" + user.getNickName() + "' joined room '" + roomName + "'.");
            }
            return room;
        }
    }

    // Remove a room when it becomes empty
    public static void removeRoom(String roomName) {
        synchronized (rooms) {
            rooms.remove(roomName);
            System.out.println("Room '" + roomName + "' deleted.");
        }
    }

    public static void addUser(User user,String ticket) {
        synchronized (users) {
            users.put(ticket, user);

        }
    }

}
