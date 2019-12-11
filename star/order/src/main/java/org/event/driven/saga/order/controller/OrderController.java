package org.event.driven.saga.order.controller;

import org.aspectj.weaver.ast.Or;
import org.event.driven.star.common.domain.Order;
import org.event.driven.saga.order.service.OrderService;
import org.event.driven.star.common.domain.UniqueIdGenerator;
import org.event.driven.star.common.webapi.CreateOrderRequest;
import org.event.driven.star.common.webapi.CreateOrderResponse;
import org.event.driven.star.common.domain.OrderDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    public OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public CreateOrderResponse createOrder (@RequestBody CreateOrderRequest createOrderRequest){
        OrderDetails orderDetails=new OrderDetails(createOrderRequest.getCustomerId(), createOrderRequest.getOrderTotal());
        UniqueIdGenerator uniqueIdGenerator=new UniqueIdGenerator();
        String orderId = uniqueIdGenerator.nextId();
        Order order=new Order(orderDetails, orderId);
        orderService.createOrder(order);
        //System.out.println("orderId: "+order.getId());
        return new CreateOrderResponse(order.getId());
    }
}
