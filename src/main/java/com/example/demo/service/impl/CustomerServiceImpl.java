package com.example.demo.service.impl;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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

  @Override
  public CustomerDto modify(long customerId, CustomerDto customerDto)
      throws CustomerNotFoundException {
    this.customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
    return this.addCustomer(customerDto);
  }

  @Override
  public List<CustomerDto> searchCustomers(String name, String email) {
    return null;
  }


  @Override
  public void deleteCustomer(long customerId) throws CustomerNotFoundException {
    this.customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
    this.customerRepository.deleteById(customerId);
  }

  @Override
  public List<CustomerDto> searchCustomers(String name, String email, String surname) {
    Specification<Customer> specification = (root, query, criteriaBuilder) -> {
      Predicate predicate = criteriaBuilder.conjunction();

      if (name != null && !name.isEmpty()) {
        predicate = criteriaBuilder.and(predicate,
            criteriaBuilder.like(root.get("name"), "%" + name + "%"));
      }

      if (email != null && !email.isEmpty()) {
        predicate = criteriaBuilder.and(predicate,
            criteriaBuilder.like(root.get("email"), "%" + email + "%"));
      }

      if (surname != null && !surname.isEmpty()) {
        predicate = criteriaBuilder.and(predicate,
            criteriaBuilder.like(root.get("surname"), "%" + surname + "%"));
      }

      return predicate;
    };

    return customerRepository.findAll(specification).stream()
        .map(customer -> modelMapper.map(customer, CustomerDto.class))
        .toList();
  }

}

