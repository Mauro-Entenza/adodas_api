package com.example.demo.service.impl;

import com.example.demo.domain.dto.RefundDto;
import com.example.demo.domain.entity.Refund;
import com.example.demo.exception.RefundNotFoundException;
import com.example.demo.repository.RefundRepository;
import com.example.demo.service.RefundService;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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

  @Override
  public RefundDto modify(long refundId, RefundDto refundDto) throws RefundNotFoundException {
    Refund existingRefund = this.refundRepository.findById(refundId)
        .orElseThrow(RefundNotFoundException::new);
    existingRefund.setReason(refundDto.getReason());
    existingRefund.setRefundDate(refundDto.getRefundDate());
    existingRefund.setAmount(refundDto.getAmount());
    existingRefund.setOrderId(refundDto.getOrderId());
    existingRefund.setCustomerId(refundDto.getCustomerId());
    existingRefund.setApproved(refundDto.isApproved());
    existingRefund = refundRepository.save(existingRefund);
    return this.modelMapper.map(existingRefund, RefundDto.class);
  }

  @Override
  public void deleteRefund(long refundId) throws RefundNotFoundException {
    this.refundRepository.findById(refundId).orElseThrow(RefundNotFoundException::new);
    this.refundRepository.deleteById(refundId);
  }

  @Override
  public List<RefundDto> searchRefunds(String reason, Float minAmount, Boolean isApproved) {
    Specification<Refund> specification = (root, query, criteriaBuilder) -> {
      Predicate predicate = criteriaBuilder.conjunction();
      if (reason != null && !reason.isEmpty()) {
        predicate = criteriaBuilder.and(predicate,
            criteriaBuilder.like(root.get("reason"), "%" + reason + "%"));
      }
      if (minAmount != null) {
        predicate = criteriaBuilder.and(predicate,
            criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
      }
      if (isApproved != null) {
        predicate = criteriaBuilder.and(predicate,
            criteriaBuilder.equal(root.get("isApproved"), isApproved));
      }

      return predicate;
    };

    return refundRepository.findAll(specification).stream()
        .map(refund -> modelMapper.map(refund, RefundDto.class))
        .toList();
  }

}
