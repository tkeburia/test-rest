# testRest
A sample rest application that can return different responses based on request parameters

# Run with maven
This is a spring boot application that can be started by running mvn spring-boot:run

# Calling the endpoints

the main endpoints are

### `/testRest` GET endpoint
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

### `/testRest` POST endpoint
if called without any query parameters will return a 200. It will also return a json with
the description of the http status in the body:

Request

`curl -X POST   'http://localhost:23240/testRest?giveMe=409'   -H 'content-type: application/json'   -d '{}' -i`

Response

```
HTTP/1.1 409
Content-Type: text/plain;charset=UTF-8
Content-Length: 23
Date: Fri, 20 Oct 2017 15:37:21 GMT

{"response":"Conflict"}
```

### `/testRest/slow` GET endpoint

This endpoint returns a [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html) That will take a few seconds to finish 
and will add new values to it in a separate thread after returning the value. This can be used for testing reactive functionality introduced in Spring 5

# Logging

The application will log full requests including request parameters and headers as well as the returned response:

Request

```curl -X POST \
   >   'http://localhost:23240/testRest?giveMe=201' \
   >   -H 'authorization: Basic dXNlcjpwd2Q=' \
   >   -H 'content-type: application/json' \
   >   -d '{
   >     "param1" : "value",
   >     "param2" : 1,
   >     "param3" : true
   > }' -i
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
