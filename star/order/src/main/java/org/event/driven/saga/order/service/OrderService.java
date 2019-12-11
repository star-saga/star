package org.event.driven.saga.order.service;

import org.event.driven.light.omegacommon.annotations.CreateEvent;
import org.event.driven.star.common.domain.Order;
import org.event.driven.saga.order.commondomain.OrderRepository;
import org.event.driven.star.common.domain.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @CreateEvent(compensationMethod="rejectOrder", approveMethod = "approveOrder")
    public void createOrder(Order order){
        //System.out.println("order created successfully!");
        //Order order=new Order(orderDetails);
        orderRepository.save(order);

        //RestTemplate restTemplate=new RestTemplate();
        String url="http://localhost:8081/reserve";
        restTemplate.postForObject(url, order, void.class);
        //return order;
    }

    public void approveOrder(Order order){
        System.out.println("order start approve");
        //order.noteCreditReserved();
        //orderRepository.save(order);
        orderRepository.updateOrderState(order.getOrderId(), OrderState.APPROVED);
    }

    public void rejectOrder(Order order){
        System.out.println("order start reject");
        //order.noteCreditReservationFailed();
        //orderRepository.save(order);
        orderRepository.updateOrderState(order.getOrderId(), OrderState.REJECTED);
    }
}
