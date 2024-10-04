package com.cheolhyeon.stockdividends.web.repository;

import com.cheolhyeon.stockdividends.web.domain.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    boolean existsByCompanyIdAndDate(Long id, LocalDateTime date);
    List<DividendEntity> findAllByCompanyId(Long id);

    void deleteAllByCompanyId(Long id);
}
