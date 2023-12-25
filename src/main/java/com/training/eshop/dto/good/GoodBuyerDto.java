package com.training.eshop.dto.good;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class GoodBuyerDto {

    private String title;

    private BigDecimal price;
}
