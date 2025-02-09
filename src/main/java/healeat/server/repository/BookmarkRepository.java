package healeat.server.repository;

import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByMemberAndStore(Member member, Store store);

    List<Bookmark> findByMember(Member member);

    Bookmark getBookmarkById(Long id);
}
