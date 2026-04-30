package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.dto.RefundDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.domain.entity.Order;
import com.example.demo.domain.entity.Refund;
import com.example.demo.exception.RefundNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.RefundRepository;
import com.example.demo.service.impl.RefundServiceImpl;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

public class RefundServiceImplTest {

  @Mock
  private RefundRepository refundRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private RefundServiceImpl refundService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testFindAll_ReturnsRefundList() {
    Refund refund = new Refund();
    refund.setId(1L);
    refund.setReason("Reason A");
    refund.setRefundDate(LocalDate.of(2024, 1, 1));
    refund.setAmount(100F);
    refund.setApproved(false);
    refund.setOrder(new Order());
    refund.setCustomer(new Customer());

    RefundDto refundDto = new RefundDto();
    refundDto.setId(1L);
    refundDto.setReason("Reason A");
    refundDto.setRefundDate(LocalDate.of(2024, 1, 1));
    refundDto.setAmount(100F);
    refundDto.setApproved(false);

    when(refundRepository.findAll()).thenReturn(List.of(refund));

    Type listType = new TypeToken<List<RefundDto>>() {
    }.getType();
    when(modelMapper.map(any(List.class), eq(listType))).thenReturn(List.of(refundDto));

    List<RefundDto> result = refundService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Reason A", result.get(0).getReason());

    verify(refundRepository, times(1)).findAll();
    verify(modelMapper, times(1)).map(any(List.class), eq(listType));
  }

  @Test
  public void testAddRefund() {
    RefundDto refundDto = new RefundDto();
    refundDto.setReason("Reason B");
    refundDto.setAmount(150F);
    refundDto.setApproved(true);
    refundDto.setRefundDate(LocalDate.of(2024, 2, 1));
    refundDto.setCustomerId(1L);
    refundDto.setOrderId(2L);

    Refund refund = new Refund();
    refund.setId(10L);

    when(modelMapper.map(refundDto, Refund.class)).thenReturn(refund);
    when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
    when(orderRepository.findById(2L)).thenReturn(Optional.of(new Order()));
    when(refundRepository.save(refund)).thenReturn(refund);
    when(modelMapper.map(refund, RefundDto.class)).thenReturn(refundDto);

    RefundDto result = refundService.addRefund(refundDto);

    assertNotNull(result);
    assertEquals("Reason B", result.getReason());
    verify(refundRepository, times(1)).save(refund);
  }

  @Test
  public void testModifyRefund_Success() throws RefundNotFoundException {
    RefundDto dto = new RefundDto();
    dto.setReason("Updated reason");
    dto.setAmount(200F);
    dto.setApproved(true);
    dto.setOrderId(1L);
    dto.setCustomerId(1L);

    Refund existing = new Refund();
    existing.setId(1L);

    when(refundRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order()));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
    when(refundRepository.save(existing)).thenReturn(existing);
    when(modelMapper.map(existing, RefundDto.class)).thenReturn(dto);

    RefundDto result = refundService.modify(1L, dto);

    assertNotNull(result);
    assertEquals("Updated reason", result.getReason());
    verify(refundRepository, times(1)).save(existing);
  }

  @Test
  public void testModifyRefund_NotFound() {
    when(refundRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(RefundNotFoundException.class, () -> refundService.modify(99L, new RefundDto()));
  }

  @Test
  public void testDeleteRefund_Success() throws RefundNotFoundException {
    Refund refund = new Refund();
    refund.setId(1L);

    when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));

    refundService.deleteRefund(1L);

    verify(refundRepository, times(1)).deleteById(1L);
  }

  @Test
  public void testDeleteRefund_NotFound() {
    when(refundRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(RefundNotFoundException.class, () -> refundService.deleteRefund(99L));
  }
}
