package org.event.driven.star.common.domain;

import org.event.driven.star.common.domain.Money;

import java.util.Collections;
import java.util.Map;
import javax.persistence.*;

@Entity
@Table(name="Customer")
@Access(AccessType.FIELD)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Embedded
    private Money creditLimit;

    @ElementCollection
    private Map<Long, Money> creditReservations;

    Money availableCredit() {
        return creditLimit.subtract(creditReservations.values().stream().reduce(Money.ZERO, Money::add));
    }

    public Customer() {
    }

    public Customer(String name, Money creditLimit) {
        this.name = name;
        this.creditLimit = creditLimit;
        this.creditReservations = Collections.emptyMap();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit){
        this.creditLimit = creditLimit;
    }

    public void reserveCredit(Long orderId, Money orderTotal) {
        if (availableCredit().isGreaterThanOrEqual(orderTotal)) {
            creditReservations.put(orderId, orderTotal);
        } else
            throw new CustomerCreditLimitExceededException();
    }

}
