# Parrot Service

Problem to solve:
* I want to quickly test what requests are sent to my service
* I want to specify HTTP response by custom request url
* I want to be able specify that n-th request will have different HTTP response status

# Example of usage:


## Custom response 
Request like */give-me/STATUS* return specified *STATUS* code

example:
```
curl -I http://localhost:8080/give-me/500
```
returns *500* response (always)

## Custom response with special case
Request like */give-me/DEFAULT_STATUS/evert/N-th-time-give-me/SPECIAL_STATUS* for every N-th request will return *SPECIAL_STATUS*

Other requests return *DEFAULT_STATUS*

example:
```
curl -I http://localhost:8080/give-me/200/every/5-th-time-give-me/503
```
returns 503 for every 5-th request and 200 for the rest of requests

## Default response
Requests not matched return default status (200)
```
curl -I http://localhost:8080/any-not-matched-request
```
returns *200* response (always)
