package org.event.driven.star.backend.controller;

import org.event.driven.star.backend.service.BackendService;
import org.event.driven.star.common.webapi.CreateOrderRequest;
import org.event.driven.star.common.domain.OrderDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class BackendController {
    public BackendService backendService;

    @Autowired
    public BackendController(BackendService backendService){
        this.backendService = backendService;
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String startEvent (@RequestBody CreateOrderRequest createOrderRequest){
        try{
            Random r = new Random();
            int ran = r.nextInt(10)+1;
            //System.out.println("random customer Id: "+ran);
            OrderDetails orderDetails = new OrderDetails((long)ran, createOrderRequest.getOrderTotal());
            String id= backendService.start(orderDetails);
            return "create success, the orderId is: "+id+"!";
        }catch(NullPointerException e){
            return "intercept http request and make it return null! ";
        }
    }
}
