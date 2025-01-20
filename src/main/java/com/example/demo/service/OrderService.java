package com.example.demo.service;

import com.example.demo.domain.dto.OrderDto;
import com.example.demo.exception.OrderNotFoundException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderService {

  List<OrderDto> findAll();

  OrderDto addOrder(OrderDto orderDto);

  void deleteOrder(long orderId) throws OrderNotFoundException;

  OrderDto modify(long orderId, OrderDto orderDto) throws OrderNotFoundException;

  List<OrderDto> searchOrders(String status, Float minPrice, Date maxPrice);

  List<OrderDto> getOrdersByFilter(Long customerId, LocalDate orderDate, Float minPrice);

  List<OrderDto> searchOrders(Boolean isDelivered, Float minPrice, LocalDate orderDate);
}
