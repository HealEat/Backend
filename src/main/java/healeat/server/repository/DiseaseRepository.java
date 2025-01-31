package healeat.server.repository;

import healeat.server.domain.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    List<Disease> findByNameContaining(String keyword);
}
