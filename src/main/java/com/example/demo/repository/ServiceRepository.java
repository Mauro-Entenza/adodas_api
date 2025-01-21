package com.example.demo.repository;

import com.example.demo.domain.entity.Service;
import java.util.Collection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends CrudRepository<Service, Long> {

  Collection<Object> findAll(Specification<Service> specification);
}
