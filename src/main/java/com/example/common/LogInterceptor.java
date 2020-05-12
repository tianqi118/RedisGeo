package com.example.common;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
/**
 * Log traceId print
 *
 * @author tianqi
 * @date 2020-4-30
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    private final static String TRACE_ROOT_ID = "traceRootId";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ROOT_ID, traceId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        MDC.remove(TRACE_ROOT_ID);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}

