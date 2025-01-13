package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    private String name;
    private User moderator;
    private List<User> users;

    public Room(String name) {
        this.name = name;
    }
    public Room(String name, User moderator) {
        users=new ArrayList<User>();
        this.name = name;
        this.moderator = moderator;
    }

    public synchronized void addUser(User user) {
        users.add(user);

    }
    public synchronized void removeUser(User user) {
        users.remove(user);
        if (users.isEmpty()) {
            deleteRoom();
        } else if (user.equals(moderator)) {
            assignNewModerator();
        }

    }
    public synchronized void broadcast(String message) {

    }
    private void assignNewModerator() {
        Random random = new Random();
        int index = random.nextInt(users.size());
        moderator = users.get(index);
        System.out.println("New moderator for room '" + name + "' is: " + moderator.getNickName());
    }

    private void deleteRoom() {
        Server.removeRoom(name); // Notify the server to remove this room
    }



    public void broadcastMessage(String message, User sender) {
        synchronized (users) { // Ensure thread-safety for user list
            for (User user : users) {
                if (!user.equals(sender)) {
                    user.sendMessage("[Room " + name + "] " + sender.getNickName() + ": " + message);
                }
            }
        }
    }



    public synchronized User getModerator() {
        return moderator;
    }
    public String getName() {
        return name;
    }
    private void identifyUser(){

    }
}
