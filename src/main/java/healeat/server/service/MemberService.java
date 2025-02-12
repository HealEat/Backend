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
import java.util.stream.Collectors;

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
    public MemberDiseaseResponseDto saveDiseasesToMember(Member member, List<Long> diseaseIds) {

        List<Disease> diseases = diseaseRepository.findAllById(diseaseIds);
        // 기존 질병 데이터 삭제
        memberDiseaseRepository.deleteAllByMember(member);
        // 새로운 질병 데이터 추가
        List<MemberDisease> newMemberDiseases = diseases.stream()
                .map(disease -> MemberDisease.builder()
                        .member(member)
                        .disease(disease)
                        .build())
                .collect(Collectors.toList());
        // 새로운 데이터 저장
        memberDiseaseRepository.saveAll(newMemberDiseases);
        // 최신 질병 목록 가져오기
        List<MemberDiseaseResponseDto.DiseaseInfo> diseaseInfoList = newMemberDiseases.stream()
                .map(md -> new MemberDiseaseResponseDto.DiseaseInfo(md.getDisease().getId(), md.getDisease().getName()))
                .collect(Collectors.toList());

        return MemberDiseaseResponseDto.from(member.getId(), diseaseInfoList);
    }
}
