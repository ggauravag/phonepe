package com.phonepe;

import com.phonepe.api.Consumer;
import com.phonepe.api.Message;
import com.phonepe.api.MessageCondition;
import com.phonepe.api.Producer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class InMemoryMessageQueueTest {

    private InMemoryMessageQueue inMemoryMessageQueue;
    private Producer producer;
    private List<String> logStore;

    @Before
    public void setUp() throws Exception {
        inMemoryMessageQueue = new InMemoryMessageQueue(10, 2);
        producer = new JSONProducer(inMemoryMessageQueue);
        logStore = new CopyOnWriteArrayList<>();
    }

    @Test
    public void shouldServeMessageFromProducerToConsumer() throws Exception {
        // having
        final TestConsumer consumer = new TestConsumer("A", logStore);
        final MessageCondition anyMessageWithTrue = new PatternMessageCondition(".*true.*");
        final Message message = new JSONMessage("{'value': 'true'}");
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumer);

        // when
        producer.produce(message);
        inMemoryMessageQueue.waitUntilAllPendingTaskCompletes();

        // then
        assertThat(logStore, contains("A: {'value': 'true'}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCircularDependency() throws Exception {
        // having
        final JSONConsumer consumerA = new JSONConsumer("A");
        final JSONConsumer consumerB = new JSONConsumer("B");
        final MessageCondition anyMessageWithTrue = new PatternMessageCondition(".*true.*");
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerA, consumerB);
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerB, consumerA);
    }

    @Test
    public void shouldRetryThreeTimesWhenProcessingFails() throws Exception {
        // having
        final TestConsumer consumer = mock(TestConsumer.class);
        doThrow(Exception.class)
                .when(consumer).process(anyObject());

        final MessageCondition anyMessageWithTrue = new PatternMessageCondition(".*true.*");
        final Message message = new JSONMessage("{'value': 'true'}");
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumer);

        // when
        producer.produce(message);
        inMemoryMessageQueue.waitUntilAllPendingTaskCompletes();

        // then
        verify(consumer, times(3)).process(any());
    }

    @Test
    public void shouldServeMessageFromProducerToMultipleConsumer() throws Exception {
        // having
        final TestConsumer consumerA = new TestConsumer("A", logStore);
        final TestConsumer consumerB = new TestConsumer("B", logStore);
        final TestConsumer consumerC = new TestConsumer("C", logStore);
        final TestConsumer consumerD = new TestConsumer("D", logStore);

        final MessageCondition anyMessageWithTrue = new PatternMessageCondition(".*true.*");
        final Message message = new JSONMessage("{'value': 'true'}");
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerA);
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerB);
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerC);
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerD);

        // when
        producer.produce(message);
        inMemoryMessageQueue.waitUntilAllPendingTaskCompletes();

        // then
        assertThat(logStore, containsInAnyOrder("A: {'value': 'true'}", "B: {'value': 'true'}",
                "C: {'value': 'true'}", "D: {'value': 'true'}"));
    }

    @Test
    public void shouldOnlyServeMessageToRelevantConsumer() throws Exception {
        // having
        final TestConsumer consumerA = new TestConsumer("A", logStore);
        final TestConsumer consumerB = new TestConsumer("B", logStore);
        final TestConsumer consumerC = new TestConsumer("C", logStore);
        final TestConsumer consumerD = new TestConsumer("D", logStore);

        final MessageCondition anyMessageWithTrue = new PatternMessageCondition(".*true.*");
        final MessageCondition notMatchingCondition = new PatternMessageCondition(".*notMatching.*");
        final Message message = new JSONMessage("{'value': 'true'}");
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerA);
        inMemoryMessageQueue.subscribe(notMatchingCondition, consumerB);
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerC);
        inMemoryMessageQueue.subscribe(notMatchingCondition, consumerD);

        // when
        producer.produce(message);
        inMemoryMessageQueue.waitUntilAllPendingTaskCompletes();

        // then
        assertThat(logStore, containsInAnyOrder("A: {'value': 'true'}",
                "C: {'value': 'true'}"));
    }

    @Test
    public void shouldServeMessageFromProducerToConsumersInOrderOfDependency() throws Exception {
        // having
        final TestConsumer consumerA = new TestConsumer("A", logStore);
        final TestConsumer consumerB = new TestConsumer("B", logStore);
        final TestConsumer consumerC = new TestConsumer("C", logStore);

        final MessageCondition anyMessageWithTrue = new PatternMessageCondition(".*true.*");
        final Message message = new JSONMessage("{'value': 'true'}");
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerA, consumerC);
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerB);
        inMemoryMessageQueue.subscribe(anyMessageWithTrue, consumerC, consumerB);

        // when
        producer.produce(message);
        inMemoryMessageQueue.waitUntilAllPendingTaskCompletes();

        // then
        assertThat(logStore, contains("B: {'value': 'true'}", "C: {'value': 'true'}", "A: {'value': 'true'}"));
    }


    private class TestConsumer implements Consumer {

        private final String name;
        private final List<String> logger;

        TestConsumer(String name, List<String> logger) {
            this.name = name;
            this.logger = logger;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void process(Message message) throws Exception {
            logger.add(String.format("%s: %s", name, message.serialize()));
        }
    }
}