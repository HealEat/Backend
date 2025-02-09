package healeat.server.repository;

import healeat.server.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String name);
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
    void deleteByProviderAndProviderId(String provider, String providerId);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.refreshToken = :refreshToken WHERE m.provider = 'apple' AND m.providerId = :providerId")
    void updateAppleRefreshToken(@Param("providerId") String providerId, @Param("refreshToken") String refreshToken);
}
