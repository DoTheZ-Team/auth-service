package com.justdo.plug.member.domain.member.repository;


import com.justdo.plug.member.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    boolean existsById(Long id);
}
