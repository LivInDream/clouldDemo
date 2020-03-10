package com.example.springcloud.payment.myservlte;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * @author lak
 * @date 2020/3/10 8:55
 */
@Aspect
@Slf4j
public class LoggerProcess {

    private final Gson gson = new Gson();


    @Around("@annotation(MyAnnotation)")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = null;
        long stratTime = System.currentTimeMillis();
        try{
            object = joinPoint.proceed();
        }catch (Throwable ex){
            object = parseException(joinPoint,ex);
            throw ex;
        }finally {
            long costTime = System.currentTimeMillis()-stratTime;
            Method method = getMethod(joinPoint);
            String parameters = formatParameters(joinPoint, method);
            String reuslt = new StringBuilder(method.getDeclaringClass().getName()).append(".").append(method.getName())
                    .append("paramters={").append(parameters).append("}").toString();

            log.info("result:{},parameters{},cost:{}",object,reuslt,costTime);
        }
        return object;
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)){
            throw  new IllegalArgumentException("该注解只能用于方法");
        }
        MethodSignature methodSignature = (MethodSignature)signature;
        Object target = joinPoint.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        return currentMethod;
    }

    private Object parseException(ProceedingJoinPoint point,Throwable ex) throws Throwable {
        Method currentMethod = getMethod(point);
        String parameters = formatParameters(point, currentMethod);
        String message = "exception:"+ ex.getClass().getSimpleName() + "\n Method:" + currentMethod.toString() + "\n" +
                "MehtodParamters:{\n" + parameters + "}";
        if (ex instanceof Exception){
            return null;
        }
        throw  ex;
    }

    private String formatParameters(ProceedingJoinPoint point,Method method){
        Object[] args = point.getArgs();
        Class<?>[] paramNames = method.getParameterTypes();
        StringBuilder argStr = new StringBuilder();
        for (int i = 0; i < args.length; i++){
            argStr.append(paramNames[i]).append(":").append(gson.toJson(args[i])).append("\n");
        }
        return argStr.toString();
    }
}
