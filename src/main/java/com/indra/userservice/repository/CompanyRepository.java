package com.indra.userservice.repository;

import com.indra.userservice.domain.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    public Page<CompanyEntity> findAll(Pageable pageable);
    CompanyEntity findById(long id);
}
