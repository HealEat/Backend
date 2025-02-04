package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.aws.s3.S3Uploader;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.web.dto.ImageResponseDto;
import healeat.server.web.dto.ProfileImageRequestDto;
import healeat.server.web.dto.ProfileImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {

    private final S3Uploader s3Uploader;
    private final MemberRepository memberRepository;

    @Transactional
    public Member uploadProfileImage(Long memberId, ProfileImageRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        ImageResponseDto.PresignedUrlDto presignedUrl = s3Uploader.createPresignedUrl("profiles", request.getImageExtension());

        member.updateProfileImageUrl(presignedUrl.getPublicUrl());
        memberRepository.save(member);

        return member;
    }

    @Transactional
    public ResponseEntity<Resource> getProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String profileImageUrl = member.getProfileImageUrl();
        if (profileImageUrl == null) {
            throw new MemberHandler(ErrorStatus.PROFILE_IMAGE_NOT_FOUND);
        }

        try {
            URL url = new URL(profileImageUrl);
            InputStream inputStream = url.openStream();
            byte[] imageBytes = inputStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 필요하면 다른 MIME 타입으로 변경 가능
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"profile-image.jpg\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("이미지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public Member deleteProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String profileImageUrl = member.getProfileImageUrl();
        if (profileImageUrl != null) {
            String keyName = s3Uploader.extractKeyFromUrl(profileImageUrl);
            s3Uploader.deleteFile(keyName);
            member.updateProfileImageUrl(null);
            memberRepository.save(member);

        }
        return member;
    }
}
