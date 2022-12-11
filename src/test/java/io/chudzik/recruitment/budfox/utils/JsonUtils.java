package io.chudzik.recruitment.budfox.utils;

import io.chudzik.recruitment.budfox.configuration.WebLayerConfiguration.JsonMappingConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {

    private static final ObjectMapper mapper = JsonMappingConfiguration.objectMapper();

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    public static String convertObjectToJsonString(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }

}
