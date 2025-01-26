package healeat.server.service;

import healeat.server.domain.Disease;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.MemberDisease;
import healeat.server.repository.DiseaseRepository;
import healeat.server.repository.MemberDiseaseRepository;
import healeat.server.repository.MemberRepository;
import healeat.server.web.dto.MemberProfileRequestDto;
import healeat.server.web.dto.MemberProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final MemberDiseaseRepository memberDiseaseRepository;

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

    // 회원이 선택한 질병들을 저장하는 API
    @Transactional
    public void saveDiseasesToMember(Member member, List<Long> diseaseIds) {

        List<Disease> diseases = diseaseRepository.findAllById(diseaseIds);

        // 중복 체크 후 새로운 질병만 추가
        List<MemberDisease> currentDiseases = member.getMemberDiseases();
        List<Disease> existingDiseases = currentDiseases.stream()
                .map(MemberDisease::getDisease)
                .collect(Collectors.toList());

        List<MemberDisease> newMemberDiseases = diseases.stream()
                .filter(disease -> !existingDiseases.contains(disease))
                .map(disease -> MemberDisease.builder()
                        .member(member)
                        .disease(disease)
                        .build())
                .collect(Collectors.toList());

        member.getMemberDiseases().addAll(newMemberDiseases);
    }

    public List<Disease> getMemberDiseases(Member member) {
        List<MemberDisease> memberDiseases = memberDiseaseRepository.findByMember(member);
        return memberDiseases.stream().map(MemberDisease::getDisease).collect(Collectors.toList());
    }

/*
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
*/

}
