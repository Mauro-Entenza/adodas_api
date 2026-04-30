package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.dto.ServiceDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.domain.entity.Service;
import com.example.demo.exception.ServiceNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.service.impl.ServiceServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

public class ServiceServiceImplTest {

  private ServiceRepository serviceRepository;
  private CustomerRepository customerRepository;
  private ModelMapper modelMapper;
  private ServiceServiceImpl serviceService;

  @BeforeEach
  void setUp() {
    serviceRepository = mock(ServiceRepository.class);
    customerRepository = mock(CustomerRepository.class);
    modelMapper = new ModelMapper();
    serviceService = new ServiceServiceImpl(serviceRepository, customerRepository, modelMapper);
  }

  @Test
  void testAddServiceWithCustomer() {

    ServiceDto dto = new ServiceDto();
    dto.setId(0L);
    dto.setName("Service name");
    dto.setDescription("desc");
    dto.setPrice(100f);
    dto.setCustomerId(1L);  // ✅ customerId presente
    dto.setIsActive(true);

    Customer mockCustomer = new Customer();
    when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));

    Service savedService = new Service();
    savedService.setId(1L);
    savedService.setName("Service name");
    when(serviceRepository.save(any(Service.class))).thenReturn(savedService);

    ServiceDto result = serviceService.addService(dto);

    assertEquals("Service name", result.getName());
    verify(customerRepository, times(1)).findById(1L);
    verify(serviceRepository, times(1)).save(any(Service.class));
  }

  @Test
  void testAddServiceNoCustomer() {
    ServiceDto dto = new ServiceDto();
    dto.setName("Service name");
    dto.setDescription("desc");
    dto.setPrice(100f);

    Service savedService = new Service();
    savedService.setId(1L);
    savedService.setName("Service name");
    when(serviceRepository.save(any(Service.class))).thenReturn(savedService);

    ServiceDto result = serviceService.addService(dto);

    assertEquals("Service name", result.getName());
    verify(customerRepository, never()).findById(any(Long.class));
    verify(serviceRepository, times(1)).save(any(Service.class));
  }

  @Test
  void testFindAll() {
    Service s1 = new Service();
    s1.setId(1L);
    s1.setName("Service1");
    Service s2 = new Service();
    s2.setId(2L);
    s2.setName("Service2");
    when(serviceRepository.findAll()).thenReturn(List.of(s1, s2));

    List<ServiceDto> services = serviceService.findAll();

    assertEquals(2, services.size());
    assertEquals("Service1", services.get(0).getName());
  }

  @Test
  void testDeleteServiceNotFound() {
    when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ServiceNotFoundException.class, () ->
        serviceService.deleteService(999L));
  }

  @Test
  void testDeleteServiceSuccess() throws ServiceNotFoundException {
    Service service = new Service();
    service.setId(1L);
    when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

    serviceService.deleteService(1L);

    verify(serviceRepository).delete(service);
  }

  @Test
  void testPatchService() throws ServiceNotFoundException {
    Service service = new Service();
    service.setId(1L);
    service.setName("Original");
    when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
    when(serviceRepository.save(any(Service.class))).thenReturn(service);

    Map<String, Object> updates = Map.of("name", "Updated Name");

    ServiceDto result = serviceService.patch(1L, updates);

    assertEquals("Updated Name", result.getName());
    verify(serviceRepository, times(1)).save(service);
  }
}
