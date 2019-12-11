package org.event.driven.light.omegacommon.transport;

import org.event.driven.light.kafkaserialize.common.LightContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.event.driven.light.kafkaserialize.common.LightContext.*;

public class TransactionHandlerInterceptor implements HandlerInterceptor {

    private final LightContext lightContext;

    public TransactionHandlerInterceptor(LightContext lightContext){
        this.lightContext = lightContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (lightContext != null) {
            String globalTxId = request.getHeader(GLOBAL_ID);
            if (globalTxId == null) {
                //System.out.println("Request URL: "+request.getRequestURL());
                System.out.println("Cannot inject transaction ID, no such header: "+ GLOBAL_ID);
            } else {
                lightContext.setGlobalId(globalTxId);
                lightContext.setLocalId(request.getHeader(LOCAL_ID));
                lightContext.setExpireTime(request.getHeader(EXPIRE_TIME));
            }
        } else {
            System.out.println("Cannot inject transaction ID, as the OmegaContext is null.");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView mv) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        if (lightContext != null) {
            lightContext.clear();
        }
    }
}
