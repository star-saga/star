package org.event.driven.light.omegacommon.config;

public class SubscribeTopicConfig {
    private final String subscribeCreateTopic;
    private final String subscribeApproveTopic;
    private final String subscribeRejectTopic;

    public SubscribeTopicConfig(String subscribeCreateTopic, String subscribeApproveTopic, String subscribeRejectTopic) {
        this.subscribeCreateTopic = subscribeCreateTopic;
        this.subscribeApproveTopic = subscribeApproveTopic;
        this.subscribeRejectTopic = subscribeRejectTopic;
    }


    public String subscribeCreateTopic() {
        return subscribeCreateTopic;
    }

    public String subscribeApproveTopic() {
        return subscribeApproveTopic;
    }

    public String subscribeRejectTopic() {
        return subscribeRejectTopic;
    }
}
