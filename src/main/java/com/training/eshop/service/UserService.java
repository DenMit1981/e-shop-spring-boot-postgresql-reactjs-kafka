package com.training.eshop.service;

import com.training.eshop.dto.user.UserRegisterDto;
import com.training.eshop.dto.user.UserLoginDto;
import com.training.eshop.model.User;

import java.util.Map;

public interface UserService {

    User save(UserRegisterDto userDto, String checkBoxValue);

    Map<Object, Object> authenticateUser(UserLoginDto userDto);

    User getByLogin(String login);

    User getByLoginAndPassword(String login, String password);
}
