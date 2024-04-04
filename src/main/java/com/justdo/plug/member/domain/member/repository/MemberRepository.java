package com.justdo.plug.member.domain.member.repository;


import com.justdo.plug.member.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByProviderId(Long kakaoId);
    boolean existsByProviderId(Long kakaoId);
}
