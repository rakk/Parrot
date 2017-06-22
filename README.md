# Parrot Service

Problem to solve:
* I want to quickly test what requests are sent to me service.
* I want to specify HTTP response status in request.
* I want to be able specify that n-th request will have different HTTP response status that default.

The service should return response with status code defined in URL or return 200 if not specified.
The service should return special status for n-th request.

# Example of usage:

## Default response
```
curl -I http://localhost:8080/some-request
```
returns 200 response (always)

## Custom response:
```
curl -I http://localhost:8080/give-me/500
```
returns 500 response (always)

## Custom response with special case

```
curl -I http://localhost:8080/give-me/200/every/5-th-time-give-me/503
```
returns 503 for every 5-th request and 200 for the rest of requests
