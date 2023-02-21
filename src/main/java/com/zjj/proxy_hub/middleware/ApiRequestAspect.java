package com.zjj.proxy_hub.middleware;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Slf4j
@Aspect
@Component
public class ApiRequestAspect {

    @Pointcut("execution(* com.zjj.proxy_hub.controller.*.*(..))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();
        try {
            final Object result = joinPoint.proceed();
            final long end = System.currentTimeMillis();
            log.info("方法 {} 执行成功, 参数为 {}, 返回值为 {}, 耗时 {} ms.", joinPoint.getSignature().getDeclaringTypeName(),
                    Arrays.toString(joinPoint.getArgs()), result, end - start);
            return result;
        } catch (Throwable e) {
            final long end = System.currentTimeMillis();
            log.info("方法 {} 执行失败, 参数为 {}, 异常为 {}, 耗时 {} ms.", joinPoint.getSignature().getDeclaringTypeName(),
                    Arrays.toString(joinPoint.getArgs()), e, end - start);
            throw e;
        }
    }
}
