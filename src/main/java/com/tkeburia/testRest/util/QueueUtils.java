package com.tkeburia.testRest.util;

import com.tkeburia.testRest.exception.MissingPropertyException;
import com.tkeburia.testRest.queues.QueueProperties;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;
import java.util.Set;

import static com.tkeburia.testRest.constants.Constants.BROKER_PRODUCER;
import static java.util.stream.Collectors.toSet;

public final class QueueUtils {

    private QueueUtils() {
    }

    public static void verifyProperties(QueueProperties producerProperties) {
        final Set<String> queueNameKeys = producerProperties.getQueueNames().keySet();
        final Set<String> uriKeys = producerProperties.getUris().keySet();
        final Set<String> userNameKeys = producerProperties.getUserNames().keySet();
        final Set<String> passwordKeys = producerProperties.getPasswords().keySet();

        if (!(queueNameKeys.equals(uriKeys) && queueNameKeys.equals(userNameKeys) && queueNameKeys
                .equals(passwordKeys))) {
            throw new MissingPropertyException(buildMissingPropertyExceptionMessage(queueNameKeys, uriKeys, userNameKeys, passwordKeys));
        }
    }

    private static String buildMissingPropertyExceptionMessage(Set<String> queueNames, Set<String> uris, Set<String> userNames, Set<String> passwords) {

        final Set<String> forQueueNames = queueNames.stream().map(n -> BROKER_PRODUCER + "queueNames" + n).collect(toSet());
        final Set<String> forUris = uris.stream().map(n -> BROKER_PRODUCER + "uris" + n).collect(toSet());
        final Set<String> forUserNames = userNames.stream().map(n -> BROKER_PRODUCER + "userNames" + n).collect(toSet());
        final Set<String> forPasswords = passwords.stream().map(n -> BROKER_PRODUCER + "passwords" + n).collect(toSet());

        return String
                .format("All configured queues should contain complete set of properties, " +
                                "following properties were found were found %s, %s, %s, %s - make sure these sets are equal",
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
