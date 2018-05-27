PhonePe Coding Assignment

Problem Statement:

Design an efficient in-memory queueing system with low latency requirements
Functional specification:
1. Queue holds JSON messages
2. Allow subscription of Consumers to messages that match a particular expression
3. Consumers register callbacks that will be invoked whenever there is a new message
4. Queue will have one producer and multiple consumers
5. Consumers might have dependency relationships between them. For ex, if there are three consumers A, B and C. One dependency relationship can be that C cannot consume a particular message before A and B have consumed it. C -> (A,B) (-> means must process after)
6. Queue is bounded in size and completely held in-memory. Size is configurable.
7. Handle concurrent writes and reads consistently between producer and consumers.
8. Provide retry mechanism to handle failures in message processing.


Solution:

I have made following assumptions:

- JSON messages would be matched using regex on serialized data, this could be changed by using JsonPath etc.
- Added handling to try processing message 3 times, it's hard-coded can be made configurable
- Size of queue and maximum number of worker threads have been kept configurable

Improvements that can be done:

- A lot of refactoring, but since I didn't time couldn't do that
- Controlling concurrency level

 