package com.tkeburia.testRest.queues;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public abstract class QueueProperties {
    private Map<String, String> uris;
    private Map<String, String> userNames;
    private Map<String, String> passwords;
    private Map<String, String> queueNames;

    public Set<String> getIds() {
        return queueNames.keySet();
    }
}
