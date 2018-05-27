package com.phonepe;

import com.phonepe.api.Message;
import com.phonepe.api.MessageCondition;

import java.util.Objects;

public class PatternMessageCondition implements MessageCondition {

    private String pattern;

    public PatternMessageCondition(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean test(Message message) {
        return message.serialize().matches(pattern);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternMessageCondition that = (PatternMessageCondition) o;
        return Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern);
    }
}
