package org.event.driven.star.backend.service;

import net.sf.json.JSONObject;
import org.event.driven.light.omegacommon.annotations.StartEvent;
import org.event.driven.star.common.webapi.CreateOrderResponse;
import org.event.driven.star.common.domain.OrderDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class BackendService {

    @Autowired
    private RestTemplate restTemplate;

    @StartEvent
    public String start(OrderDetails orderDetails) {
        String url="http://localhost:8080/orders";
        //RestTemplate restTemplate=new RestTemplate();
        CreateOrderResponse response=restTemplate.postForObject(url, orderDetails, CreateOrderResponse.class);
        return response.getOrderId().toString();
    }

}
