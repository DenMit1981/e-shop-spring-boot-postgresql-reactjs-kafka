package com.training.eshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.training.eshop.dto.good.GoodBuyerDto;
import com.training.eshop.dto.history.HistoryDto;
import com.training.eshop.dto.order.OrderAdminViewDto;
import com.training.eshop.dto.order.OrderBuyerDto;
import com.training.eshop.exception.OrderNotPlacedException;
import com.training.eshop.model.Order;
import com.training.eshop.service.HistoryService;
import com.training.eshop.service.OrderService;
import com.training.eshop.service.ValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/orders")
@AllArgsConstructor
@Api("Order controller")
public class OrderController {

    private static final Logger LOGGER = LogManager.getLogger(OrderController.class.getName());

    private static final String ORDER_NOT_PLACED = "Your order not placed yet";

    private final OrderService orderService;
    private final HistoryService historyService;
    private final ValidationService validationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a new order by buyer", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> save(@RequestBody GoodBuyerDto goodBuyerDto,
                                  BindingResult bindingResult,
                                  Principal principal,
                                  @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) throws JsonProcessingException {
        List<String> errorMessage = validationService.generateErrorMessage(bindingResult);

        if (checkErrors(errorMessage)) {
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        return clickingActions(buttonValue, goodBuyerDto, principal);
    }

    @GetMapping
    @ApiOperation(value = "Get all orders for admin")
    public ResponseEntity<List<OrderAdminViewDto>> getAll(@RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                                          @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                                          @RequestParam(value = "pageSize", defaultValue = "15") int pageSize,
                                                          @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber) {
        List<OrderAdminViewDto> orders = orderService.getAll(sortField, sortDirection, pageSize, pageNumber);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get order by ID")
    public ResponseEntity<OrderBuyerDto> getById(@PathVariable("id") Long id,
                                                 @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        OrderBuyerDto order = orderService.getById(id);

        if (buttonValue.equals("LogOut")) {
            orderService.updateDataAfterPlacingOrder(order);
        }

        return ResponseEntity.ok(order);
    }

    @GetMapping(value = "/{orderId}/history")
    @ApiOperation(value = "Get all order history")
    public ResponseEntity<List<HistoryDto>> getAllHistoryByOrderId(@PathVariable("orderId") Long orderId,
                                                                   @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        return ResponseEntity.ok(historyService.getAllByOrderId(orderId, buttonValue));
    }

    @GetMapping("/total")
    @ApiOperation(value = "Get total amount of orders")
    public ResponseEntity<?> getTotalAmount() {
        return ResponseEntity.ok(orderService.getTotalAmount());
    }

    private ResponseEntity<?> clickingActions(String buttonValue, GoodBuyerDto goodBuyerDto, Principal principal) throws JsonProcessingException {
        Order order = new Order();

        switch (buttonValue) {
            case "Add Goods":
                orderService.addGoodToOrder(goodBuyerDto);

                return new ResponseEntity<>(orderService.getCartGoods(), HttpStatus.OK);
            case "Remove Goods":
                orderService.deleteGoodFromOrder(goodBuyerDto);

                return new ResponseEntity<>(orderService.getCartGoods(), HttpStatus.OK);

            case "Submit":
                if (goodBuyerDto.getTitle() != null && goodBuyerDto.getPrice() != null) {

                    Order savedOrder = orderService.save(order, principal.getName());

                    String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
                    String savedOrderLocation = currentUri + "/" + savedOrder.getId();

                    return ResponseEntity.status(CREATED)
                            .header(HttpHeaders.LOCATION, savedOrderLocation)
                            .body(savedOrder);
                } else {
                    LOGGER.error(ORDER_NOT_PLACED);

                    throw new OrderNotPlacedException(ORDER_NOT_PLACED);
                }
        }

        orderService.updateDataForCancelledOrder(order);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean checkErrors(List<String> errorMessage) {
        return !errorMessage.isEmpty();
    }
}
