package io.chudzik.recruitment.budfox.utils;

import java.io.IOException;

import io.chudzik.recruitment.budfox.configuration.JsonMappingConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static final ObjectMapper mapper = JsonMappingConfiguration.objectMapper();

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    public static String convertObjectToJsonString(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }

}
