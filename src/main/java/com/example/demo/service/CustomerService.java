package com.example.demo.service;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.exception.CustomerNotFoundException;
import java.util.List;

public interface CustomerService {

  List<CustomerDto> findAll();

  CustomerDto addCustomer(CustomerDto customerDto);

  void deleteCustomer(long customerId) throws CustomerNotFoundException;

  CustomerDto modify(long customerId, CustomerDto customerDto) throws CustomerNotFoundException;

  List<CustomerDto> searchCustomers(String name, String email);

  List<CustomerDto> searchCustomers(String name, String email, String surname);
}
