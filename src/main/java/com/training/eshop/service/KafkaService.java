package com.training.eshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.training.eshop.model.Order;

public interface KafkaService {

    void sendMessageToSeller(Order order) throws JsonProcessingException;

    void receiveMessageBySeller(String message) throws JsonProcessingException;
}
