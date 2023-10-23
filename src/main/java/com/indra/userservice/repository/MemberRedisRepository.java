package com.indra.userservice.repository;

import com.indra.userservice.domain.MemberRedis;
import org.springframework.data.repository.CrudRepository;

public interface MemberRedisRepository extends CrudRepository<MemberRedis, String> {
}
