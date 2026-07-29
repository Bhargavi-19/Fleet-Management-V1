package com.example.demo.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ============================================================
 *  SPRING AOP - LOGGING ASPECT
 * ============================================================
 *
 * WHAT PROBLEM DOES THIS SOLVE?
 * -----------------------------
 * Logging "method X started", "method X finished in 42 ms" is the same code
 * in every method. That is a *cross-cutting concern*: it cuts across many
 * classes but is not really part of what any of them does.
 *
 * AOP lets us write that logging ONCE, here, and have Spring apply it
 * automatically. None of the controller or service code changes at all.
 *
 * THE VOCABULARY (only four words)
 * --------------------------------
 *  Aspect     - this class. A bundle of cross-cutting behaviour.
 *  Join point - a place where the behaviour can be attached. In Spring AOP
 *               that always means "a method call on a Spring bean".
 *  Pointcut   - the expression that decides WHICH methods (see below).
 *  Advice     - the code to run. Which one you use depends on WHEN:
 *                 @Before        - just before the method runs
 *                 @After         - after it finishes, success or failure
 *                 @AfterThrowing - only when it throws
 *                 @Around        - wraps it, so it can time the call
 *
 * HOW IT WORKS UNDER THE BONNET
 * -----------------------------
 * Spring does not modify your class. At startup it creates a *proxy* object
 * that wraps the real bean. Everyone gets the proxy injected. The proxy runs
 * the advice, then calls the real method.
 *
 * One consequence worth knowing: a method calling another method *on itself*
 * bypasses the proxy, so no advice runs. Only calls that come in from outside
 * the bean are advised. (The same rule applies to @Transactional and @Async.)
 *
 * WHERE IS IT APPLIED?
 * --------------------
 * Deliberately scoped to ONE module - customer registration - so the logs
 * stay readable while you are learning:
 *
 *     POST /api/customer/register
 *       -> CustomerController.register(..)   [logged: entry, exit, time]
 *       -> CustomerServiceImpl.register(..)  [logged: execution, time]
 *
 * TO APPLY IT MORE WIDELY
 * -----------------------
 * Widen the pointcuts below, for example:
 *
 *     within(com.example.demo.controller..*)   // every controller
 *     within(com.example.demo.service.impl..*) // every service
 *
 * SAMPLE OUTPUT
 * -------------
 *   -> ENTER  CustomerController.register() args=[RegisterRequest]
 *   -> ENTER  CustomerServiceImpl.register()
 *   <- EXIT   CustomerServiceImpl.register() took 118 ms
 *   <- EXIT   CustomerController.register() took 121 ms
 *
 * ============================================================
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    // ============================================================
    // POINTCUTS - the "which methods" part
    // ============================================================

    /** Every public method on CustomerController. */
    @Pointcut("execution(* com.example.demo.controller.CustomerController.*(..))")
    public void customerControllerMethods() {
        // Marker method. The body is never executed - only the annotation
        // matters. It exists so the expression can be given a readable name
        // and reused by the advice below.
    }

    /** Every public method on CustomerServiceImpl. */
    @Pointcut("execution(* com.example.demo.service.impl.CustomerServiceImpl.*(..))")
    public void customerServiceMethods() {
    }

    /** Controller or service - used for the exception advice. */
    @Pointcut("customerControllerMethods() || customerServiceMethods()")
    public void customerModule() {
    }

    // ============================================================
    // ADVICE - the "what to do" part
    // ============================================================

    /** Runs immediately BEFORE a controller method. */
    @Before("customerControllerMethods()")
    public void logControllerEntry(org.aspectj.lang.JoinPoint joinPoint) {

        log.info("-> ENTER  {}.{}() args={}",
                shortClassName(joinPoint),
                joinPoint.getSignature().getName(),
                describeArguments(joinPoint.getArgs()));
    }

    /** Runs AFTER a controller method, whether it succeeded or threw. */
    @After("customerControllerMethods()")
    public void logControllerExit(org.aspectj.lang.JoinPoint joinPoint) {

        log.info("<- EXIT   {}.{}()",
                shortClassName(joinPoint),
                joinPoint.getSignature().getName());
    }

    /**
     * Wraps every service method so we can measure how long it took.
     *
     * @Around is the only advice that can do this, because it is the only one
     * that controls when (and whether) the real method runs - that is what
     * joinPoint.proceed() does.
     */
    @Around("customerServiceMethods()")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        String method = shortClassName(joinPoint) + "." + joinPoint.getSignature().getName() + "()";

        long startedAt = System.currentTimeMillis();
        log.info("-> ENTER  {}", method);

        try {
            // Run the real method.
            Object result = joinPoint.proceed();

            log.info("<- EXIT   {} took {} ms",
                    method, System.currentTimeMillis() - startedAt);

            return result;

        } catch (Throwable ex) {
            // Log the timing even on the failure path, then rethrow so the
            // normal exception handling is completely unaffected.
            log.warn("<- FAILED {} after {} ms",
                    method, System.currentTimeMillis() - startedAt);
            throw ex;
        }
    }

    /**
     * Runs only when a method throws.
     *
     * Note this does NOT swallow the exception - it still travels up to
     * GlobalExceptionHandler exactly as before.
     */
    @AfterThrowing(pointcut = "customerModule()", throwing = "ex")
    public void logException(org.aspectj.lang.JoinPoint joinPoint, Throwable ex) {

        log.error("!! ERROR  {}.{}() threw {}: {}",
                shortClassName(joinPoint),
                joinPoint.getSignature().getName(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }

    // ============================================================
    // Small helpers
    // ============================================================

    private String shortClassName(org.aspectj.lang.JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType().getSimpleName();
    }

    /**
     * Logs argument TYPES, never their values.
     *
     * Registration payloads contain a raw password, so printing the values
     * would leak it straight into the log file.
     */
    private String describeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.stream(args)
                .map(a -> a == null ? "null" : a.getClass().getSimpleName())
                .toList()
                .toString();
    }
}
