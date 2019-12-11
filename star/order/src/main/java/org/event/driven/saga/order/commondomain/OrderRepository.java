package org.event.driven.saga.order.commondomain;

import org.event.driven.star.common.domain.Order;
import org.event.driven.star.common.domain.OrderState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    @Transactional
    @Modifying
    @Query("update Order o set o.state = ?2 where o.orderId = ?1")
    void updateOrderState(String orderId, OrderState orderState);
}
