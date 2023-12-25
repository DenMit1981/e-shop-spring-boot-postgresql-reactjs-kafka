package com.training.eshop.dto.order;

import com.training.eshop.dto.good.GoodBuyerDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderBuyerDto {

    private BigDecimal totalPrice;

    private List<GoodBuyerDto> goods = new ArrayList<>();

    private String description;
}
