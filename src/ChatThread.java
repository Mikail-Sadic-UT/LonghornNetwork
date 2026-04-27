// Chat thread
public class ChatThread implements Runnable {

    private UniversityStudent sender;
    private UniversityStudent receiver;
    private String message;

    public ChatThread(UniversityStudent sender, UniversityStudent receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    public void run() {
        String formatted = sender.name + " -> " + receiver.name + ": " + message;
        sender.addChatMessage(formatted); // both sides get the message
        receiver.addChatMessage(formatted);
        System.out.println(formatted);
    }
}
