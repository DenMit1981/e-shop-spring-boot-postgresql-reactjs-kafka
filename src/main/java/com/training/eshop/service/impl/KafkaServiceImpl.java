package com.training.eshop.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.eshop.converter.OrderConverter;
import com.training.eshop.dto.order.OrderMessageDto;
import com.training.eshop.model.Order;
import com.training.eshop.service.KafkaService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private static final Logger LOGGER = LogManager.getLogger(KafkaServiceImpl.class.getName());
    private static final String ORDER_TOPIC = "ORDER";
    public static final String GROUP_ID = "group-id";

    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final OrderConverter orderConverter;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessageToSeller(Order order) throws JsonProcessingException {
        OrderMessageDto orderDto = orderConverter.convertToOrderMessageDto(order);

        String orderAsMessage = objectMapper.writeValueAsString(orderDto);

        kafkaTemplate.send(ORDER_TOPIC, orderAsMessage);

        LOGGER.info("Order has been produced {}", orderAsMessage);
    }

    @Override
    @KafkaListener(topics = ORDER_TOPIC, groupId = GROUP_ID)
    public void receiveMessageBySeller(String message) throws JsonProcessingException {
        LOGGER.info("Create order request has been received: {}", message);

        OrderMessageDto orderMessageDto = objectMapper.readValue(message, OrderMessageDto.class);

        Order order = modelMapper.map(orderMessageDto, Order.class);

        LOGGER.info("Order № {} has been viewed by admin", order.getId());
    }
}
