package com.justdo.plug.member.domain.member.repository;


import com.justdo.plug.member.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByProviderId(Long kakaoId);
    boolean existsByProviderId(Long kakaoId);
}
