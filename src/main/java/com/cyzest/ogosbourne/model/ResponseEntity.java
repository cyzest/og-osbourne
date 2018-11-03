package com.cyzest.ogosbourne.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResponseEntity {

    private int statusCode;
    private Map<String, String> headers;
    private String body;

    public ResponseEntity() {}

    public ResponseEntity(final int statusCode, final Map<String, String> headers, final String body) {
        this.statusCode = statusCode;
        this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
