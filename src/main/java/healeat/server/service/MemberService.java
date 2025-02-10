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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
    public Member updateProfile(Member member, MultipartFile file,MemberProfileRequestDto request) {
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

        // 현재 회원의 기존 질병 목록 조회
        List<MemberDisease> existingMemberDiseases = memberDiseaseRepository.findByMember(member);
        // 기존 질병 ID 목록
        Set<Long> existingDiseaseIds = existingMemberDiseases.stream()
                .map(md -> md.getDisease().getId())
                .collect(Collectors.toSet());
        // 추가할 질병 목록 (새로운 ID만 추가)
        List<Disease> newDiseases = diseaseRepository.findAllById(diseaseIds)
                .stream()
                .filter(disease -> !existingDiseaseIds.contains(disease.getId())) // 기존에 없는 질병만 추가
                .toList();
        // 삭제할 질병 목록 (기존에는 있었지만 새로운 리스트에는 없는 경우 삭제)
        List<MemberDisease> diseasesToRemove = existingMemberDiseases.stream()
                .filter(md -> !diseaseIds.contains(md.getDisease().getId()))
                .toList();
        // 삭제할 질병 제거
        if (!diseasesToRemove.isEmpty()) {
            memberDiseaseRepository.deleteAll(diseasesToRemove);
        }
        // 새로운 질병 추가
        if (!newDiseases.isEmpty()) {
            List<MemberDisease> newMemberDiseases = newDiseases.stream()
                    .map(disease -> MemberDisease.builder()
                            .member(member)
                            .disease(disease)
                            .build())
                    .toList();
            memberDiseaseRepository.saveAll(newMemberDiseases);
        }
        // 최신 질병 목록 반환
        List<MemberDiseaseResponseDto.DiseaseInfo> updatedDiseaseInfoList = memberDiseaseRepository.findByMember(member)
                .stream()
                .map(md -> new MemberDiseaseResponseDto.DiseaseInfo(md.getDisease().getId(), md.getDisease().getName()))
                .toList();

        return MemberDiseaseResponseDto.from(member.getId(), updatedDiseaseInfoList);
    }

    @PersistenceContext
    private EntityManager entityManager;
    // disease_names.csv 파일 읽어서 DB 저장
    @Transactional
    public void saveDiseasesFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // member_disease 테이블의 데이터 삭제
            memberDiseaseRepository.deleteAll();
            memberDiseaseRepository.flush();
            // 기존 질병 데이터 전체 삭제
            diseaseRepository.deleteAll();
            diseaseRepository.flush();
            // AUTO_INCREMENT 값 초기화
            entityManager.createNativeQuery("ALTER TABLE disease AUTO_INCREMENT = 1").executeUpdate();
            List<Disease> diseaseList = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    diseaseList.add(Disease.builder().name(line).build());
                }
            }
            // 새로운 질병명 목록 저장
            if(!diseaseList.isEmpty()) {
                diseaseRepository.saveAll(diseaseList);
            }
            System.out.println("CSV 데이터가 성공적으로 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("CSV 파일을 읽는 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("데이터 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
