package com.example.demo.service;

import com.example.demo.domain.dto.CustomerDto;
import java.util.List;

public interface CustomerService {

  List<CustomerDto> findAll();

  CustomerDto addCustomer(CustomerDto customerDto);
}
