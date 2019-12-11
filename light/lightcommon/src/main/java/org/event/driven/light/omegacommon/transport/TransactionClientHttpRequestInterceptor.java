package org.event.driven.light.omegacommon.transport;

import org.event.driven.light.kafkaserialize.common.LightContext;
import org.event.driven.light.kafkaserialize.core.RootContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.event.driven.light.kafkaserialize.common.LightContext.*;

public class TransactionClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private final LightContext lightContext;

    public TransactionClientHttpRequestInterceptor(LightContext lightContext){
        this.lightContext = lightContext;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
         if(lightContext!=null && lightContext.getGlobalId()!= null){
             request.getHeaders().add(GLOBAL_ID, lightContext.getGlobalId());
             request.getHeaders().add(LOCAL_ID, lightContext.getLocalId());
             request.getHeaders().add(EXPIRE_TIME, lightContext.getExpireTime());

             RootContext.bindLastServiceFlag();

//             System.out.println("Add "+GLOBAL_ID+" "+lightContext.getGlobalId()+", "+
//                     LOCAL_ID+" "+lightContext.getLocalId()+" and "+
//                     EXPIRE_TIME+" "+lightContext.getExpireTime()+" to request header");
         }else{
             System.out.println("Can not inject transaction Id, because the lightContext is null or cannot get the globalId");
         }

        return execution.execute(request, body);
    }
}
