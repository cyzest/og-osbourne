package com.cyzest.ogosbourne.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.cyzest.ogosbourne.model.OpenGraph;
import com.cyzest.ogosbourne.model.ResponseBody;
import com.cyzest.ogosbourne.model.ResponseEntity;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

public class CreateOpenGraphHandler implements RequestStreamHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, String> headers = Collections.singletonMap("Content-Type", "application/json;charset=UTF-8");

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        final JsonNode event;

        try {
            event = objectMapper.readTree(inputStream);
        } catch (JsonMappingException e) {
            writeInvalidJsonInStreamResponse(outputStream, e.getMessage());
            return;
        }

        if (event == null) {
            writeInvalidJsonInStreamResponse(outputStream, "event was null");
            return;
        }

        final JsonNode requestBody = event.findValue("body");

        if (requestBody == null) {
            writeInvalidJsonInStreamResponse(outputStream, "request body is null");
            return;
        }

        final JsonNode url = requestBody.findValue("url");

        if (url == null) {
            writeInvalidJsonInStreamResponse(outputStream, "url is null");
            return;
        }

        Map<String, String> openGraph = null;

        try {
            openGraph = OpenGraph.getOpenGraphByUrl(url.asText());
        } catch (Throwable ex) {
            writeInvalidJsonInStreamResponse(outputStream, "invalid url");
        }

        objectMapper.writeValue(
                outputStream,
                new ResponseEntity(200, headers, toString(new ResponseBody<>(200, "OK", openGraph))));
    }

    private void writeInvalidJsonInStreamResponse(OutputStream outputStream, String message) throws IOException {
        objectMapper.writeValue(
                outputStream,
                new ResponseEntity(400, headers, toString(new ResponseBody<>(400, message))));
    }

    private String toString(ResponseBody responseBody) throws IOException {
        return objectMapper.writeValueAsString(responseBody);
    }

}
