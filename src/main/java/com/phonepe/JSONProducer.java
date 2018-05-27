package com.phonepe;

import com.phonepe.api.Message;
import com.phonepe.api.MessageQueue;
import com.phonepe.api.Producer;

public class JSONProducer implements Producer {

    private final MessageQueue messageQueue;

    public JSONProducer(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void produce(Message message) {
        while (true) {
            try {
                messageQueue.notify(message);
                break;
            } catch (QueueOverflowException e) {
                // do nothing
            }
        }
    }
}
