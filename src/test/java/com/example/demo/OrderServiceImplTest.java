package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.dto.OrderDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.domain.entity.Item;
import com.example.demo.domain.entity.Order;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.impl.OrderServiceImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private ItemRepository itemRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private ModelMapper modelMapper;
  @InjectMocks
  private OrderServiceImpl orderService;

  @Test
  void addOrder_Success() {
    // GIVEN
    Item item1 = new Item();
    item1.setId(1L);
    Item item2 = new Item();
    item2.setId(2L);
    when(itemRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(item1, item2));

    Customer customer = new Customer();
    customer.setId(1L);
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

    OrderDto dto = new OrderDto();
    dto.setCustomerId(1L);
    dto.setOrderPrice(100f);
    dto.setDelivered(false);
    dto.setOrderDate(LocalDate.now());
    dto.setItemIds(Arrays.asList(1L, 2L));

    Order savedOrder = new Order();
    savedOrder.setId(1L);
    savedOrder.setCustomer(customer);
    savedOrder.setItems(Arrays.asList(item1, item2));
    savedOrder.setOrderPrice(100f);
    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
    when(modelMapper.map(savedOrder, OrderDto.class)).thenReturn(dto);

    // WHEN
    OrderDto result = orderService.addOrder(dto);

    // THEN
    assertEquals(1L, result.getCustomerId());
    assertEquals(100f, result.getOrderPrice());
    verify(orderRepository).save(any(Order.class));
    verify(itemRepository, times(2)).save(any(Item.class)); // 2 items saved
  }


  @Test
  void findAll_ReturnsDtoList() {
    // GIVEN
    Customer customer = new Customer();
    customer.setId(1L);
    Item item = new Item();
    item.setId(1L);

    Order order1 = new Order();
    order1.setId(1L);
    order1.setCustomer(customer);
    order1.setItems(List.of(item));
    order1.setOrderPrice(50f);
    order1.setDelivered(false);

    Order order2 = new Order();
    order2.setId(2L);
    order2.setCustomer(customer);
    order2.setItems(List.of(item));
    order2.setOrderPrice(100f);
    order2.setDelivered(true);

    List<Order> orders = Arrays.asList(order1, order2);
    when(orderRepository.findAll()).thenReturn(orders);

    OrderDto dto1 = new OrderDto();
    dto1.setId(1L);
    dto1.setCustomerId(1L);
    dto1.setOrderPrice(50f);
    dto1.setDelivered(false);
    OrderDto dto2 = new OrderDto();
    dto2.setId(2L);
    dto2.setCustomerId(1L);
    dto2.setOrderPrice(100f);
    dto2.setDelivered(true);

    when(modelMapper.map(order1, OrderDto.class)).thenReturn(dto1);
    when(modelMapper.map(order2, OrderDto.class)).thenReturn(dto2);

    // WHEN
    List<OrderDto> result = orderService.findAll();

    // THEN
    assertEquals(2, result.size());
    assertEquals(50f, result.get(0).getOrderPrice());
    assertTrue(result.get(1).isDelivered());
    verify(orderRepository).findAll();
  }

  @Test
  void findByFilters_DeliveredTrue() {
    // GIVEN
    Boolean delivered = true;
    Float minPrice = null;
    Float maxPrice = null;

    Customer customer = new Customer();
    customer.setId(1L);
    Item item = new Item();
    item.setId(1L);

    Order order1 = new Order();
    order1.setId(1L);
    order1.setCustomer(customer);
    order1.setDelivered(true);
    order1.setOrderPrice(100f);

    List<Order> filteredOrders = List.of(order1);
    when(orderRepository.findAll(any(Specification.class))).thenReturn(filteredOrders);

    OrderDto dto = new OrderDto();
    dto.setId(1L);
    dto.setDelivered(true);
    when(modelMapper.map(order1, OrderDto.class)).thenReturn(dto);

    // WHEN
    List<OrderDto> result = orderService.findByFilters(delivered, minPrice, maxPrice);

    // THEN
    assertEquals(1, result.size());
    assertTrue(result.get(0).isDelivered());
    verify(orderRepository).findAll(any(Specification.class));
  }

  @Test
  void findByFilters_PriceRange() {
    // GIVEN
    Boolean delivered = null;
    Float minPrice = 75f;
    Float maxPrice = 125f;

    Customer customer = new Customer();
    customer.setId(1L);
    Order order = new Order();
    order.setId(1L);
    order.setCustomer(customer);
    order.setOrderPrice(100f);

    List<Order> filteredOrders = List.of(order);
    when(orderRepository.findAll(any(Specification.class))).thenReturn(filteredOrders);

    OrderDto dto = new OrderDto();
    dto.setId(1L);
    dto.setOrderPrice(100f);
    when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);

    // WHEN
    List<OrderDto> result = orderService.findByFilters(delivered, minPrice, maxPrice);

    // THEN
    assertEquals(1, result.size());
    assertEquals(100f, result.get(0).getOrderPrice());
  }


  @Test
  void patchOrder_NotFound_ThrowsException() {
    // GIVEN
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());
    Map<String, Object> updates = Map.of("orderPrice", 100f);

    // WHEN & THEN
    assertThrows(OrderNotFoundException.class,
        () -> orderService.patchOrder(1L, updates));
    verify(orderRepository).findById(1L);
  }

  @Test
  void deleteOrder_Success() throws OrderNotFoundException {
    when(orderRepository.existsById(1L)).thenReturn(true);
    doNothing().when(orderRepository).deleteById(1L);

    // WHEN
    orderService.deleteOrder(1L);

    // THEN
    verify(orderRepository).existsById(1L);
    verify(orderRepository).deleteById(1L);
  }

  @Test
  void deleteOrder_NotFound_ThrowsException() {
    when(orderRepository.existsById(1L)).thenReturn(false);

    // WHEN & THEN
    OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
        () -> orderService.deleteOrder(1L));

    verify(orderRepository).existsById(1L);
    verify(orderRepository, never()).deleteById(1L);
  }

  @Test
  void modify_Success() throws OrderNotFoundException {
    // GIVEN
    Customer customer = new Customer();
    customer.setId(1L);
    Item item1 = new Item();
    item1.setId(1L);
    Item item2 = new Item();
    item2.setId(2L);

    Order existingOrder = new Order();
    existingOrder.setId(1L);
    existingOrder.setCustomer(customer);
    existingOrder.setItems(new ArrayList<>());

    when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
    when(itemRepository.findAllById(anyList())).thenReturn(Arrays.asList(item1, item2));

    OrderDto inputDto = new OrderDto();
    inputDto.setCustomerId(1L);
    inputDto.setCustomerNotes("Nota nueva");
    inputDto.setOrderPrice(150f);
    inputDto.setDelivered(true);
    inputDto.setItemIds(Arrays.asList(1L, 2L));

    Order updatedOrder = new Order();
    updatedOrder.setId(1L);
    OrderDto resultDto = new OrderDto();
    resultDto.setId(1L);
    resultDto.setOrderPrice(150f);

    // ✅ FIX: ModelMapper correcto
    when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
    when(modelMapper.map(any(Order.class), argThat(clazz -> OrderDto.class.equals(clazz))))
        .thenReturn(resultDto);

    // WHEN
    OrderDto result = orderService.modify(1L, inputDto);

    // THEN
    assertEquals(1L, result.getId());
    assertEquals(150f, result.getOrderPrice());
    verify(orderRepository).save(any(Order.class));
  }


}
