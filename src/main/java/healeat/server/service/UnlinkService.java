package healeat.server.service;

import healeat.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnlinkService {

    private final MemberRepository memberRepository;

    @Transactional
    public void deleteKakaoMember(String provider, String providerId) {
        System.out.println(" DB에서 삭제: provider=" + provider + ", providerId=" + providerId);
        memberRepository.deleteByProviderAndProviderId(provider, providerId);
    }
}
