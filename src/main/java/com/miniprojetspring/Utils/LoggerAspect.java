package com.miniprojetspring.Utils;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class LoggerAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.miniprojetspring.Controller.*.*(..))")
    public Object logApiCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get HTTP request details
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        String httpMethod = request.getMethod();
        String requestUrl = request.getRequestURL().toString();

        // Log before method execution
        logger.info("[{}] {} - Calling: {} with arguments: {}",
                httpMethod,
                requestUrl,
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        // Log after method execution
        logger.info("[{}] {} - Completed: {} in {}ms with result: {}",
                httpMethod,
                requestUrl,
                joinPoint.getSignature().toShortString(),
                executionTime,
                result);

        return result;
    }
}