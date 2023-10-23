package com.indra.userservice.repository;

import com.indra.userservice.domain.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    public Page<MemberEntity> findAll(Pageable pageable);
    MemberEntity findById(long id);
    MemberEntity findByAccountId(String accountId);
}
