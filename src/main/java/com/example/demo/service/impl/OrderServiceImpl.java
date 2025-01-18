package com.example.demo.service.impl;

import com.example.demo.domain.dto.OrderDto;
import com.example.demo.domain.entity.Item;
import com.example.demo.domain.entity.Order;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ItemRepository itemRepository;

  @Override
  public List<OrderDto> findAll() {
    Iterable<Order> orderList = this.orderRepository.findAll();
    List<OrderDto> orderDtoList = new ArrayList<>();
    for (Order order : orderList) {
      OrderDto orderDto = this.modelMapper.map(order, OrderDto.class);
      List<Long> itemIds = new ArrayList<>();
      for (Item item : order.getItemIds()) {
        itemIds.add(item.getId());
      }
      orderDto.setItemIds(itemIds);
      orderDtoList.add(orderDto);
    }
    return orderDtoList;
  }

  @Override
  public OrderDto addOrder(OrderDto orderDto) {
    try {
      Order order = this.modelMapper.map(orderDto, Order.class);
      List<Item> itemList = new ArrayList<>();
      for (Long id : orderDto.getItemIds()) {
        itemList.add(this.itemRepository.findById(id).orElse(null));
      }
      order.setItemIds(itemList);
      order = orderRepository.save(order);

      return orderDto;
    } catch (IllegalArgumentException e) {

      throw new IllegalStateException("Error while mapping or saving the order: " + e.getMessage(),
          e);
    } catch (Exception e) {

      throw new IllegalStateException("Unexpected error while adding the order: " + e.getMessage(),
          e);
    }
  }


  @Override
  public OrderDto modify(long orderId, OrderDto orderDto) throws OrderNotFoundException {
    Order existingOrder = this.orderRepository.findById(orderId)
        .orElseThrow(OrderNotFoundException::new);

    existingOrder.setOrderPrice(orderDto.getOrderPrice());
    existingOrder.setCustomerNotes(orderDto.getCustomerNotes());
    existingOrder.setOrderDate(orderDto.getOrderDate());
    existingOrder.setDelivered(orderDto.isDelivered());
    Order updatedOrder = this.orderRepository.save(existingOrder);
    return this.modelMapper.map(updatedOrder, OrderDto.class);
  }

  @Override
  public List<OrderDto> searchOrders(String status, Float minPrice, Date maxPrice) {
    return null;
  }

  @Override
  public List<OrderDto> getOrdersByFilter(Long customerId, LocalDate orderDate, Float minPrice) {
    return null;
  }

  @Override
  public void deleteOrder(long orderId) throws OrderNotFoundException {
    this.orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
    this.orderRepository.deleteById(orderId);
  }

  @Override
  public List<OrderDto> searchOrders(Boolean isDelivered, Float minPrice, LocalDate orderDate) {
    return orderRepository.findAll((root, query, criteriaBuilder) -> {
          Predicate predicate = criteriaBuilder.conjunction();
          if (isDelivered != null) {
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.equal(root.get("isDelivered"), isDelivered));
          }
          if (minPrice != null) {
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.greaterThanOrEqualTo(root.get("orderPrice"), minPrice));
          }
          if (orderDate != null) {
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.equal(root.get("orderDate"), orderDate));
          }
          return predicate;
        }).stream()
        .map(order -> modelMapper.map(order, OrderDto.class))
        .toList();
  }

}
