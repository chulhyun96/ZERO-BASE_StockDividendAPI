package com.cheolhyeon.stockdividends.web.model;

import com.cheolhyeon.stockdividends.web.domain.CompanyEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@Builder
public class Company {
    private Long id;
    private String ticker;
    private String name;

    @JsonCreator
    public Company() {
    }

    public static Company fromEntity(CompanyEntity companyEntity) {
        return Company.builder()
                .id(companyEntity.getId())
                .ticker(companyEntity.getTicker())
                .name(companyEntity.getName())
                .build();
    }
}
