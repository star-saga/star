package org.event.driven.star.customer.controller;

import org.event.driven.star.common.domain.Customer;
import org.event.driven.star.common.domain.Order;
import org.event.driven.star.common.webapi.CreateOrderRequest;
import org.event.driven.star.customer.service.CustomerService;
import org.event.driven.star.common.webapi.CreateCustomerRequest;
import org.event.driven.star.common.webapi.CreateCustomerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CustomerController {
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(value = "/customers", method = RequestMethod.POST)
    public CreateCustomerResponse createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        Customer customer = customerService.createCustomer(createCustomerRequest.getName(), createCustomerRequest.getCreditLimit());
        return new CreateCustomerResponse(customer.getId());
    }

    @RequestMapping(value = "/reserve", method = RequestMethod.POST)
    public void reserveCredit(@RequestBody Order order) throws Exception {
        customerService.reserveCredit(order);
    }
}
