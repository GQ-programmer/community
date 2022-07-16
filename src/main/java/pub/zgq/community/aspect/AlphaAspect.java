package pub.zgq.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @Author 孑然
 */
//@Component
//@Aspect
public class AlphaAspect {

    /**
     * 切点
     */
    @Pointcut("execution(* pub.zgq.community.service.*.*(..))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before() {
        System.out.println("before...");
    }

    @After("pointCut()")
    public void After() {
        System.out.println("After...");
    }

    @AfterReturning("pointCut()")
    public void AfterReturning() {
        System.out.println("AfterReturning...");
    }

    @AfterThrowing("pointCut()")
    public void afterThrowing() {
        System.out.println("afterThrowing...");
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before...");
        Object obj = joinPoint.proceed();
        System.out.println("around after...");
        return obj;
    }
}
