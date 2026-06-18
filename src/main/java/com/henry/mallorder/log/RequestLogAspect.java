package com.henry.mallorder.log;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RequestLogAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class);

    @Around("within(com.henry.mallorder..controller..*)")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = getCurrentRequest();
        String method = request== null ? "UNKNOWN" : request.getMethod();
        String uri = request== null ? "UNKNOWN" : request.getRequestURI();

        try {
            return joinPoint.proceed();
        }finally {
            long costTime = System.currentTimeMillis() - startTime;
            log.info("request method: {}, uri: {}, cost time: {}ms", method, uri, costTime);
        }
    }
    private HttpServletRequest getCurrentRequest(){
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes==null){
            return null;
        }

        return attributes.getRequest();
    }
}
