package com.example.demo.service.impl;

import com.example.demo.domain.dto.ServiceDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.domain.entity.Service;
import com.example.demo.exception.ServiceNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.service.ServiceService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

  private final ServiceRepository serviceRepository;
  private final CustomerRepository customerRepository;
  private final ModelMapper modelMapper;

  public ServiceServiceImpl(ServiceRepository serviceRepository,
      CustomerRepository customerRepository,
      ModelMapper modelMapper) {
    this.serviceRepository = serviceRepository;
    this.customerRepository = customerRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ServiceDto> findAll() {
    return modelMapper.map(serviceRepository.findAll(),
        new TypeToken<List<ServiceDto>>() {
        }.getType());
  }

  @Override
  @Transactional
  public ServiceDto addService(ServiceDto serviceDto) throws ServiceNotFoundException {
    Service service = modelMapper.map(serviceDto, Service.class);

    if (serviceDto.getCustomerId() != null) {
      Customer customer = customerRepository.findById(serviceDto.getCustomerId())
          .orElseThrow(() -> new ServiceNotFoundException(
              "Customer not found with ID: " + serviceDto.getCustomerId()));
      service.setCustomer(customer);
    }

    service = serviceRepository.save(service);
    return modelMapper.map(service, ServiceDto.class);
  }

  @Override
  @Transactional
  public ServiceDto modify(long serviceId, ServiceDto serviceDto) throws ServiceNotFoundException {
    Service existingService = serviceRepository.findById(serviceId)
        .orElseThrow(() -> new ServiceNotFoundException("Service not found with ID: " + serviceId));

    modelMapper.map(serviceDto, existingService);

    if (serviceDto.getCustomerId() != null) {
      Customer customer = customerRepository.findById(serviceDto.getCustomerId())
          .orElseThrow(() -> new ServiceNotFoundException(
              "Customer not found with ID: " + serviceDto.getCustomerId()));
      existingService.setCustomer(customer);
    }

    Service updatedService = serviceRepository.save(existingService);
    return modelMapper.map(updatedService, ServiceDto.class);
  }

  @Override
  @Transactional
  public void deleteService(long serviceId) throws ServiceNotFoundException {
    Service service = serviceRepository.findById(serviceId)
        .orElseThrow(() -> new ServiceNotFoundException("Service not found with ID: " + serviceId));
    serviceRepository.delete(service);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ServiceDto> findByFilters(String name, Float minPrice, Float maxPrice,
      Boolean isActive) {
    Specification<Service> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (name != null && !name.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
      }
      if (minPrice != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
      }
      if (maxPrice != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
      }
      if (isActive != null) {
        predicates.add(cb.equal(root.get("isActive"), isActive));
      }

      return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    };

    return serviceRepository.findAll(spec).stream()
        .map(service -> modelMapper.map(service, ServiceDto.class))
        .toList();
  }

  @Override
  @Transactional
  public ServiceDto patch(long serviceId, Map<String, Object> updates)
      throws ServiceNotFoundException {

    Service service = serviceRepository.findById(serviceId)
        .orElseThrow(() -> new ServiceNotFoundException("Service not found with ID: " + serviceId));

    if (updates.containsKey("name") && updates.get("name") != null) {
      service.setName((String) updates.get("name"));
    }
    if (updates.containsKey("price") && updates.get("price") != null) {
      service.setPrice(((Number) updates.get("price")).floatValue());
    }
    if (updates.containsKey("description") && updates.get("description") != null) {
      service.setDescription((String) updates.get("description"));
    }
    if (updates.containsKey("isActive") && updates.get("isActive") != null) {
      service.setIsActive((Boolean) updates.get("isActive"));
    }

    if (updates.containsKey("customerId") || updates.containsKey("customer_id")) {
      String key = updates.containsKey("customerId") ? "customerId" : "customer_id";
      Object customerIdObj = updates.get(key);

      if (customerIdObj != null) {
        Long customerId = ((Number) customerIdObj).longValue();
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ServiceNotFoundException(
                "Customer not found with ID: " + customerId));
        service.setCustomer(customer);
      } else {
        service.setCustomer(null);
      }
    }

    Service updatedService = serviceRepository.save(service);
    return modelMapper.map(updatedService, ServiceDto.class);
  }
}