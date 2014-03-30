package me.chudzik.recruitment.vivus.utils;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return mapper().writeValueAsBytes(object);
    }

    public static String convertObjectToJsonString(Object object) throws IOException {
        return mapper().writeValueAsString(object);
    }

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

}
