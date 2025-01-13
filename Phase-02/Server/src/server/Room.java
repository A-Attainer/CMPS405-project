
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


    public synchronized User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getPseudonym().equals(username)) {
                return user;
            }
        }
        return null;
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
        System.out.println("New moderator for room '" + name + "' is: " + moderator.getPseudonym());
    }

    private void deleteRoom() {
        Server.removeRoom(name); // Notify the server to remove this room
    }



    public void broadcastMessage(String message, User sender) {
        synchronized (users) { // Ensure thread-safety for user list
            for (User user : users) {
                if (!user.equals(sender)) {
                    user.sendMessage("room " + sender.getPseudonym() + " "  + ": " + message);
                }
            }
        }
    }


    public void kickUser(User target, String reason, User requester) {
        // Ensure the requester is the moderator
        if (!requester.equals(moderator)) {
            requester.sendMessage("info You are not authorized to kick users.");
            return;
        }

        // Check if the target user exists in the room
        if (!users.contains(target)) {
            requester.sendMessage("info User is not in this room.");
            return;
        }

        // Remove the target user from the room
        users.remove(target);
        target.setCurrentRoom(null);
        target.sendMessage("kick " + name + " " + reason); // Notify the target user
        broadcastMessage("leave " + name + " " + target.getPseudonym(), moderator); // Notify all users in the room

        // Check if the room is empty or if the moderator needs to be reassigned
        if (users.isEmpty()) {
            deleteRoom();
        } else if (target.equals(moderator)) {
            assignNewModerator();
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
