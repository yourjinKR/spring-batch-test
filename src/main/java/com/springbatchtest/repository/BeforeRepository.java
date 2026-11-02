package com.springbatchtest.repository;

import com.springbatchtest.entity.BeforeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeforeRepository extends JpaRepository<BeforeEntity, Long> {
}
