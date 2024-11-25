package com.example.demo.controller;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

  private final Logger logger = LoggerFactory.getLogger(CustomerController.class);
  @Autowired
  private CustomerService customerService;

  @GetMapping("/customers")
  public ResponseEntity<List<CustomerDto>> getAll() {
    logger.info("BEGIN getAll");
    List<CustomerDto> customers = customerService.findAll();
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }

  @PostMapping("/customers")
  public ResponseEntity<CustomerDto> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
    logger.info("BEGIN addCustomer");
    CustomerDto newCustomer = customerService.addCustomer(customerDto);
    return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
  }

}
