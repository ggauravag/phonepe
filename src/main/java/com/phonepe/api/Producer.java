package com.phonepe.api;

/**
 * Representation of a message producer which supplies generated messages to {@link MessageQueue}
 */
public interface Producer {

    /**
     * Blocking call, which produces a message, and passes it to {@link MessageQueue}
     */
    void produce(Message message);

}
