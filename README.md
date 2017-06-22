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

## Default response with special case

Request like */evert/N-th-time-give-me/SPECIAL_STATUS* 
returns *DEFAULT_STATUS* but for every N-th (eg. 5th) requests returns *SPECIAL_STATUS*


example:
```
curl -I http://localhost:8080/every/5-th-time-give-me/501
```
returns 501 for every 5-th request and 200 for the rest of requests


## Custom response with special case
Request like */give-me/CUSTOM_STATUS/evert/N-th-time-give-me/SPECIAL_STATUS* 
returns *CUSTOM_STATUS* but for every N-th (eg. 5th) requests returns *SPECIAL_STATUS*

example:
```
curl -I http://localhost:8080/give-me/208/every/5-th-time-give-me/503
```
returns 503 for every 5-th request and 208 for the rest of requests

## Default response
Requests not matched return default status (200)

example:
```
curl -I http://localhost:8080/any-not-matched-request
```
returns *200* response (always)
