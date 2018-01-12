# testRest
A sample rest application that can return different responses based on request parameters

# Run with maven
This is a spring boot application that can be started by running mvn spring-boot:run

# Run as a jar
The application can be built with maven (`mvn clean install`) and the resulting jar can be run directly: `java -jar testRest-1.0-SNAPSHOT.jar`

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

### Dynamic response bodies
If we need the service to return a specific response body (e.g. specific json object), we can include the `responseFile` request parameter in our request.
This is going to be the name of a file that is managed by the application, the contents of a file are then returned.

Request

`curl -X GET \
   'http://localhost:23240/testRest?responseFile=new_file1.json' \
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

```curl -X POST \
     'http://localhost:23240/testRest/responseFile?fileName=new_file2.json' \
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

`curl -X GET http://localhost:23240/testRest/responseFile -H 'Content-Type: application/json'`

Response:

```HTTP/1.1 200
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

## Swagger Docs

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
