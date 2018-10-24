package com.cyzest.ogosbourne.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class OpenGraphTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void handleRequestTest() throws Exception {

        Map<String, String> openGraphMap = OpenGraph.getOpenGraphByUrl("http://naver.com");

        Assertions.assertNotNull(openGraphMap);
        Assertions.assertFalse(openGraphMap.isEmpty());

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(openGraphMap));
    }

}
