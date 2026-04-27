 // Friend Request Thread
public class FriendRequestThread implements Runnable {

    private UniversityStudent sender;
    private UniversityStudent receiver;

    public FriendRequestThread(UniversityStudent sender, UniversityStudent receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        sender.addFriend(receiver); // sender adds receiver
        receiver.addFriend(sender); // receiver adds sender back
        System.out.println(sender.name + " sent a friend request to " + receiver.name);
    }
}
