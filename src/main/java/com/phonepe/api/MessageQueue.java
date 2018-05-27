package com.phonepe.api;

import com.phonepe.QueueOverflowException;

/**
 * Representation of a message store which receives messages from {@link Producer}, keeps track of subscribed {@link
 * Consumer}
 */
public interface MessageQueue {

    /**
     * Non-blocking call which notifies all the subscribers of the given message
     *
     * @param message to notify
     */
    void notify(Message message) throws QueueOverflowException;

    /**
     * Blocking call, which registers the given consumer for messages which meet the given condition, along with any
     * dependents
     *
     * @param messageCondition condition to match for future messages produced
     * @param consumer         consumer to be notified, by invoking {@link Consumer#process(Message)}
     * @param dependentOn      names of dependencies in order which are to be notified before the given consumer
     */
    void subscribe(MessageCondition messageCondition, Consumer consumer, Consumer... dependentOn);

}
