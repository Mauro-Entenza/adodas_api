package com.example.demo.service.impl;

import com.example.demo.domain.dto.ServiceDto;
import com.example.demo.domain.entity.Service;
import com.example.demo.exception.ServiceNotFoundException;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.service.ServiceService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

  @Autowired
  private ServiceRepository serviceRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public List<ServiceDto> findAll() {
    return this.modelMapper.map(this.serviceRepository.findAll(),
        new TypeToken<List<ServiceDto>>() {
        }.getType());
  }

  @Override
  public ServiceDto addService(ServiceDto serviceDto) {
    Service service = this.modelMapper.map(serviceDto, Service.class);
    service = serviceRepository.save(service);
    return this.modelMapper.map(service, ServiceDto.class);
  }

  @Override
  public ServiceDto modify(long serviceId, ServiceDto serviceDto) throws ServiceNotFoundException {
    this.serviceRepository.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
    return this.addService(serviceDto);
  }

  @Override
  public List<ServiceDto> searchServices(String type, Float minPrice, Float maxPrice,
      Boolean isActive) {
    return null;
  }

  @Override
  public void deleteService(long serviceId) throws ServiceNotFoundException {
    this.serviceRepository.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
    this.serviceRepository.deleteById(serviceId);
  }

}