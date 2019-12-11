package org.event.driven.star.common.webapi;

import org.event.driven.star.common.domain.Money;

public class CreateOrderRequest {
    private Money orderTotal;
    private Long customerId;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(Long customerId, Money orderTotal) {
        this.customerId = customerId;
        this.orderTotal = orderTotal;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
