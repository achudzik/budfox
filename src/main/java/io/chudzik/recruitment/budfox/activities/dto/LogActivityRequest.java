package io.chudzik.recruitment.budfox.activities.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class LogActivityRequest {

    private final List<String> tags;
    private final String methodSignature;
    private final Object[] args;

}
