package com.bank.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

public class StopWatchAspect {

    public Object doAround(ProceedingJoinPoint joinpoint) throws Throwable {
        StopWatch stopWatch = new StopWatch(joinpoint.getTarget().getClass().getSimpleName());
        stopWatch.start(joinpoint.getSignature().getName());
        Object result = joinpoint.proceed(joinpoint.getArgs());
        stopWatch.stop();
        System.out.printf("- %s %s%n", joinpoint.getSignature().getName(), stopWatch.shortSummary());
        return result;
    }
}
