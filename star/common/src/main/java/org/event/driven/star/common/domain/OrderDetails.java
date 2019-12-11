package org.event.driven.star.common.domain;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Embeddable
public class OrderDetails implements Serializable {
    private Long customerId;

    @Embedded
    private Money orderTotal;

    public OrderDetails() {
    }

    public OrderDetails(Long customerId, Money orderTotal) {
        this.customerId = customerId;
        this.orderTotal = orderTotal;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }
}
