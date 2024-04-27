package com.justdo.plug.member.domain.member.repository;


import com.justdo.plug.member.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByProviderId(Long kakaoId);
    boolean existsByProviderId(Long kakaoId);

    @Query("SELECT m FROM Member m WHERE m.id IN :memberIdList")
    List<Member> findAllMemberIdList(List<Long> memberIdList);
}
