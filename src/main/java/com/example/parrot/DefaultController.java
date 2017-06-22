package com.example.parrot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
public class DefaultController {

    private static final Pattern REQUEST_CUSTOM_STATUS = Pattern.compile("/give-me/(\\d+)");
    private static final Pattern REQUEST_CUSTOM_STATUS_WITH_SPECIAL_CASE = Pattern.compile("/give-me/(\\d+)/every/(\\d+)-th-time-give-me/(\\d+)");
    private static final Pattern REQUEST_DEFAULT_STATUS_WITH_SPECIAL_CASE = Pattern.compile("/every/(\\d+)-th-time-give-me/(\\d+)");

    private static final Map<String, Integer> SPECIAL_REQUEST_COUNTERS = new HashMap<>();

    private int defaultStatus;

    public DefaultController(@Value("${parrot.default-status}") String defaultStatusString) {
        try {
            this.defaultStatus = Integer.parseInt(defaultStatusString);
            HttpStatus.valueOf(this.defaultStatus);
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("Invalid value of 'parrot.default-status' should be a number and correct HTTP status", illegalArgumentException);
            throw illegalArgumentException;
        }
    }

    @RequestMapping(value = "/**")
    public ResponseEntity<String> handleRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        ResponseEntity<String> response = createResponse(requestUri, method);
        log.info("Requesting: {} and returned {}", requestUri, response.getStatusCodeValue());
        return response;
    }

    private synchronized ResponseEntity<String> createResponse(String requestUri, String method) {
        int status = getStatus(requestUri);
        String responseMesssage = "Handling: " + method + " " + requestUri + " => " + status;
        return new ResponseEntity<>(responseMesssage, HttpStatus.valueOf(status));
    }

    private int getStatus(String requestUri) {
        int status = this.defaultStatus;
        Matcher matcherSpecialCase = REQUEST_CUSTOM_STATUS_WITH_SPECIAL_CASE.matcher(requestUri);
        if (matcherSpecialCase.matches()) {
            status = getStatusForCustomStatusWithSpecialCase(requestUri, matcherSpecialCase);
        } else {
            Matcher matcherSpecialCaseWithDefault = REQUEST_DEFAULT_STATUS_WITH_SPECIAL_CASE.matcher(requestUri);
            if (matcherSpecialCaseWithDefault.matches()) {
                status = getDefaultStatusOrSpecial(requestUri, status, matcherSpecialCaseWithDefault);
            } else {
                status = getCustomStatusOrDefault(requestUri, status);
            }
        }

        return status;
    }

    private int getCustomStatusOrDefault(String requestUri, int status) {
        Matcher requestMatcher = REQUEST_CUSTOM_STATUS.matcher(requestUri);
        if (requestMatcher.matches()) {
            status = Integer.parseInt(requestMatcher.group(1));
        }
        return status;
    }

    private int getDefaultStatusOrSpecial(String requestUri, int status, Matcher matcherSpecialCaseWithDefault) {
        Integer requestCounter = getRequestCounterAndIncrement(requestUri);
        int nth = Integer.parseInt(matcherSpecialCaseWithDefault.group(1));
        if (requestCounter == nth) {
            SPECIAL_REQUEST_COUNTERS.remove(requestUri);
            status = Integer.parseInt(matcherSpecialCaseWithDefault.group(2));
        } else {
            SPECIAL_REQUEST_COUNTERS.put(requestUri, requestCounter);
        }
        return status;
    }

    private int getStatusForCustomStatusWithSpecialCase(String requestUri, Matcher matcherSpecialCase) {
        int status;
        Integer requestCounter = getRequestCounterAndIncrement(requestUri);
        int nth = Integer.parseInt(matcherSpecialCase.group(2));
        if (requestCounter == nth) {
            SPECIAL_REQUEST_COUNTERS.remove(requestUri);
            status = Integer.parseInt(matcherSpecialCase.group(3));
        } else {
            SPECIAL_REQUEST_COUNTERS.put(requestUri, requestCounter);
            status = Integer.parseInt(matcherSpecialCase.group(1));
        }
        return status;
    }

    private Integer getRequestCounterAndIncrement(String requestUri) {
        return Optional.ofNullable(SPECIAL_REQUEST_COUNTERS.get(requestUri)).orElse(0) + 1;
    }

}
