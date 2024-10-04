package com.cheolhyeon.stockdividends.web.domain;

import com.cheolhyeon.stockdividends.web.model.Company;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class CompanyEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String ticker;

    public static CompanyEntity fromDTO(Company company) {
        String replace = company.getName().split("\\(")[0].trim();
        return CompanyEntity.builder()
                .name(replace)
                .ticker(company.getTicker().trim())
                .build();
    }
}
