package com.phonepe;

import com.phonepe.api.Message;

import java.util.Objects;

public class JSONMessage implements Message {

    private String serializedJSON;

    public JSONMessage(String serializedJSON) {
        this.serializedJSON = serializedJSON;
    }

    @Override
    public String serialize() {
        return serializedJSON;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONMessage that = (JSONMessage) o;
        return Objects.equals(serializedJSON, that.serializedJSON);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serializedJSON);
    }
}
