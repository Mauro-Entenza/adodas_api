package com.example.demo.service.impl;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public List<CustomerDto> findAll() {
    return this.modelMapper.map(this.customerRepository.findAll(),
        new TypeToken<List<CustomerDto>>() {
        }.getType());
  }

  @Override
  public CustomerDto addCustomer(CustomerDto customerDto) {
    Customer customer = this.modelMapper.map(customerDto, Customer.class);
    customer = customerRepository.save(customer);
    return this.modelMapper.map(customer, CustomerDto.class);
  }
}
