package com.example.demo.repository;

import com.example.demo.domain.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

  @Query("SELECT i.id FROM Item i WHERE i.order.id = :orderId")
  List<Long> findIdsByOrder(@Param("orderId") Long orderId);
}
