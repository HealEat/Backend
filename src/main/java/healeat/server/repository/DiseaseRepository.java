package healeat.server.repository;

import healeat.server.domain.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    List<Disease> findByNameContaining(String keyword);
    Optional<Disease> findByName(String name);  // 질병명으로 찾기
}
