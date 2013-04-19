Generic HTTP Stub
=================

[![Build Status](https://travis-ci.org/headexplodes/http-stub-server.png)](https://travis-ci.org/headexplodes/http-stub-server)

The Generic HTTP Stub Server (a.k.a. 'Stubby') is a protocol and server implementation for stubbing HTTP interactions, mainly aimed at automated acceptance testing. There's also some example client code in various languages.

## Overview

A typical use case is a web application that depends on some back-end service. You might want to perform acceptance testing of your web application, say via Cucumber + Selenium. 

However you don't want to use a real instance of the back-end service for many reasons (it's not actually completed yet, there's no integration testing environment or you just plain don't want to couple your testing together). This is where the Generic HTTP Stub comes in. You can use it in place of the back-end service, much like you might use Mockito in your unit tests.

## Example

Let's say that we hvae a back-end service that exposes an endpoint for fetching product information via HTTP (eg, `GET /products/<id>`). We also have a web application that uses this service.

### Successful Scenario

We might have a Cucumber feature that looks something like this:

    Given the following product exists 
        | ID  | Name                 |
        | 100 | USB Missile Launcher |
    When I view the product information page for product '100'
    Then I should see the product name 'USB Missile Launcher'

To stub the product information in the _given_ step we could send the following message at the stub server:

    {
        "request": {
            "method": "GET",
            "path": "/products/1000"
        },
        "response": {
            "status": 200,
            "body": {
                "productId": 100,
                "productName": "USB Missile Launcher"
            }
        }
    }

Which tells the stub server when it receives a `GET` request matching path `/products/100` to respond with an HTTP `200`, and the JSON body:

    {
        "productId": 100,
        "productName": "USB Missile Launcher"
    }

### Failure Scenario

Now if we wanted to test a failure scenario such as:

    Given no products exist
    When I view the product information page for product '100'
    Then I should see a 'Product not found' message

We could simply send the following message to the server instead: 

    {
        "request": {
            "method": "GET",
            "path": "/products/.*"
        },
        "response": {
            "status": 404,
            "body": "Product not found"
        }
    }

Which tells the stub server when it receives a `GET` request matching any path starting with `/products/` (ie, a regular expression) to respond with an HTTP `404` and text body "`Product not found`".

### Making things even simpler

To make things even simpler, and to save you having to construct JSON messages and post them to the stub server, there is simple client library for Ruby (more languages to come). 

For example, if you were using Ruby to implement the examples above you could write:

    product = {
        :productId => 100,
        :productName => 'USB Missile Launcher'
    }
    @stubby.get('/products/1000').return(200, product).stub!

And for the failure scenario:

    @stubby.get('/products/.*').return(404, 'Product not found').stub!

## Language/Framework Support

### Java

* Server for Play Framework (1.x)
* Server for Spring MVC 3.x
* Server for Servlets 2.5 (WAR)

### Ruby

* Client for Ruby (1.9). Can be used with Cucumber (for example).

## Server Protocol

The stub server implements a really simple JSON-based protocol for querying and updating the server state.

### Endpoints

The stub server has a number of endpoints for adding stub data, verifying requests and resetting the state. Every other URL is available to be stubbed.

* `GET /_control/requests/{index}`
  
  Retrieves details of HTTP requests that have been sent to the stub server by index, where zero is the most recent request received.

* `DELETE /_control/requests`

  Deletes all received requests.

* `GET /_control/responses/{index}`

  Retrieves details of a stubbed request.

* `POST /_control/responses`

  Submits a stubbed request to the server - this is the most important endpoint.

* `DELETE /_control/responses`

  Deletes all stubbed requests.

### Message Formats

#### HTTP Message

A JSON object with the following fields:

    {
        "body": <object> | <array> | <string>
        "headers": [
            {
                "name": <string>
                "value": <string>
            },
            ...
        ]
    }

Field details:

* `body`

  HTTP message body. 

  When used in a pattern, can be an `object`, `array` or `string` (see Body Patterns).

  When returned from the _requests_ endpoint, will always be a `string`.

* `headers`

  List of key-value pairs representing HTTP headers. The `name` is always a string, but the `value` is interpreted as a regular expression when used in patterns.

  When matching a request, the stub server iterates over each header in the `headers` array and attempts to find a match in the incoming request. If no match is found, the request as a whole is not matched. An incoming request that has additional headers that are not present in the `headers` array is still considered a successful match.

  **Note:** header names are case-insensitive.

### HTTP Request

Extends `HTTP Message` (above), with additional fields:

    {
        ...
        "method": <string>,
        "path": <string>,
        "params": [
            {
                "name": <string>
                "value": <string>
            },
            ...
        ]
    }

* `method`

  String representing the HTTP method, always in upper-case. Interpreted as a regular expression when used in patterns.
  
* `path`

  String representing the _path_ component of the URL (ie, everything after the hostname up to, but not including, the query). Interpreted as a regular expression when used in patterns.

  **Required** when used in a pattern.

* `params`

  List of key-value pairs representing query parameters. The `name` is always a string, but the `value` is interpreted as a regular expression when used in patterns.

  When matching a request, the stub server iterates over each query parameter in the `params` array and attempts to find a match in the incoming request. If no match is found, the request as a whole is not matched. An incoming request that has additional query parameters that are not present in the `params` array is still considered a successful match.

### HTTP Response

Extends `HTTP Message` (above), with additional fields:

    {
        ...
        "status": <integer>
    }

* `status`

  Integer representing the HTTP status code. 

  **Required** when used in a pattern.
  
### Stubbed Request

This object is used by the `/_control/responses` endpoints and represents a stubbed request/response pair.

    {
        "request": <HTTP Request>,
        "response": <HTTP Response>,
        "delay": <integer>,
        "script": <string>
    }

Field details:

* `request`

  The request object describes what HTTP requests that we want to match (the request _pattern_). **Required**.

  Any fields that are not provided (or are `null`) will match any value. For example, if the `request.method` field omitted, the pattern will match _any_ request method. Only the `request.path` field is required.

* `response`

  The HTTP response to return when the request matches. **Required**.

  Any field that is omitted will simply not appear in the response. For example, if the `response.body` field is omitted, there will be no body in the respones. Only the `response.status` field is required.

* `delay`

  Integer representing a number of milliseconds to delay the response by. If not provided (or `null`) no delay will be used.

* `script`

  JavaScript code to execute when this request is matched. Can be used to dynamically modify the output based on input parameters, for example (more details to come).

### Body Patterns

The stub server currently supports two types of _body patterns_. A body pattern is specified by the `request.body` property in `Stubbed Request` message (above). For an incoming request to match the stubbed response, the body pattern must be matched. 

#### Text (Regular Expression)

If the `request.body` property is a `string`, it is treated as a regular expression that is matched against the incoming request body's contents.

#### JSON

If the `request.body` property is an `object` or `array` it is treated as a JSON structure and matched against the incoming request's JSON body.

Some rules for the JSON body matching:

* The incoming request's must have a `Content-Type` of `application/json`.
* A similar matching approach to the headers and query parameters is employed, ie, only what's specified in the pattern is matched, and any additional properties in the incoming request are ignored.
* The pattern match is applied recursively, and matches strings, numbers, boolean, objects and arrays (and nulls)
* Any string in a pattern is treated as regular expression, that can match either another string, a number or a boolean. It cannot match an array or object.
* Any number in a pattern can only match another number in the incoming request.
* Any boolean in a pattern can only match another boolean in the incoming request.
* Any  null in pattern can only match another null in the incoming request.
* For an object in a pattern to match, for each property in the pattern the incoming request must have a property with exactly the same name, and it's value must match (ie, recursive match). Properties in the request object that are not in the pattern are ignored. An empty object in pattern matches any object in the incoming request.
* Arrays does not have to match exactly, but the pattern order is important. For example, pattern `[b,d]` matches `[a,b,c,d]` because `b` and `d` exist and `b` appears before `d`. An empty array in pattern matches any array in the incoming request.

For example, given the following body pattern:

    {
        "foo": "\\d+",
        "bar": {
            "foo": true
        }
    }

Would match the following JSON body in an incoming request:

    {
        "bar": {
            "foo": true,
            "blah": "blah"
        },
        "blah": [ ],
        "foo": 1234, 
    }

### Body Responses

As with body patterns, when used in a response object, the `body` property can be either a string, an array or an object.

If the body is a string, it is returned as text. If there is no header named `Content-Type` in the response, it is assumed to be `text/plain`.

If the body is an array or an object, it is returned as JSON. If there is no header named `Content-Type` in the response, it is assumed to be `application/json`. 

Note that the returned JSON may not match the body property exactly, as it will be deserialised and then reserialised in the server. For example, the order of properties may change, escape sequences may be represented differently and the format/indenting will most likely differ. If you need to preserve the formatting exactly, provide the body as a string and set the `Content-Type` manually. 

## Building

* Requires [Apache Maven 3.0](http://maven.apache.org/)
* To build all components:

    `mvn clean install`

## Creating Eclipse projects

* Set a classpath variable `M2_REPO` in Eclipse to `/home/<user>/.m2/repository` (Window > Preferences > Java > Build Path > Classpath)
* Execute `mvn eclipse:eclipse`
* Import projects in to Eclipse

# Running

There are several different server implementation, and each is started in a different way. The simplest of the servers is the _standalone_ server.

The standalone server uses the built-in HTTP server implementation in the Sun Java 6.0 (and above) VM. 

To build the server and start it on port `9080` enter (`bash`/`sh`):

    $ cd standalone
    $ mvn package
    $ ./run.sh 9080


