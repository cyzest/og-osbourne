package com.cyzest.ogosbourne.handler;

import com.cyzest.ogosbourne.model.ResponseBody;
import com.cyzest.ogosbourne.model.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

public class CreateOpenGraphHandlerTest {

    private ObjectMapper objectMapper;

    private CreateOpenGraphHandler createOpenGraphHandler;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        createOpenGraphHandler = new CreateOpenGraphHandler();
    }

    @Test
    public void handleRequestTest() throws IOException {

        String lambdaRequest = "{\"body\":{\"url\":\"http://www.naver.com\"}}";

        try(ByteArrayInputStream in = new ByteArrayInputStream(lambdaRequest.getBytes());
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            createOpenGraphHandler.handleRequest(in, out, null);

            ResponseEntity responseEntity = objectMapper.readValue(out.toByteArray(), ResponseEntity.class);

            Assertions.assertNotNull(responseEntity);
            Assertions.assertEquals(200, responseEntity.getStatusCode());
            Assertions.assertNotNull(responseEntity.getBody());

            ResponseBody responseBody = objectMapper.readValue(responseEntity.getBody(), ResponseBody.class);

            Assertions.assertNotNull(responseBody);
            Assertions.assertEquals(200, responseBody.getCode());
            Assertions.assertNotNull(responseBody.getExtra());

            System.out.println(responseBody.getExtra());
        }
    }

}
