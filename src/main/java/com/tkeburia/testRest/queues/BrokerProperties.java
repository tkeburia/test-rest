package com.tkeburia.testRest.queues;

import lombok.Data;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;

@Data
public abstract class BrokerProperties {
    private Map<String, String> uris;
    private Map<String, String> userNames;
    private Map<String, String> passwords;
    private Map<String, String> queueNames;

    public Set<String> getIds() {
        return Stream.of(uris, userNames, passwords, queueNames).filter(Objects::nonNull).map(Map::keySet).findFirst().orElse(
                emptySet());
    }
}
