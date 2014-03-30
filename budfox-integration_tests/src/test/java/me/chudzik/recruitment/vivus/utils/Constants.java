package me.chudzik.recruitment.vivus.utils;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.nio.charset.Charset;

import org.springframework.http.MediaType;

public class Constants {

    public static final MediaType APPLICATION_JSON_WITH_UTF8 = new MediaType(
            APPLICATION_JSON.getType(),
            APPLICATION_JSON.getSubtype(),
            Charset.forName("UTF-8"));
}
