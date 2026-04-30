package com.example.demo.service.impl;

import com.example.demo.domain.dto.OrderDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.domain.entity.Item;
import com.example.demo.domain.entity.Order;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final CustomerRepository customerRepository;
  private final ModelMapper modelMapper;

  public OrderServiceImpl(OrderRepository orderRepository,
      ItemRepository itemRepository,
      CustomerRepository customerRepository,
      ModelMapper modelMapper) {
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
    this.customerRepository = customerRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderDto> findAll() {
    return orderRepository.findAll().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderDto> findByFilters(Boolean delivered, Float minPrice, Float maxPrice) {
    Specification<Order> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (delivered != null) {
        predicates.add(cb.equal(root.get("isDelivered"), delivered)); // <- aquí
      }

      if (minPrice != null && minPrice > 0) {
        predicates.add(cb.ge(root.get("orderPrice"), minPrice));
      }

      if (maxPrice != null && maxPrice > 0) {
        predicates.add(cb.le(root.get("orderPrice"), maxPrice));
      }

      return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    };

    return orderRepository.findAll(spec).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public OrderDto addOrder(OrderDto orderDto) {
    List<Item> items = itemRepository.findAllById(orderDto.getItemIds());
    if (items.size() != orderDto.getItemIds().size()) {
      throw new IllegalArgumentException("Uno o más items no existen");
    }

    Customer customer = customerRepository.findById(orderDto.getCustomerId())
        .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

    Order order = new Order();
    order.setCustomer(customer);
    order.setCustomerNotes(orderDto.getCustomerNotes());
    order.setOrderPrice(orderDto.getOrderPrice());
    order.setDelivered(orderDto.isDelivered());
    order.setOrderDate(orderDto.getOrderDate());

    Order savedOrder = orderRepository.save(order);

    items.forEach(item -> {
      item.setOrder(savedOrder);
      itemRepository.save(item);
    });

    return convertToDto(savedOrder);
  }

  @Override
  @Transactional
  public OrderDto modify(long orderId, OrderDto orderDto) throws OrderNotFoundException {
    Order existingOrder = orderRepository.findById(orderId)
        .orElseThrow(OrderNotFoundException::new);

    List<Item> items = itemRepository.findAllById(orderDto.getItemIds());
    if (items.size() != orderDto.getItemIds().size()) {
      throw new IllegalArgumentException("Uno o más items no existen");
    }

    existingOrder.setCustomerNotes(orderDto.getCustomerNotes());
    existingOrder.setOrderPrice(orderDto.getOrderPrice());
    existingOrder.setDelivered(orderDto.isDelivered());
    existingOrder.setOrderDate(orderDto.getOrderDate());

    existingOrder.getItems().clear();

    items.forEach(item -> {
      item.setOrder(existingOrder);
      itemRepository.save(item);
    });

    return convertToDto(orderRepository.save(existingOrder));
  }

  @Override
  @Transactional
  public OrderDto patchOrder(long orderId, Map<String, Object> updates)
      throws OrderNotFoundException {
    Order existingOrder = orderRepository.findById(orderId)
        .orElseThrow(OrderNotFoundException::new);

    if (updates.containsKey("customerNotes") && updates.get("customerNotes") != null) {
      existingOrder.setCustomerNotes((String) updates.get("customerNotes"));
    }
    if (updates.containsKey("orderPrice") && updates.get("orderPrice") != null) {
      existingOrder.setOrderPrice(((Number) updates.get("orderPrice")).floatValue());
    }
    if (updates.containsKey("delivered") && updates.get("delivered") != null) {
      existingOrder.setDelivered((Boolean) updates.get("delivered"));
    }
    if (updates.containsKey("orderDate") && updates.get("orderDate") != null) {
      existingOrder.setOrderDate(LocalDate.parse((String) updates.get("orderDate")));
    }

    return convertToDto(orderRepository.save(existingOrder));
  }

  @Override
  @Transactional
  public void deleteOrder(long orderId) throws OrderNotFoundException {
    if (!orderRepository.existsById(orderId)) {
      throw new OrderNotFoundException();
    }
    orderRepository.deleteById(orderId);
  }

  private OrderDto convertToDto(Order order) {
    OrderDto dto = modelMapper.map(order, OrderDto.class);

    List<Long> itemIds = itemRepository.findIdsByOrder(order.getId());
    dto.setItemIds(itemIds);

    if (order.getCustomer() != null) {
      dto.setCustomerId(order.getCustomer().getId());
    }

    return dto;
  }
}