package com.training.eshop.converter;

import com.training.eshop.dto.good.GoodBuyerDto;
import com.training.eshop.dto.order.OrderAdminViewDto;
import com.training.eshop.dto.order.OrderBuyerDto;
import com.training.eshop.dto.order.OrderMessageDto;
import com.training.eshop.model.Good;
import com.training.eshop.model.Order;

import java.util.List;

public interface OrderConverter {

    OrderBuyerDto convertToOrderBuyerDto(Order order);

    OrderAdminViewDto convertToOrderAdminViewDto(Order order);

    OrderMessageDto convertToOrderMessageDto(Order order);

    List<GoodBuyerDto> convertToListGoodDto(List<Good> goods);
}
