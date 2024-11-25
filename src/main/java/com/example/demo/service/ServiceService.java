package com.example.demo.service;

import com.example.demo.domain.dto.ServiceDto;
import java.util.List;

public interface ServiceService {

  List<ServiceDto> findAll();

  ServiceDto addService(ServiceDto serviceDto);
}
