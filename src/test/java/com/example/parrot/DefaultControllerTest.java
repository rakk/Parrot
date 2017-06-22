package com.example.parrot;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DefaultController.class)
public class DefaultControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldReturnCorrectStatusAndBodyForGivenRequest() throws Exception {
        List<TestData> examples = new ArrayList<>();

        examples.add(new TestData(
                new Request("GET", "/default-response"),
                new Response(200, "Handling: GET /default-response => 200")));
        examples.add(new TestData(
                new Request("GET", "/give-me/500"),
                new Response(500, "Handling: GET /give-me/500 => 500")));
        examples.add(new TestData(
                new Request("GET", "/give-me/501"),
                new Response(501, "Handling: GET /give-me/501 => 501")));
        examples.add(new TestData(
                new Request("POST", "/give-me/401"),
                new Response(401, "Handling: POST /give-me/401 => 401")));
        examples.add(new TestData(
                new Request("GET", "/give-me/404"),
                new Response(404, "Handling: GET /give-me/404 => 404")));

        for (TestData example : examples) {
            check(example.getRequest(), example.getExpectedResponse());
        }
    }

    @Test
    public void shouldReturnDifferentStautsEveryNth() throws Exception {

        Request request = new Request("GET", "/give-me/200/every/5-th-time-give-me/404");
        Response defaultResponse = new Response(200, "Handling: GET /give-me/200/every/5-th-time-give-me/404 => 200");
        Response specialResponse = new Response(404, "Handling: GET /give-me/200/every/5-th-time-give-me/404 => 404");
        TestData defaultTestData = new TestData(request, defaultResponse);
        TestData specialData = new TestData(request, specialResponse);

        List<TestData> examples = new ArrayList<>();
        examples.add(defaultTestData);
        examples.add(defaultTestData);
        examples.add(defaultTestData);
        examples.add(defaultTestData);
        examples.add(specialData);
        examples.add(defaultTestData);

        for (TestData example : examples) {
            check(example.getRequest(), example.getExpectedResponse());
        }
    }

    private void check(Request request, Response expectedResponse) throws Exception {
        switch (request.getMethod()) {
            case "GET":
                this.mvc.perform(
                        get(request.getPath()).accept(MediaType.ALL))
                        .andExpect(status().is(expectedResponse.getStatus()))
                        .andExpect(content().string(expectedResponse.getBody()));
                break;
            case "POST":
                this.mvc.perform(
                        post(request.getPath()).accept(MediaType.ALL))
                        .andExpect(status().is(expectedResponse.getStatus()))
                        .andExpect(content().string(expectedResponse.getBody()));
                break;
            default:
                throw new RuntimeException("Unsupported method: " + request.getMethod());

        }
    }

    @Data
    @AllArgsConstructor
    private class TestData {
        private Request request;
        private Response expectedResponse;
    }

    @Data
    @AllArgsConstructor
    private class Request {
        private String method;
        private String path;
    }

    @Data
    @AllArgsConstructor
    private class Response {
        private int status;
        private String body;
    }
}
