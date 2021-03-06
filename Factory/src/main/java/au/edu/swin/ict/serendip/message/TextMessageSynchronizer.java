package au.edu.swin.ict.serendip.message;

public class TextMessageSynchronizer implements MessageSynchronizer {

    public TextMessageSynchronizer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Message synchronizeMessages(Message[] messages) {
        // TODO Auto-generated method stub

        Message syncedMessage = null;
        String contents = null;
        String id = null;
        String pid = messages[messages.length].getPid();
        for (int i = 0; i < messages.length; i++) {
            contents += messages[i].getContents();
            id += messages[i].getId();

        }
        syncedMessage = new Message(id, pid);
        syncedMessage.setContents(contents);
        return syncedMessage;
    }

}
