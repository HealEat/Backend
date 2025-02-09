package healeat.server.repository;

import healeat.server.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByKakaoPlaceId(Long kakaoPlaceId);
}
