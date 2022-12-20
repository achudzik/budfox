package io.chudzik.recruitment.budfox.activities;

import io.chudzik.recruitment.budfox.activities.dto.LogActivityRequest;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toUnmodifiableList;

@Aspect
@RequiredArgsConstructor
class LogActivityAspect {

    private final ActivitiesFacade activitiesFacade;


    @Before("@annotation(io.chudzik.recruitment.budfox.activities.LogActivity)")
    public void logActivity(JoinPoint joinPoint) {
        var methodSignature = methodSignatureFrom(joinPoint);
        var auditTags = auditTagsDefinedAt(methodSignature);
        var request = new LogActivityRequest(
                auditTags,
                methodSignature.toLongString(),
                joinPoint.getArgs()
        );
        activitiesFacade.newActivity(request);
    }

    private MethodSignature methodSignatureFrom(JoinPoint joinPoint) {
        return Optional.ofNullable(joinPoint)
                .map(JoinPoint::getSignature)
                .map(MethodSignature.class::cast)
                .orElseThrow();
    }

    private List<String> auditTagsDefinedAt(MethodSignature methodSignature) {
        return Optional.of(methodSignature)
                .map(MethodSignature::getMethod)
                .map(method -> method.getAnnotation(LogActivity.class))
                .map(LogActivity::value).stream()
                .flatMap(Arrays::stream)
                .sorted()
                .collect(toUnmodifiableList())
        ;
    }

}
