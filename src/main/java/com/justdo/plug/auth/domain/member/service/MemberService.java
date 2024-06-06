package com.justdo.plug.auth.domain.member.service;

import com.justdo.plug.auth.domain.blog.BlogClient;
import com.justdo.plug.auth.domain.member.Member;
import com.justdo.plug.auth.domain.member.dto.MemberInfoRequest;
import com.justdo.plug.auth.domain.member.dto.MemberInfoResponse;
import com.justdo.plug.auth.domain.member.repository.MemberRepository;
import com.justdo.plug.auth.global.exception.ApiException;
import com.justdo.plug.auth.global.jwt.JwtTokenProvider;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.KakaoTokenJsonData;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.KakaoUserInfoJsonData;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.JwtTokenResponse;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.KakaoAccount;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.KakaoProfile;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.response.KakaoTokenResponse;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.response.KakaoUserInfoResponse;
import com.justdo.plug.auth.global.response.code.status.ErrorStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoTokenJsonData kakaoTokenJsonData;
    private final KakaoUserInfoJsonData kakaoUserInfoJsonData;
    private final BlogClient blogClient;

    // 카카오 로그인 처리
    @Transactional
    public JwtTokenResponse processKakaoLogin(String code) {

        KakaoTokenResponse tokenResponse = kakaoTokenJsonData.getToken(code);
        KakaoUserInfoResponse userInfo = kakaoUserInfoJsonData.getUserInfo(
                tokenResponse.getAccess_token());

        Map<String, Long> resultMap = registerMemberIfNew(userInfo);

        String accessToken = jwtTokenProvider.generateAccessToken(resultMap.get("memberId"), resultMap.get("blogId"));
        String refreshToken = jwtTokenProvider.generateRefreshToken(resultMap.get("memberId"));

        return JwtTokenResponse.createJwtTokenResponse(accessToken, refreshToken);
    }

    private Map<String, Long> registerMemberIfNew(KakaoUserInfoResponse userInfo) {

        Optional<Member> optionalMember = memberRepository.findByProviderId(userInfo.getId());
        HashMap<String, Long> resultMap = new HashMap<>();

        if (optionalMember.isPresent()) {
            Member existMember = optionalMember.get();
            Long blogId = blogClient.getBlogId(existMember.getId());

            resultMap.put("memberId", existMember.getId());
            resultMap.put("blogId", blogId);
        } else {
            Member newMember = createNewMember(userInfo);
            Member savedMember = memberRepository.save(newMember);
            Long blogId = blogClient.createBlog(savedMember.getId());

            resultMap.put("memberId", savedMember.getId());
            resultMap.put("blogId", blogId);
        }
        return resultMap;
    }

    private Member createNewMember(KakaoUserInfoResponse userInfo) {

        KakaoAccount kakaoAccount = userInfo.getKakao_account();
        KakaoProfile kakaoProfile = kakaoAccount.getProfile();

        return Member.builder()
                .providerId(userInfo.getId())
                .provider("kakao")
                .email(kakaoAccount.getEmail())
                .nickname(kakaoProfile.getNickname())
                .build();
    }


    // 멤버 정보 조회
    public MemberInfoResponse getMemberInfo(String accessToken) {

        jwtTokenProvider.checkToken(accessToken);
        Long memberId = jwtTokenProvider.extractUserIdFromToken(accessToken);
        Member foundmMember = findMember(memberId);

        return MemberInfoResponse.toMemberInfoResponse(foundmMember);
    }

    // 멤버 정보 수정
    @Transactional
    public MemberInfoResponse updateMemberInfo(String accessToken,
            MemberInfoRequest memberInfoRequest) {

        jwtTokenProvider.checkToken(accessToken);
        Long memberId = jwtTokenProvider.extractUserIdFromToken(accessToken);
        Member foundMember = findMember(memberId);
        foundMember.updateMember(memberInfoRequest);

        return MemberInfoResponse.toMemberInfoResponse(foundMember);
    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
        jwtTokenProvider.deleteRefreshToken(accessToken);
    }

    // 액세스 토큰 재발급
    @Transactional
    public String reissueAccessToken(String accessToken, String refreshToken) {
        jwtTokenProvider.checkToken(accessToken);

        jwtTokenProvider.validateRefreshToken(refreshToken);
        Long memberId = jwtTokenProvider.extractUserIdFromToken(accessToken);
        Long blogId = blogClient.getBlogId(memberId);

        return jwtTokenProvider.generateAccessToken(memberId, blogId);
    }

    // 유저 리스트에서 유저 닉네임 리스트 조회
    public List<String> getMemberNicknames(List<Long> memberIdList) {

        return memberRepository.findAllMemberIdList(memberIdList).stream()
                .map(Member::getNickname)
                .toList();
    }

    // 특정 멤버 검색
    private Member findMember(Long userId) {
        return memberRepository.findById(userId).orElseThrow(
                () -> new ApiException(ErrorStatus._MEMBER_NOT_FOUND_ERROR));
    }

    public String findMemberName(Long memberId) {
        return findMember(memberId).getNickname();
    }

}
