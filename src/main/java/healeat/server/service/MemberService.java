package healeat.server.service;

import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.web.dto.MemberProfileRequestDto;
import healeat.server.web.dto.MemberProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 프로필 정보 조회 API
    @Transactional(readOnly = true)
    public MemberProfileResponseDto getProfileInfo(Member member) {
        return MemberProfileResponseDto.from(member);
    }

    // 프로필 설정(생성) API
    @Transactional
    public MemberProfileResponseDto createProfile(Member member, MemberProfileRequestDto request) {
        member.updateProfile(request.getName(), request.getProfileImageUrl());
        memberRepository.save(member);
        return MemberProfileResponseDto.from(member);
    }

    // 프로필 수정 API
    @Transactional
    public MemberProfileResponseDto updateProfile(Member member, MemberProfileRequestDto request) {
        member.updateProfile(request.getName(), request.getProfileImageUrl());
        memberRepository.save(member);
        return MemberProfileResponseDto.from(member);
    }

    // 프로필 이미지 설정 API
    @Transactional
    public void setProfileImage(Member member, String profileImageUrl) {
        member.updateProfileImageUrl(profileImageUrl);
        memberRepository.save(member);
    }

    // 프로필 이미지 삭제 API
    @Transactional
    public void deleteProfileImage(Member member) {
        member.updateProfileImageUrl(null);
        memberRepository.save(member);
    }

    // 닉네임 중복 확인 API
    public boolean checkNameAvailability(String name) {
        Optional<Member> existingMember = memberRepository.findByName(name);
        return existingMember.isEmpty();
    }

}
