package com.cheolhyeon.stockdividends.web.domain;

import com.cheolhyeon.stockdividends.web.model.Dividend;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"companyId","date"}
        )
})
public class DividendEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private LocalDateTime  date;

    private String dividend;

    public static DividendEntity fromDTO(Long companyId,Dividend dividend) {
        return DividendEntity.builder()
                .companyId(companyId)
                .date(dividend.getDate())
                .dividend(dividend.getDividend())
                .build();
    }
}
