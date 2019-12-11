package org.event.driven.light.omegacommon.config;

public class PublishTopicConfig {
    private final String publishCreateTopic;
    private final String publishApproveTopic;
    private final String publishRejectTopic;

    public PublishTopicConfig(String publishCreateTopic, String publishApproveTopic, String publishRejectTopic) {
        this.publishCreateTopic = publishCreateTopic;
        this.publishApproveTopic = publishApproveTopic;
        this.publishRejectTopic = publishRejectTopic;
    }


    public String publishCreateTopic() {
        return publishCreateTopic;
    }

    public String publishApproveTopic() {
        return publishApproveTopic;
    }

    public String publishRejectTopic() {
        return publishRejectTopic;
    }
}
