package com.tkeburia.testRest.util;

import com.tkeburia.testRest.exception.MissingPropertyException;
import com.tkeburia.testRest.queues.BrokerProperties;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tkeburia.testRest.constants.Constants.*;
import static java.util.stream.Collectors.toSet;

public final class QueueUtils {

    private QueueUtils() {
    }

    public static void verifyProperties(BrokerProperties properties, String propertyPrefix) {

        checkForNulls(properties, propertyPrefix);

        final Set<String> queueNameKeys = properties.getQueueNames().keySet();
        final Set<String> uriKeys = properties.getUris().keySet();
        final Set<String> userNameKeys = properties.getUserNames().keySet();
        final Set<String> passwordKeys = properties.getPasswords().keySet();

        if (!(queueNameKeys.equals(uriKeys) && queueNameKeys.equals(userNameKeys) && queueNameKeys
                .equals(passwordKeys))) {
            throw new MissingPropertyException(buildMissingPropertyExceptionMessage(propertyPrefix, queueNameKeys, uriKeys, userNameKeys, passwordKeys));
        }
    }

    private static void checkForNulls(BrokerProperties properties, String prefix) {
        List<String> nullProperties = new ArrayList<>();
        if (properties.getUris() == null ) {
            nullProperties.add(prefix + URIS_PROPERTY_NAME);
        }
        if (properties.getUserNames() == null) {
            nullProperties.add(prefix + USER_NAMES_PROPERTY_NAME);
        }
        if (properties.getPasswords() == null) {
            nullProperties.add(prefix + PASSWORDS_PROPERTY_NAME);
        }
        if (properties.getQueueNames() == null) {
            nullProperties.add(prefix + QUEUE_NAMES_PROPERTY_NAME);
        }

        if (!nullProperties.isEmpty()) {
            final List<String> fullPropNames = nullProperties.stream()
                                                       .flatMap(p -> properties.getIds().stream().map(id -> p + id))
                                                       .collect(Collectors.toList());
            throw new MissingPropertyException(String.format("Broker properties cannot be null, the following properties were null : %s", fullPropNames));
        }
    }

    private static String buildMissingPropertyExceptionMessage(String prefix, Set<String> queueNames, Set<String> uris, Set<String> userNames, Set<String> passwords) {

        final Set<String> forQueueNames = queueNames.stream().map(n -> prefix + QUEUE_NAMES_PROPERTY_NAME + n).collect(toSet());
        final Set<String> forUris = uris.stream().map(n -> prefix + URIS_PROPERTY_NAME + n).collect(toSet());
        final Set<String> forUserNames = userNames.stream().map(n -> prefix + USER_NAMES_PROPERTY_NAME + n).collect(toSet());
        final Set<String> forPasswords = passwords.stream().map(n -> prefix +  PASSWORDS_PROPERTY_NAME + n).collect(toSet());

        return String
                .format("All configured queues should contain complete set of properties, " +
                                "following properties were found %s, %s, %s, %s - make sure these sets are equal",
                        forQueueNames, forUris, forUserNames, forPasswords);
    }

    public static ConnectionFactory buildConnectionFactory(String uri, String userName, String password) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(uri);
        connectionFactory.setUserName(userName);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }
}
