package au.edu.swin.ict.road.composite.message.analyzer;

import au.edu.swin.ict.road.composite.message.MessageWrapper;

import java.util.List;

/**
 * This interface has to be implemented by all the analyzers which process the messages.
 *
 * @author Malinda Kapuruge (mkapuruge@swin.edu.au)
 * @author Saichander Kukunoor (saichanderreddy@gmail.com)
 */
public interface MessageAnalyzer {
    public MessageWrapper conjunct(List<MessageWrapper> messages, TransformLogic tl) throws RuntimeException;

    public List<MessageWrapper> disjunct(MessageWrapper aMessage, TransformLogic tl) throws RuntimeException;

    public MessageWrapper transform(MessageWrapper aMessage, TransformLogic tl) throws RuntimeException;
}




