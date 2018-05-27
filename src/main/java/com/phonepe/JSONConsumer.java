package com.phonepe;

import com.phonepe.api.Consumer;
import com.phonepe.api.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JSONConsumer implements Consumer {

    private final String name;

    private final List<Message> consumedMessages = new ArrayList<>();

    public JSONConsumer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void process(Message message) throws Exception {
        consumedMessages.add(message);
        System.out.println(String.format("Consumer: %s, Message: %s", name, message.serialize()));
    }

    public List<Message> getConsumedMessages() {
        return consumedMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONConsumer that = (JSONConsumer) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
