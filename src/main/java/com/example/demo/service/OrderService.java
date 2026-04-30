package com.example.demo.service;

import com.example.demo.domain.dto.OrderDto;
import com.example.demo.exception.OrderNotFoundException;
import java.util.List;
import java.util.Map;

public interface OrderService {

  List<OrderDto> findAll();

  OrderDto addOrder(OrderDto orderDto);

  OrderDto modify(long orderId, OrderDto orderDto) throws OrderNotFoundException;

  OrderDto patchOrder(long orderId, Map<String, Object> updates) throws OrderNotFoundException;

  void deleteOrder(long orderId) throws OrderNotFoundException;

  // ✅ Filtro directo para GET /orders
  List<OrderDto> findByFilters(Boolean delivered, Float minPrice, Float maxPrice);
}