package com.training.eshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.training.eshop.dto.good.GoodBuyerDto;
import com.training.eshop.dto.order.OrderAdminViewDto;
import com.training.eshop.dto.order.OrderBuyerDto;
import com.training.eshop.model.Good;
import com.training.eshop.model.Order;

import java.util.List;

public interface OrderService {

    Order save(Order order, String login) throws JsonProcessingException;

    void addGoodToOrder(GoodBuyerDto goodBuyerDto);

    void deleteGoodFromOrder(GoodBuyerDto goodBuyerDto);

    OrderBuyerDto getById(Long id);

    List<OrderAdminViewDto> getAll(String sortField, String sortDirection, int pageSize, int pageNumber);

    List<Good> getCartGoods();

    Order findById(Long id);

    void updateDataForCancelledOrder(Order order);

    void updateDataAfterPlacingOrder(OrderBuyerDto orderBuyerDto);

    long getTotalAmount();
}

