package com.bank.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

public class StopWatchAspect {

    public Object doAround(ProceedingJoinPoint joinpoint) throws Throwable {
        String className = joinpoint.getTarget().getClass().getSimpleName();
        StopWatch stopWatch = new StopWatch(className != null ? className : "");
        String methodName = joinpoint.getSignature().getName();
        stopWatch.start(methodName != null ? methodName : "");
        Object result = joinpoint.proceed(joinpoint.getArgs());
        stopWatch.stop();
        System.out.printf("- %s %s%n", joinpoint.getSignature().getName(), stopWatch.shortSummary());
        return result;
    }
}
