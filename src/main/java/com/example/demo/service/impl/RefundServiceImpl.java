package com.example.demo.service.impl;

import com.example.demo.domain.dto.RefundDto;
import com.example.demo.domain.entity.Refund;
import com.example.demo.repository.RefundRepository;
import com.example.demo.service.RefundService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundServiceImpl implements RefundService {

  @Autowired
  private RefundRepository refundRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public List<RefundDto> findAll() {
    return this.modelMapper.map(this.refundRepository.findAll(), new TypeToken<List<RefundDto>>() {
    }.getType());
  }

  @Override
  public RefundDto addRefund(RefundDto refundDto) {
    Refund refund = this.modelMapper.map(refundDto, Refund.class);
    refund = refundRepository.save(refund);
    return this.modelMapper.map(refund, RefundDto.class);
  }
}
