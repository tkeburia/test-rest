[![Build Status](https://travis-ci.org/tkeburia/test-rest.svg?branch=master)](https://travis-ci.org/tkeburia/test-rest)  [![Coverage Status](https://coveralls.io/repos/github/tkeburia/test-rest/badge.svg?branch=master)](https://coveralls.io/github/tkeburia/test-rest?branch=master)
# test-rest
A simple rest application that can return different responses based on request parameters

# Run with maven
This is a spring boot application that can be started by running mvn spring-boot:run

# Run as a jar
The application can be built with maven (`mvn clean install`) and the resulting jar can be run directly: `java -jar testRest-1.0-SNAPSHOT.jar`

# Calling the endpoints

the main endpoints are

### `/test-rest` GET endpoint
if called without any query parameters will return a 200 response.
If a specific response code is necessary it can be supplied as a parameter:

Request

`curl -X GET   'http://localhost:23240/testRest?giveMe=400' -i`

Response
```
 HTTP/1.1 400
 Content-Length: 0
 Date: Fri, 20 Oct 2017 15:33:52 GMT
 Connection: close
```

### `/test-rest` POST endpoint
if called without any query parameters will return a 200. It will also return a json with
the description of the http status in the body:

Request

`curl -X POST   'http://localhost:23240/test-rest?giveMe=409'   -H 'content-type: application/json'   -d '{}' -i`

Response

```
HTTP/1.1 409
Content-Type: text/plain;charset=UTF-8
Content-Length: 23
Date: Fri, 20 Oct 2017 15:37:21 GMT

{"response":"Conflict"}
```

### Dynamic response bodies
If we need the service to return a specific response body (e.g. specific json object), we can include the `responseFile` request parameter in our request.
This is going to be the name of a file that is managed by the application, the contents of a file are then returned.

Request

`curl -X GET \
   'http://localhost:23240/test-rest?responseFile=new_file1.json' \
   -H 'Content-Type: application/json -i'
`

```
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Content-Length: 582
Date: Thu, 11 Jan 2018 16:17:26 GMT

{
    "glossary": {
        "title": "example glossary",
        "GlossDiv": {
            "title": "S",
            "GlossList": {
                "GlossEntry": {
                "ID": "SGML",
                "SortAs": "SGML",
                "GlossTerm": "Standard Generalized Markup Language",
                "Acronym": "SGML",
                "Abbrev": "ISO 8879:1986",
                "GlossDef": {
                        "para": "A meta-markup language, used to create markup languages such as DocBook.",
                        "GlossSeeAlso": ["GML", "XML"]
                    },
                "GlossSee": "markup"
                }
            }
        }
    }
}
```

The above assumes that a json file with name new_file1.json existed in the preconfigured directory.

### Adding new sample response files

The following api call can be used to create new sample response files:

```
curl -X POST \
     'http://localhost:23240/test-rest/responseFile?fileName=new_file2.json' \
     -H 'Content-Type: application/json' \
     -d '{
       "glossary": {
           "title": "example glossary",
            "GlossDiv": {
            "title": "S",
            "GlossList": {
                   "GlossEntry": {
                    "ID": "SGML",
                    "SortAs": "SGML",
                    "GlossTerm": "Standard Generalized Markup Language",
                    "Acronym": "SGML",
                    "Abbrev": "ISO 8879:1986",
                    "GlossDef": {
                        "para": "A meta-markup language, used to create markup languages such as DocBook.",
                        "GlossSeeAlso": ["GML", "XML"]
                       },
                    "GlossSee": "markup"
                   }
               }
           }
       }
   }'
```

This will create a file with name new_file2.json (per fileName parameter) with the contents of the request body. The location of the file can be
configured with the `sample.response.directory` property. After this the new file name can be used in above requests to simulate response bodies.

### Listing sample response files

The following lists all currently existing sample response files that can be used in requests:

Request:

`curl -X GET http://localhost:23240/test-rest/responseFile -H 'Content-Type: application/json'`

Response:

```
   HTTP/1.1 200
   Content-Type: application/json;charset=UTF-8
   Content-Length: 89
   Date: Thu, 11 Jan 2018 16:25:15 GMT

   {
       "files": [
           "/tmp/sample_responses/new_file1.json",
           "/tmp/sample_responses/new_file2.json"
       ]
   }
```

# ActiveMQ support
I addition to testing rest apis, test-rest provides the ability to test ActiveMQ queues.

### Setting up queue connectivity
#### Consumers
To subscribe and consume messages from a queue, you will first need to set the `activemq.connections.enabled` flag to true.
This will try to read information about queue brokers from the config and will fail the application startup if minimal required
config is not provided.

Here's an example config to read from a queue

```
broker.consumer.uris.orderBroker=tcp://localhost:61616
broker.consumer.userNames.orderBroker=admin
broker.consumer.passwords.orderBroker=admin
broker.consumer.queueNames.orderBroker=testQueueInbound
```

This will subscribe to a queue named testQueueInbound provided by a broker hosted at the given url and credentials.
After consuming a message, the content of the message will be logged to the std out.

In the above example, the config is for one broker `orderBroker`, but we can subscribe to multiple brokers:

```
broker.consumer.uris.broker1=tcp://localhost:61616
broker.consumer.userNames.broker1=admin
broker.consumer.passwords.broker1=admin
broker.consumer.queueNames.broker1=testQueueInbound1

broker.consumer.uris.broker2=tcp://example.org:61616
broker.consumer.userNames.broker2=admin
broker.consumer.passwords.broker2=admin
broker.consumer.queueNames.broker2=testQueueInbound2
```

This way messages coming in from all configured brokers and queues will be processed.

The above gives possibility not to only subscribe to multiple brokers, but also multiple queues within the same broker if we define two different brokers with the same url:

```
broker.consumer.uris.orderBroker=tcp://localhost:61616
broker.consumer.userNames.orderBroker=admin
broker.consumer.passwords.orderBroker=admin
broker.consumer.queueNames.orderBroker=MyQueue

broker.consumer.uris.otherBroker=tcp://localhost:61616
broker.consumer.userNames.otherBroker=admin
broker.consumer.passwords.otherBroker=admin
broker.consumer.queueNames.otherBroker=OtherQueue

```

IMPORTANT: it is important that for each configured broker all 4 (uris, userNames, passwords, queueNames) properties are defined. If any of the defined brokers
are missing one of the properties, the application will throw a `MissingPropertyException`. The only time these config properties will be ignored is when `activemq.connections.enabled` is set to false.

Note: the reason the property names above (uris, userNames, passwords, queueNames) are defined in plural is that at runtime they are aggregated to maps of brokerName->value,
and the values of these maps are often treated as a collection.


##### Schema validation

In addition, there is the possibility to register json schema files for specific queues, this is done under `queue.schema.files.names.{QUEUE_NAME}` property, it
should be a file name that will be mapped to the given queue name and all incoming message contents in this queue will be evaluated against the schema. The property should only
contain the file name, the directory of the file should be provided by the `schema.file.directory` property.

If a message content in a queue fails to validate against the configured schema, a `DetailedValidationException` will be thrown.

#### Consumers

To be able to put messages into a queue, a provider config needs to be given.

A sample provider config:

```
broker.producer.uris.orderBroker=tcp://activemq:61616
broker.producer.userNames.orderBroker=admin
broker.producer.passwords.orderBroker=admin
broker.producer.queueNames.orderBroker=testQueueOutbound
```

The producer config gives the same flexibility as consumer config, e.g. we can define multiple brokers, multiple queues within brokers like in the consumer examples above.

IMPORTANT: just like with consumers, producer config requires you to declare all four properties for every broker config block (like above).

##### Triggering message producers

In order to get the application to put a message to a configured queue we need to call the queues REST endpoint.

An example request to trigger:

```
curl -X POST \
  'http://localhost:3001/test-rest/queues?brokerName=orderBroker' \
  -H 'Content-Type: application/json' \
  -d '{ "firstName" : "Peter" , "lastName" : "Griffin"}'

```

Will result in a message with content `{ "firstName" : "Peter" , "lastName" : "Griffin"}` being sent to the whatever queue was configured for
`orderBroker` broker.

Note: The queues endpoint will not show up on swagger docs if the `activemq.connections.enabled` is set to false - the creation of this bean is conditional
on the `activemq.connections.enabled` property being true. 

# Swagger Docs

The above endpoints are described using swagger and can be accessed on `http://localhost:23240/swagger-ui.html`

# Logging

The application will log full requests including request parameters and headers as well as the returned response:

Request

```
      curl -X POST \
      'http://localhost:23240/testRest?giveMe=201' \
      -H 'authorization: Basic dXNlcjpwd2Q=' \
      -H 'content-type: application/json' \
      -d '{
        "param1" : "value",
        "param2" : 1,
        "param3" : true
    }' -i
 ```

Response

```
HTTP/1.1 201
Content-Type: text/plain;charset=UTF-8
Content-Length: 22
Date: Fri, 20 Oct 2017 15:40:30 GMT

{"response":"Created"}
```

Log
```
  "method" : [ "MainController.postMe(..)" ],
  "params" : [ {
    "param3" : true,
    "param1" : "value",
    "param2" : 1
  }, 201 ],
  "result" : [ "<201 Created,{\"response\":\"Created\"},{}>" ],
  "headers" : {
    "authorization" : "Basic dXNlcjpwd2Q= (user:pwd)",
    "content-length" : "67",
    "host" : "localhost:23240",
    "content-type" : "application/json",
    "accept" : "*/*"
  }
}
```

Note that for basic authorization header the application will decode the credentials and print them next to header value,
this tool is to be used for testing purposes only
