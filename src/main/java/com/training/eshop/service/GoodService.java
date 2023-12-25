package com.training.eshop.service;

import com.training.eshop.dto.good.GoodAdminCreationDto;
import com.training.eshop.dto.good.GoodAdminViewDto;
import com.training.eshop.dto.good.GoodBuyerDto;
import com.training.eshop.model.Good;

import java.util.List;

public interface GoodService {

    Good save(GoodAdminCreationDto goodDto, String login);

    List<GoodBuyerDto> getAllForBuyer();

    List<GoodAdminViewDto> getAllForAdmin(String searchField, String parameter, String sortField,
                                          String sortDirection, int pageSize, int pageNumber);

    GoodAdminViewDto getById(Long id);

    Good getByTitleAndPrice(String title, String price);

    Good update(Long id, GoodAdminCreationDto goodDto, String login);

    void deleteById(Long id, String login);

    void deleteByIdAfterQuantityEqualsZero(Long id);

    List<Good> findAll();

    long getTotalAmount();

    String getPriceFromDropMenu(String price);
}
