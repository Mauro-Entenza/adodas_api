package com.example.demo.service.impl;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final ModelMapper modelMapper;

  public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper) {
    this.customerRepository = customerRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public List<CustomerDto> findAll() {
    return modelMapper.map(
        customerRepository.findAll(),
        new TypeToken<List<CustomerDto>>() {
        }.getType()
    );
  }

  @Override
  public CustomerDto addCustomer(CustomerDto customerDto) {
    Customer customer = modelMapper.map(customerDto, Customer.class);
    customer = customerRepository.save(customer);
    return modelMapper.map(customer, CustomerDto.class);
  }

  @Override
  public CustomerDto modify(long customerId, CustomerDto customerDto)
      throws CustomerNotFoundException {

    Customer existingCustomer = customerRepository.findById(customerId)
        .orElseThrow(CustomerNotFoundException::new);

    customerDto.setId(existingCustomer.getId());
    Customer customer = modelMapper.map(customerDto, Customer.class);
    customer = customerRepository.save(customer);

    return modelMapper.map(customer, CustomerDto.class);
  }

  @Override
  public CustomerDto patchCustomer(long customerId, Map<String, Object> updates)
      throws CustomerNotFoundException {

    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(CustomerNotFoundException::new);

    updates.forEach((fieldName, value) -> {
      try {
        var field = Customer.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(customer, value);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new RuntimeException("Invalid field: " + fieldName);
      }
    });

    Customer updatedCustomer = customerRepository.save(customer);
    return modelMapper.map(updatedCustomer, CustomerDto.class);
  }

  @Override
  public void deleteCustomer(long customerId) throws CustomerNotFoundException {
    customerRepository.findById(customerId)
        .orElseThrow(CustomerNotFoundException::new);
    customerRepository.deleteById(customerId);
  }

  // ------------------ NUEVO MÉTODO DE FILTROS ------------------
  @Override
  public List<CustomerDto> findByFilters(String name, String email, String surname) {
    Specification<Customer> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (name != null && !name.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
      }

      if (email != null && !email.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
      }

      if (surname != null && !surname.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%"));
      }

      return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    };

    return customerRepository.findAll(spec).stream()
        .map(customer -> modelMapper.map(customer, CustomerDto.class))
        .toList();
  }
}