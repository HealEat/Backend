package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.apiPayload.exception.handler.MemberHealthInfoHandler;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.web.dto.MemberProfileRequestDto;
import healeat.server.web.dto.MemberProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final DiseaseFeignClient diseaseFeignClient;

    // 프로필 정보 조회 API
    @Transactional(readOnly = true)
    public MemberProfileResponseDto getProfileInfo() {
        Member member = getAuthenticatedMember();
        return MemberProfileResponseDto.from(member);
    }

    // 프로필 설정(생성) API
    @Transactional
    public MemberProfileResponseDto createProfile(MemberProfileRequestDto request) {
        Member member = getAuthenticatedMember();
        member.updateProfile(request.getName(), uploadImageToStorage(request.getProfileImage()));
        memberRepository.save(member);
        return MemberProfileResponseDto.from(member);
    }

    // 프로필 수정 API
    @Transactional
    public MemberProfileResponseDto updateProfile(MemberProfileRequestDto request) {
        Member member = getAuthenticatedMember();
        String profileImageUrl = request.getProfileImage() != null ? uploadImageToStorage(request.getProfileImage()) : member.getProfileImageUrl();
        member.updateProfile(request.getName(), profileImageUrl);
        memberRepository.save(member);
        return MemberProfileResponseDto.from(member);
    }

    // 프로필 이미지 설정 API
    @Transactional
    public String setProfileImage(MultipartFile profileImage) {
        Member member = getAuthenticatedMember();
        String uploadedUrl = uploadImageToStorage(profileImage);
        member.updateProfileImageUrl(uploadedUrl);
        memberRepository.save(member);
        return uploadedUrl;
    }

    // 프로필 이미지 삭제 API
    @Transactional
    public void deleteProfileImage() {
        Member member = getAuthenticatedMember();
        member.updateProfileImageUrl(null);
        memberRepository.save(member);
    }

    // 닉네임 중복 확인 API
    public boolean checkNameAvailability(String name) {
        Optional<Member> existingMember = memberRepository.findByName(name);
        return existingMember.isEmpty();
    }


    private Member getAuthenticatedMember() {
        //
        // JWT 토큰으로 인증된 사용자 정보를 가져오는 로직 필요
        //
        return memberRepository.findById(1L)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private String uploadImageToStorage(MultipartFile file) {
        //
        // 파일 업로드 로직 필요
        //
        return null;
    }


    // 회원의 질병 설정
    @Transactional
    public void updateMemberDiseases(Long memberId, String searchText){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHealthInfoHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // Feign Client 호출
        List<String> diseases = diseaseFeignClient.getDiseases(
                10,
                1,
                1,
                1,  // 양방
                "SICK_NM",
                searchText  // 내가 검색한 질병명
        ).extractDiseaseNames();

        // Member 도메인의 diseases 필드 업데이트
        member.getDiseases().clear();
        member.getDiseases().addAll(diseases);

        memberRepository.save(member);
    }
}
