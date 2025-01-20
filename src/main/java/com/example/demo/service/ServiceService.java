package com.example.demo.service;

import com.example.demo.domain.dto.ServiceDto;
import com.example.demo.exception.ServiceNotFoundException;
import java.util.List;

public interface ServiceService {

  List<ServiceDto> findAll();

  ServiceDto addService(ServiceDto serviceDto);

  void deleteService(long serviceId) throws ServiceNotFoundException;

  ServiceDto modify(long serviceId, ServiceDto serviceDto) throws ServiceNotFoundException;

  List<ServiceDto> searchServices(String type, Float minPrice, Float maxPrice, Boolean isActive);
}
