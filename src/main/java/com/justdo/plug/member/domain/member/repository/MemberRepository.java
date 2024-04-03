package com.justdo.plug.member.domain.member.repository;


import com.justdo.plug.member.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByKakaoId(Long kakaoId);
    boolean existsByKakaoId(Long kakaoId);
}
