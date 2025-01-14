package healeat.server.service;

import healeat.server.domain.Store;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreQueryServiceImpl implements StoreQueryService {

    private final StoreRepository storeRepository;

    @Override
    public Page<Store> searchByFilter(StoreRequestDto.SearchFilterDto request) {

        return null;
    }
}
