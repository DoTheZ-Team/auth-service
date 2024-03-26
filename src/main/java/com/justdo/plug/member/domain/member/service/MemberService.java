package com.justdo.plug.member.domain.member.service;

import com.justdo.plug.member.domain.member.Member;
import com.justdo.plug.member.domain.member.dto.KakaoTokenResponse;
import com.justdo.plug.member.domain.member.dto.KakaoUserInfoResponse;
import com.justdo.plug.member.domain.member.repository.MemberRepository;
import com.justdo.plug.member.global.utils.KakaoTokenJsonData;
import com.justdo.plug.member.global.utils.KakaoUserInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final KakaoUserInfo kakaoUserInfo;
    private final KakaoTokenJsonData kakaoTokenJsonData;
    private final MemberRepository memberRepository;
    public KakaoUserInfoResponse getKakaoUserInfo(String token) {
        return kakaoUserInfo.getUserInfo(token);
    }

    public KakaoTokenResponse getKakaoTokenJsonData(String code) {
        return kakaoTokenJsonData.getToken(code);
    }

    public boolean existsById(Long id){
        return memberRepository.existsById(id);
    }

    public void saveMember(Member member) {
        memberRepository.save(member);
    }
}
