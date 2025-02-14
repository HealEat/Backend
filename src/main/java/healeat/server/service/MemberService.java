package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.aws.s3.AmazonS3Manager;
import healeat.server.domain.Disease;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.MemberDisease;
import healeat.server.repository.DiseaseRepository;
import healeat.server.repository.MemberDiseaseRepository;
import healeat.server.repository.MemberRepository;
import healeat.server.web.dto.MemberDiseaseResponseDto;
import healeat.server.web.dto.MemberProfileRequestDto;
import healeat.server.web.dto.MemberProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AmazonS3Manager amazonS3Manager;
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
    public Member createProfile(Member member, MultipartFile file, MemberProfileRequestDto request) {
        if (member == null) {
            throw new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        //새로운 이미지 S3 에 등록
        String uploadFileUrl = null;
        if(file != null) {
            String keyName = amazonS3Manager.generateProfileKeyName();
            uploadFileUrl = amazonS3Manager.uploadFile(keyName, file);
        }

        member.updateProfile(request.getName(), uploadFileUrl);
        return memberRepository.save(member);
    }

    // 프로필 수정 API
    @Transactional
    public Member updateProfile(Member member, MultipartFile file, MemberProfileRequestDto request) {
        if (member == null) {
            throw new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        //기존에 있던 이미지 S3 에서 삭제
        String beforeImageUrl = member.getProfileImageUrl();
        if(beforeImageUrl != null) {
            amazonS3Manager.deleteFile(beforeImageUrl);
        }

        //새로운 이미지 S3 에 등록
        String uploadFileUrl = null;
        if(file != null) {
            String keyName = amazonS3Manager.generateProfileKeyName();
            uploadFileUrl = amazonS3Manager.uploadFile(keyName, file);
        }

        member.updateProfile(request.getName(), uploadFileUrl);
        return memberRepository.save(member);
    }

    // 회원이 선택한 질병들을 저장하는 API
    @Transactional
    public MemberDiseaseResponseDto saveDiseasesToMember(Member member, String diseaseName) {

        // 질병명이 존재하는지 확인
        Disease disease = diseaseRepository.findByName(diseaseName)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.DISEASE_NOT_FOUND));

        // 중복 저장 방지
        boolean exists = memberDiseaseRepository.existsByMemberAndDisease(member, disease);
        if(exists) {
            throw new MemberHandler(ErrorStatus.ALREADY_EXISTS);
        }

        // 질병 저장하기
        MemberDisease memberDisease = MemberDisease.builder()
                .member(member)
                .disease(disease)
                .build();
        memberDiseaseRepository.save(memberDisease);

        // 회원의 최신 질병 목록 반환
        List<MemberDiseaseResponseDto.DiseaseInfo> diseaseInfoList = memberDiseaseRepository.findByMember(member)
                .stream()
                .map(md -> new MemberDiseaseResponseDto.DiseaseInfo(md.getDisease().getId(), md.getDisease().getName()))
                .toList();

        return MemberDiseaseResponseDto.from(member.getId(), diseaseInfoList);
    }
}
