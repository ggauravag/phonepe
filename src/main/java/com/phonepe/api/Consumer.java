package com.phonepe.api;

/**
 * Representation of a consumer which subscribes for messages with {@link MessageQueue}
 */
public interface Consumer {

    /**
     * Return unique name of the consumer
     */
    String getName();

    /**
     * Blocking call which receives the message and processes it
     */
    void process(Message message) throws Exception;

}
