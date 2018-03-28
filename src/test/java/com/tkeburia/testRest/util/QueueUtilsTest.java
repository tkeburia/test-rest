package com.tkeburia.testRest.util;

import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.exception.MissingPropertyException;
import com.tkeburia.testRest.queues.BrokerProperties;
import com.tkeburia.testRest.queues.consumer.ConsumerProperties;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.ConnectionFactory;

import static com.tkeburia.testRest.constants.Constants.BROKER_CONSUMER;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class QueueUtilsTest {

    private static final String BROKER_NAME_1 = "broker1";
    private static final String BROKER_URI_1 = "tcp://broker.uri1";
    private static final String BROKER_URI2 = "tcp://broker.uri2";
    private static final String BROKER_NAME_2 = "broker2";
    private static final String USER_NAME_1 = "userName1";
    private static final String USER_NAME_2 = "userName2";
    private static final String PASSWORD_2 = "password2";
    private static final String PASSWORD_1 = "password1";
    private static final String QUEUE_NAME_2 = "queue2";
    private static final String QUEUE_NAME_1 = "queue1";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private BrokerProperties properties = new ConsumerProperties();

    @Before
    public void setup() {
        properties.setUris(ImmutableMap.of(BROKER_NAME_1, BROKER_URI_1, BROKER_NAME_2, BROKER_URI2));
        properties.setUserNames(ImmutableMap.of(BROKER_NAME_1, USER_NAME_1, BROKER_NAME_2, USER_NAME_2));
        properties.setPasswords(ImmutableMap.of(BROKER_NAME_1, PASSWORD_1, BROKER_NAME_2, PASSWORD_2));
        properties.setQueueNames(ImmutableMap.of(BROKER_NAME_1, QUEUE_NAME_1, BROKER_NAME_2, QUEUE_NAME_2));
    }

    @Test
    public void shouldNotThrowExceptionForCorrectProperties() {
        QueueUtils.verifyProperties(properties, BROKER_CONSUMER);
    }

    @Test
    public void shouldThrowExceptionWhenPropertiesInconsistent() {
        exception
                .expect(missingPropertyExceptionWithMessageContaining("All configured queues should contain complete set of properties"));
        properties.setUris(emptyMap());
        QueueUtils.verifyProperties(properties, BROKER_CONSUMER);
    }

    @Test
    public void shouldThrowExceptionWhenUrisAresNull() {
        exception
                .expect(missingPropertyExceptionWithMessageContaining("the following properties were null : [broker.consumer.uris.broker1, broker.consumer.uris.broker2]"));
        properties.setUris(null);
        QueueUtils.verifyProperties(properties, BROKER_CONSUMER);
    }

    @Test
    public void shouldThrowExceptionWhenUsernamessAresNull() {
        exception
                .expect(missingPropertyExceptionWithMessageContaining("the following properties were null : [broker.consumer.userNames.broker1, broker.consumer.userNames.broker2]"));
        properties.setUserNames(null);
        QueueUtils.verifyProperties(properties, BROKER_CONSUMER);
    }

    @Test
    public void shouldThrowExceptionWhenPasswordsAresNull() {
        exception
                .expect(missingPropertyExceptionWithMessageContaining("the following properties were null : [broker.consumer.passwords.broker1, broker.consumer.passwords.broker2]"));
        properties.setPasswords(null);
        QueueUtils.verifyProperties(properties, BROKER_CONSUMER);
    }

    @Test
    public void shouldThrowExceptionWhenQueueNamesAresNull() {
        exception
                .expect(missingPropertyExceptionWithMessageContaining("the following properties were null : [broker.consumer.queueNames.broker1, broker.consumer.queueNames.broker2]"));
        properties.setQueueNames(null);
        QueueUtils.verifyProperties(properties, BROKER_CONSUMER);
    }

    @Test
    public void shouldBuildConnectionFactoryWithCorrectConfiguration() {
        final ConnectionFactory connectionFactory = QueueUtils
                .buildConnectionFactory(BROKER_URI_1, USER_NAME_1, PASSWORD_1);

        assertTrue(ActiveMQConnectionFactory.class.isAssignableFrom(connectionFactory.getClass()));
        assertEquals(BROKER_URI_1, ((ActiveMQConnectionFactory)connectionFactory).getBrokerURL());
        assertEquals(USER_NAME_1, ((ActiveMQConnectionFactory)connectionFactory).getUserName());
        assertEquals(PASSWORD_1, ((ActiveMQConnectionFactory)connectionFactory).getPassword());

    }

    private Matcher<RuntimeException> missingPropertyExceptionWithMessageContaining(String expected) {
        return new MissingPropertyExceptionMessageMatcher(expected);
    }

    private class MissingPropertyExceptionMessageMatcher extends TypeSafeMatcher<RuntimeException> {

        private final String expectedMessage;

        private MissingPropertyExceptionMessageMatcher(String expectedMessage) {
            this.expectedMessage = expectedMessage;
        }

        @Override
        protected boolean matchesSafely(RuntimeException item) {
            return MissingPropertyException.class.isAssignableFrom(item.getClass()) && item.getMessage()
                                                                                           .contains(expectedMessage);
        }

        @Override
        public void describeTo(Description description) {
            description
                    .appendText(String.format("MissingPropertyException with message containing %s", expectedMessage));
        }
    }

}