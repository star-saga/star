package org.event.driven.star.common.domain;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="orders")
@Access(AccessType.FIELD)
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private OrderState state;

    @Embedded
    private OrderDetails orderDetails;

    public Order() {
    }

    public Order(OrderDetails orderDetails, String orderId) {
        this.orderDetails = orderDetails;
        this.state = OrderState.PENDING;
        this.orderId = orderId;
    }

    public Long getId() {
        return id;
    }

    public String getOrderId(){
        return orderId;
    }

    public void setOrderId(String orderId){
        this.orderId = orderId;
    }

    public void noteCreditReserved() {
        this.state = OrderState.APPROVED;
    }

    public void noteCreditReservationFailed() {
        this.state = OrderState.REJECTED;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state){
        this.state= state;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

//    @Override
//    public String toString(){
//        return "Order: { id= "+id+", customerId= "+orderDetails.getCustomerId()
//                +"Money= "+orderDetails.getOrderTotal()+" }";
//    }
}
