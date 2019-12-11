package org.event.driven.star.customer.service;

import org.event.driven.light.omegacommon.annotations.ApproveEvent;
import org.event.driven.light.omegacommon.annotations.CreateEvent;
import org.event.driven.light.omegacommon.annotations.RejectEvent;
import org.event.driven.star.common.domain.Customer;
import org.event.driven.star.common.domain.Order;
import org.event.driven.star.common.domain.Money;
import org.event.driven.star.customer.commondomain.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

//    @CreateEvent
    public Customer createCustomer(String name, Money creditLimit) {
        //System.out.println("customer created successfully!");
        Customer customer =new Customer(name, creditLimit);
        customerRepository.save(customer);
        return customer;
    }

    @CreateEvent(compensationMethod="rejectCustomer", approveMethod = "approveCustomer")
    public void reserveCredit(Order order) throws Exception{
        Customer customer = customerRepository.findById(order.getOrderDetails().getCustomerId()).get();
        //customer.reserveCredit(order.getId(), order.getOrderDetails().getOrderTotal());
        Money money = order.getOrderDetails().getOrderTotal();
        Money left = customer.getCreditLimit().subtract(money);
        customer.setCreditLimit(left);
        customerRepository.save(customer);
        //throw new Exception();
    }

   // @ApproveEvent
    public void approveCustomer(Order order){
        //System.out.println("customer start approve!");
    }

   // @RejectEvent
    public void rejectCustomer(Order order){
        //System.out.println("customer start reject");
        Customer customer = customerRepository.findById(order.getOrderDetails().getCustomerId()).get();
        Money money = order.getOrderDetails().getOrderTotal();
        Money left = customer.getCreditLimit().add(money);
        customer.setCreditLimit(left);
        customerRepository.save(customer);
       // System.out.println("customer rejected!");
    }
}
