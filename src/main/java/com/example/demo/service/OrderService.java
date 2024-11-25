package com.example.demo.service;

import com.example.demo.domain.dto.OrderDto;
import java.util.List;

public interface OrderService {

  List<OrderDto> findAll();

  OrderDto addOrder(OrderDto orderDto);
}
