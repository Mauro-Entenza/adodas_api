package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.impl.CustomerServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private CustomerServiceImpl customerService;


  @Test
  void testAddCustomer() {
    CustomerDto dto = getSampleCustomerDto();

    Customer savedCustomer = getSampleCustomer();
    when(modelMapper.map(dto, Customer.class)).thenReturn(savedCustomer);  // ← AGREGAR
    when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
    when(modelMapper.map(savedCustomer, CustomerDto.class)).thenReturn(dto); // ← AGREGAR

    CustomerDto result = customerService.addCustomer(dto);

    assertEquals("Mauro", result.getName());
    verify(modelMapper, times(2)).map(any(), any(Class.class));  // ← VERIFICAR
  }

  @Test
  void testModifySuccess() throws CustomerNotFoundException {
    Customer existingCustomer = getSampleCustomer();
    CustomerDto newDto = new CustomerDto(1L, "Laura", "12345678L", "Gomez",
        "laura@example.com", true, LocalDate.now());

    Customer updatedCustomer = getSampleCustomer();
    updatedCustomer.setName("Laura");
    updatedCustomer.setSurname("Gomez");
    updatedCustomer.setEmail("laura@example.com");

    CustomerDto resultDto = new CustomerDto(1L, "Laura", "12345678L", "Gomez",
        "laura@example.com", true, LocalDate.now());

    when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
    when(modelMapper.map(newDto, Customer.class)).thenReturn(updatedCustomer);
    when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
    when(modelMapper.map(updatedCustomer, CustomerDto.class)).thenReturn(resultDto);

    CustomerDto result = customerService.modify(1L, newDto);

    assertEquals("Laura", result.getName());
    assertEquals("laura@example.com", result.getEmail());
    verify(customerRepository).save(any(Customer.class));
    verify(modelMapper, times(2)).map(any(), any(Class.class));
  }

  @Test
  void testModifyThrowsWhenNotFound() {
    when(customerRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(CustomerNotFoundException.class,
        () -> customerService.modify(99L, getSampleCustomerDto()));
  }

  @Test
  void testDeleteCustomerSuccess() throws CustomerNotFoundException {
    when(customerRepository.findById(1L)).thenReturn(Optional.of(getSampleCustomer()));
    doNothing().when(customerRepository).deleteById(1L);

    customerService.deleteCustomer(1L);
    verify(customerRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteCustomerThrowsWhenNotFound() {
    when(customerRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(99L));
  }

  private Customer getSampleCustomer() {
    return new Customer(1L, "Mauro", "12345678L", "Perez", "mauro@example.com", true,
        LocalDate.of(2020, 5, 20), null);
  }

  private CustomerDto getSampleCustomerDto() {
    return new CustomerDto(1L, "Mauro", "12345678L", "Perez", "mauro@example.com", true,
        LocalDate.of(2020, 5, 20));
  }
}
