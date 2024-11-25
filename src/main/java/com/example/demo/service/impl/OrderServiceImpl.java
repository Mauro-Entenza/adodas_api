package com.example.demo.service.impl;

import com.example.demo.domain.dto.OrderDto;
import com.example.demo.domain.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public List<OrderDto> findAll() {
    return this.modelMapper.map(this.orderRepository.findAll(), new TypeToken<List<OrderDto>>() {
    }.getType());
  }

  @Override
  public OrderDto addOrder(OrderDto orderDto) {
    Order order = this.modelMapper.map(orderDto, Order.class);
    order = orderRepository.save(order);
    return this.modelMapper.map(order, OrderDto.class);
  }
}
