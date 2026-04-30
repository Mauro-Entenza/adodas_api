package com.example.demo.service.impl;

import com.example.demo.domain.dto.RefundDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.domain.entity.Order;
import com.example.demo.domain.entity.Refund;
import com.example.demo.exception.RefundNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.RefundRepository;
import com.example.demo.service.RefundService;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundServiceImpl implements RefundService {

  @Autowired
  private RefundRepository refundRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  @Transactional(readOnly = true)
  public List<RefundDto> findAll() {
    return modelMapper.map(
        refundRepository.findAll(),
        new TypeToken<List<RefundDto>>() {
        }.getType()
    );
  }

  @Override
  @Transactional
  public RefundDto addRefund(RefundDto refundDto) {
    Refund refund = modelMapper.map(refundDto, Refund.class);

    Order order = orderRepository.findById(refundDto.getOrderId())
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    Customer customer = customerRepository.findById(refundDto.getCustomerId())
        .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    refund.setOrder(order);
    refund.setCustomer(customer);

    refund = refundRepository.save(refund);
    return modelMapper.map(refund, RefundDto.class);
  }

  @Override
  @Transactional
  public RefundDto modify(long refundId, RefundDto refundDto) throws RefundNotFoundException {
    Refund existingRefund = refundRepository.findById(refundId)
        .orElseThrow(RefundNotFoundException::new);

    existingRefund.setReason(refundDto.getReason());
    existingRefund.setRefundDate(refundDto.getRefundDate());
    existingRefund.setAmount(refundDto.getAmount());

    Order order = orderRepository.findById(refundDto.getOrderId())
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    Customer customer = customerRepository.findById(refundDto.getCustomerId())
        .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    existingRefund.setOrder(order);
    existingRefund.setCustomer(customer);

    existingRefund.setApproved(refundDto.isApproved());

    existingRefund = refundRepository.save(existingRefund);
    return modelMapper.map(existingRefund, RefundDto.class);
  }

  @Override
  @Transactional
  public RefundDto patchRefund(long refundId, Map<String, Object> updates)
      throws RefundNotFoundException {
    Refund existingRefund = refundRepository.findById(refundId)
        .orElseThrow(RefundNotFoundException::new);

    if (updates.containsKey("reason") && updates.get("reason") != null) {
      existingRefund.setReason((String) updates.get("reason"));
    }
    if (updates.containsKey("refundDate") && updates.get("refundDate") != null) {
      existingRefund.setRefundDate(LocalDate.parse((String) updates.get("refundDate")));
    }
    if (updates.containsKey("amount") && updates.get("amount") != null) {
      existingRefund.setAmount(((Number) updates.get("amount")).floatValue());
    }
    if (updates.containsKey("approved") && updates.get("approved") != null) {
      existingRefund.setApproved((Boolean) updates.get("approved"));
    }

    existingRefund = refundRepository.save(existingRefund);
    return modelMapper.map(existingRefund, RefundDto.class);
  }

  @Override
  @Transactional
  public void deleteRefund(long refundId) throws RefundNotFoundException {
    refundRepository.findById(refundId).orElseThrow(RefundNotFoundException::new);
    refundRepository.deleteById(refundId);
  }

  @Override
  public List<RefundDto> searchRefunds(String reason, Float minAmount, Boolean isApproved) {
    return null;
  }

  // Ahora los filtros funcionan usando el nombre correcto de la entidad
  @Override
  @Transactional(readOnly = true)
  public List<RefundDto> findByFilters(String reason, Float minAmount, Boolean approved) {
    Specification<Refund> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (reason != null && !reason.isEmpty()) {
        predicates.add(cb.like(root.get("reason"), "%" + reason + "%"));
      }
      if (minAmount != null) {
        predicates.add(cb.ge(root.get("amount"), minAmount));
      }
      if (approved != null) {
        // 🔹 Aquí usamos exactamente el nombre del campo en la entidad: isApproved
        predicates.add(cb.equal(root.get("isApproved"), approved));
      }

      return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    };

    return refundRepository.findAll(spec).stream()
        .map(r -> modelMapper.map(r, RefundDto.class))
        .collect(Collectors.toList());
  }
}