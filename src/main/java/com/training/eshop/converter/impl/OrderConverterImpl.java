package com.training.eshop.converter.impl;

import com.training.eshop.converter.GoodConverter;
import com.training.eshop.converter.OrderConverter;
import com.training.eshop.dto.good.GoodBuyerDto;
import com.training.eshop.dto.order.OrderAdminViewDto;
import com.training.eshop.dto.order.OrderBuyerDto;
import com.training.eshop.dto.order.OrderMessageDto;
import com.training.eshop.model.Good;
import com.training.eshop.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderConverterImpl implements OrderConverter {

    private final GoodConverter goodConverter;

    @Override
    public OrderBuyerDto convertToOrderBuyerDto(Order order) {
        OrderBuyerDto orderBuyerDto = new OrderBuyerDto();

        orderBuyerDto.setTotalPrice(order.getTotalPrice());
        orderBuyerDto.setGoods(convertToListGoodDto(order.getGoods()));
        orderBuyerDto.setDescription(order.getDescription());

        return orderBuyerDto;
    }

    @Override
    public OrderAdminViewDto convertToOrderAdminViewDto(Order order) {
        OrderAdminViewDto orderDto = new OrderAdminViewDto();

        orderDto.setId(order.getId());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setUser(order.getUser().getName());
        orderDto.setDescription(order.getDescription());

        return orderDto;
    }

    @Override
    public OrderMessageDto convertToOrderMessageDto(Order order) {
        OrderMessageDto orderDto = new OrderMessageDto();

        orderDto.setId(order.getId());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setUser(order.getUser().getName());
        orderDto.setGoods(convertToListGoodDto(order.getGoods()));

        return orderDto;
    }

    @Override
    public List<GoodBuyerDto> convertToListGoodDto(List<Good> goods) {
        return goods
                .stream()
                .map(goodConverter::convertToGoodBuyerDto)
                .collect(Collectors.toList());
    }
}
