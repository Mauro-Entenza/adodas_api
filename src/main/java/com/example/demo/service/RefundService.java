package com.example.demo.service;

import com.example.demo.domain.dto.RefundDto;
import com.example.demo.exception.RefundNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

public interface RefundService {

  List<RefundDto> findAll();

  RefundDto addRefund(RefundDto refundDto);

  RefundDto modify(long refundId, RefundDto refundDto) throws RefundNotFoundException;

  void deleteRefund(long refundId) throws RefundNotFoundException;

  List<RefundDto> searchRefunds(String reason, Float minAmount, Boolean isApproved);

  RefundDto patchRefund(long refundId, Map<String, Object> updates) throws RefundNotFoundException;

  // ✅ Ahora no hace falta searchRefunds
  @Transactional(readOnly = true)
  List<RefundDto> findByFilters(String reason, Float minAmount, Boolean approved);
}
