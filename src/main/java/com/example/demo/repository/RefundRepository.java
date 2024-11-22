package com.example.demo.repository;

import com.example.demo.domain.entity.Refund;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends CrudRepository<Refund, Long> {

}
