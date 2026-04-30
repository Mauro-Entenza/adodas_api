package com.example.demo.service;

import com.example.demo.domain.dto.ServiceDto;
import com.example.demo.exception.ServiceNotFoundException;
import java.util.List;
import java.util.Map;

public interface ServiceService {

  List<ServiceDto> findAll();

  ServiceDto addService(ServiceDto serviceDto);

  void deleteService(long serviceId) throws ServiceNotFoundException;

  ServiceDto modify(long serviceId, ServiceDto serviceDto) throws ServiceNotFoundException;

  // ✅ Reemplazamos searchServices por findByFilters
  List<ServiceDto> findByFilters(String name, Float minPrice, Float maxPrice, Boolean isActive);

  ServiceDto patch(long serviceId, Map<String, Object> updates);
}