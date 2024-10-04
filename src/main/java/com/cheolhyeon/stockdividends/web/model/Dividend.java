package com.cheolhyeon.stockdividends.web.model;

import com.cheolhyeon.stockdividends.web.domain.DividendEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Dividend {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;
    private String dividend;

    @JsonCreator
    public Dividend() {
    }

    public static Dividend fromEntity(DividendEntity entity) {
        return Dividend.builder()
                .date(entity.getDate())
                .dividend(entity.getDividend())
                .build();
    }
}

