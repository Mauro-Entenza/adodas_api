package com.example.demo.service;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.exception.CustomerNotFoundException;
import java.util.List;
import java.util.Map;

public interface CustomerService {

  List<CustomerDto> findAll();

  CustomerDto addCustomer(CustomerDto customerDto);

  void deleteCustomer(long customerId) throws CustomerNotFoundException;

  CustomerDto modify(long customerId, CustomerDto customerDto) throws CustomerNotFoundException;

  CustomerDto patchCustomer(long customerId, Map<String, Object> updates)
      throws CustomerNotFoundException;

  // ✅ Método unificado de filtros
  List<CustomerDto> findByFilters(String name, String email, String surname);
}