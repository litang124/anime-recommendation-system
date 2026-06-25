package com.anime.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志切面
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    
    /**
     * 定义切点：所有Controller层的方法
     */
    @Pointcut("execution(* com.anime.controller..*(..))")
    public void controllerPointcut() {
    }
    
    /**
     * 环绕通知：记录方法执行时间和异常
     */
    @Around("controllerPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.info("开始执行: {}.{}()", className, methodName);
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("执行完成: {}.{}() - 耗时: {}ms", className, methodName, endTime - startTime);
            return result;
        } catch (Exception e) {
            log.error("执行异常: {}.{}() - 错误: {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }
}
