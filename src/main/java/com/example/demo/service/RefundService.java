package com.example.demo.service;

import com.example.demo.domain.dto.RefundDto;
import java.util.List;

public interface RefundService {

  List<RefundDto> findAll();

  RefundDto addRefund(RefundDto refundDto);
}
