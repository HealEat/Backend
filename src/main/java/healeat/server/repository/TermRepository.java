package healeat.server.repository;

import healeat.server.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository  extends JpaRepository<Term, Long> {
    List<Term> findAllByOrderByIsRequiredDesc();
}
