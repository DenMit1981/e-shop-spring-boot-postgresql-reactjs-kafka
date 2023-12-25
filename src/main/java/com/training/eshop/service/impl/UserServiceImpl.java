package com.training.eshop.service.impl;

import com.training.eshop.config.security.jwt.JwtTokenProvider;
import com.training.eshop.converter.UserConverter;
import com.training.eshop.dto.user.UserLoginDto;
import com.training.eshop.dto.user.UserRegisterDto;
import com.training.eshop.exception.CheckBoxException;
import com.training.eshop.exception.UserIsPresentException;
import com.training.eshop.exception.UserNotFoundException;
import com.training.eshop.model.CustomUserDetails;
import com.training.eshop.model.User;
import com.training.eshop.model.enums.Role;
import com.training.eshop.repository.UserRepository;
import com.training.eshop.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class.getName());
    private static final String USER_IS_PRESENT = "User with name %s or email %s is already present";
    private static final String USER_NOT_FOUND = "User with login %s not found";
    private static final String USER_HAS_ANOTHER_PASSWORD = "User with login %s has another password. " +
            "Go to register or enter valid credentials";
    private static final String CHECKBOX_IS_NOT_CLICKED = "You should not be here.\n" +
            "Please, agree with the terms of service first\n";
    private static final String ADMIN = "admin";

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public User save(UserRegisterDto userDto, String checkBoxValue) {
        checkUserBeforeSave(userDto, checkBoxValue);

        User user = userConverter.fromUserRegisterDto(userDto);

        user.setRole(getUserRoleByLogin(user.getEmail()));

        userRepository.save(user);

        LOGGER.info("New user : {}", user);

        return user;
    }

    @Override
    @Transactional
    public Map<Object, Object> authenticateUser(UserLoginDto userDto) {
        User user = getByLoginAndPassword(userDto.getLogin(), userDto.getPassword());

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());

        Map<Object, Object> response = new HashMap<>();

        response.put("userName", user.getName());
        response.put("role", user.getRole());
        response.put("token", token);

        return response;
    }

    @Override
    @Transactional
    public User getByLogin(String login) {
        return userRepository.findByEmail(login)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, login)));
    }

    @Override
    @Transactional
    public User getByLoginAndPassword(String login, String password) {
        User user = userRepository.findByEmail(login)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, login)));

        if (passwordEncoder.matches(password, user.getPassword())) {

            LOGGER.info("User : {}", user);

            return user;
        }
        LOGGER.error(String.format(USER_HAS_ANOTHER_PASSWORD, login));

        throw new UserNotFoundException(String.format(USER_HAS_ANOTHER_PASSWORD, login));
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return new CustomUserDetails(getByLogin(login));
    }

    private boolean isUserPresent(UserRegisterDto userDto) {
        String name = userDto.getName();
        String email = userDto.getEmail();

        List<User> users = (List<User>) userRepository.findAll();

        return users.stream().anyMatch(user -> name.equals(user.getName())
                || email.equals(user.getEmail()));
    }

    private void checkUserBeforeSave(UserRegisterDto userDto, String checkBoxValue) {
        if (isUserPresent(userDto)) {
            LOGGER.error(String.format(USER_IS_PRESENT, userDto.getName(), userDto.getEmail()));

            throw new UserIsPresentException(String.format(USER_IS_PRESENT, userDto.getName(), userDto.getEmail()));
        }

        if (!checkBoxValue.equals("yes")) {
            LOGGER.error(CHECKBOX_IS_NOT_CLICKED);

            throw new CheckBoxException(CHECKBOX_IS_NOT_CLICKED);
        }
    }

    private Role getUserRoleByLogin(String login) {
        if (login.contains(ADMIN)) {
            return Role.ROLE_ADMIN;
        }

        return Role.ROLE_BUYER;
    }
}

