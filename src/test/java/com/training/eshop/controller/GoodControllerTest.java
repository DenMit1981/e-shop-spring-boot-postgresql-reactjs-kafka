package com.training.eshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.eshop.dto.user.UserRegisterDto;
import com.training.eshop.dto.good.GoodAdminCreationDto;
import com.training.eshop.dto.good.GoodAdminViewDto;
import com.training.eshop.dto.good.GoodBuyerDto;
import com.training.eshop.model.Good;
import com.training.eshop.model.User;
import com.training.eshop.service.GoodService;
import com.training.eshop.service.UserService;
import com.training.eshop.service.ValidationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GoodControllerTest {

    @Autowired
    private GoodController goodController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GoodService goodService;

    @Autowired
    private UserService userService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String[] CLEAN_TABLES_SQL = {
            "delete from goods",
            "delete from users"
    };

    @AfterEach
    public void resetDb() {
        for (String query : CLEAN_TABLES_SQL) {
            jdbcTemplate.execute(query);
        }
    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "1234")
    void saveTest_withStatus201andProductReturned() throws Exception {
        createTestUser("Denis", "admin_mogilev@yopmail.com", "P@ssword1", "yes");

        GoodAdminCreationDto newGood = createTestGoodDto();

        mockMvc.perform(
                        post("http://localhost:8081/goods/forAdmin")
                                .content(objectMapper.writeValueAsString(newGood))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(newGood.getTitle()))
                .andExpect(jsonPath("$.price").value(newGood.getPrice()))
                .andExpect(jsonPath("$.quantity").value(newGood.getQuantity()))
                .andExpect(jsonPath("$.description").value(newGood.getDescription()));

    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "1234")
    void save_NegativeTest_whenCreateInvalidProduct_thenStatus400BadRequest() throws Exception {
        createTestUser("John", "user_mogilev@yopmail.com", "P@ssword1", "yes");

        GoodAdminCreationDto newGood = createTestGoodDto();

        newGood.setTitle("");

        mockMvc.perform(
                        post("http://localhost:8081/goods/forAdmin")
                                .content(objectMapper.writeValueAsString(newGood))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user_mogilev@yopmail.com", password = "5678")
    void save_NegativeTest_whenBuyerDoesNotHaveAccessToCreateProduct_thenStatus403Forbidden() throws Exception {
        createTestUser("John", "user_mogilev@yopmail.com", "P@ssword1", "yes");

        GoodAdminCreationDto newGood = createTestGoodDto();

        mockMvc.perform(
                        post("http://localhost:8081/goods/forAdmin")
                                .content(objectMapper.writeValueAsString(newGood))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.info").value("Access is allowed only for administrator"));
    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "P@ssword1")
    void getByIdTest_whenGetExistingProduct_thenStatus200andProductReturned() throws Exception {
        User user = createTestUser("Denis", "admin_mogilev@yopmail.com", "P@ssword1", "yes");

        Good good = createTestProduct("Phone", BigDecimal.valueOf(4.2), 3L, "This is a phone",
                user.getEmail());

        mockMvc.perform(
                        get("http://localhost:8081/goods/forAdmin/{id}", good.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(good.getTitle()))
                .andExpect(jsonPath("$.price").value(good.getPrice()))
                .andExpect(jsonPath("$.quantity").value(good.getQuantity()))
                .andExpect(jsonPath("$.description").value(good.getDescription()));

    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "1234")
    void getById_NegativeTest_whenGetNotExistingProduct_thenStatus404NotFound() throws Exception {
        long wrongId = goodService.getTotalAmount() + 1;

        String error = "Product with id " + wrongId + " not found";

        mockMvc.perform(
                        get("http://localhost:8081/goods/forAdmin/{id}", wrongId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.info").value(error));
    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "1234")
    void updateTest_withStatus200andUpdatedProductReturned() throws Exception {
        User user = createTestUser("Denis", "admin_mogilev@yopmail.com", "P@ssword1", "yes");

        Good good = createTestProduct("Phone", BigDecimal.valueOf(4.2), 3L, "This is a phone",
                user.getEmail());

        GoodAdminCreationDto updatedGood = createTestGoodDto();

        mockMvc.perform(
                        put("http://localhost:8081/goods/forAdmin/{id}", good.getId())
                                .content(objectMapper.writeValueAsString(updatedGood))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(updatedGood.getTitle()))
                .andExpect(jsonPath("$.price").value(updatedGood.getPrice()))
                .andExpect(jsonPath("$.quantity").value(updatedGood.getQuantity()))
                .andExpect(jsonPath("$.description").value(updatedGood.getDescription()));
    }

    @Test
    @WithMockUser(username = "user_mogilev@yopmail.com", password = "5678")
    void update_NegativeTest_whenUpdateProductByBuyer_thenStatus403Forbidden() throws Exception {
        User buyer = createTestUser("John", "user_mogilev@yopmail.com", "P@ssword1", "yes");
        User admin = createTestUser("Denis", "admin_mogilev@yopmail.com", "P@ssword1", "yes");

        Good good = createTestProduct("Phone", BigDecimal.valueOf(4.2), 3L, "This is a phone",
                admin.getEmail());


        GoodAdminCreationDto updatedGood = createTestGoodDto();

        mockMvc.perform(
                        put("http://localhost:8081/goods/forAdmin/{id}", good.getId())
                                .content(objectMapper.writeValueAsString(updatedGood))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.info").value("Access is allowed only for administrator"));
    }

    @Test
    @WithMockUser(username = "user_mogilev@yopmail.com", password = "5678")
    void getAllForBuyerTest_whenIAmBuyer_thenStatus200() throws Exception {
        List<GoodBuyerDto> goods = goodService.getAllForBuyer();

        mockMvc.perform(
                        get("http://localhost:8081/goods/forBuyer"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(goods)));
    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "1234")
    void getAllForAdminTest_ByDefault_whenIAmAdmin_thenStatus200() throws Exception {
        List<GoodAdminViewDto> goods = goodService.getAllForAdmin("default",
                "", "id", "asc", 25, 0);

        mockMvc.perform(
                        get("http://localhost:8081/goods/forAdmin"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(goods)));
    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "221182")
    void getAllForAdminTest_ByParameters_whenIAmAdmin_thenStatus200() throws Exception {
        List<GoodAdminViewDto> goods = goodService.getAllForAdmin("title",
                "b", "id", "desc", 10, 1);

        mockMvc.perform(
                        get("http://localhost:8081/goods/forAdmin?pageSize=10&pageNumber=1&sortField=id" +
                                "&sortDirection=desc&searchField=title&parameter=b"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(goods)));
    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "1234")
    void getAllForAdmin_NegativeTest_whenISearchWrongParameters_thenStatus400BadRequest() throws Exception {
        String wrongSearchParameterError = validationService.getWrongSearchParameterError("книга");

        mockMvc.perform(
                        get("http://localhost:8081/goods/forAdmin?searchField=title&parameter=книга"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("Search should be in latin letters or figures",
                        wrongSearchParameterError));
    }

    @Test
    @WithMockUser(username = "admin_mogilev@yopmail.com", password = "1234")
    public void deleteTest_whenDeleteProductById_thenStatus200() throws Exception {
        User user = createTestUser("Denis", "admin_mogilev@yopmail.com", "P@ssword1", "yes");

        Good good = createTestProduct("Phone", BigDecimal.valueOf(4.2), 3L, "This is a phone",
                user.getEmail());

        mockMvc.perform(
                        delete("http://localhost:8081/goods/forAdmin/{id}", good.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user_mogilev@yopmail.com", password = "5678")
    public void givenError_whenDeleteProductByBuyer_thenStatus403Forbidden() throws Exception {
        createTestUser("John", "user_mogilev@yopmail.com", "P@ssword1", "yes");

        User user = createTestUser("Denis", "admin_mogilev@yopmail.com", "P@ssword1", "yes");

        Good good = createTestProduct("Phone", BigDecimal.valueOf(4.2), 3L, "This is a phone",
                user.getEmail());

        mockMvc.perform(
                        delete("http://localhost:8081/goods/forAdmin/{id}", good.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.info").value("Access is allowed only for administrator"));
    }

    private Good createTestProduct(String title, BigDecimal price, Long quantity, String description, String login) {
        GoodAdminCreationDto good = new GoodAdminCreationDto();

        good.setTitle(title);
        good.setPrice(price);
        good.setQuantity(quantity);
        good.setDescription(description);

        return goodService.save(good, login);
    }

    private GoodAdminCreationDto createTestGoodDto() {
        GoodAdminCreationDto goodDto = new GoodAdminCreationDto();

        goodDto.setTitle("Book");
        goodDto.setPrice(BigDecimal.valueOf(5.53));
        goodDto.setQuantity(2L);
        goodDto.setDescription("This is a book");

        return goodDto;
    }

    private User createTestUser(String name, String email, String password, String checkBoxValue) {
        UserRegisterDto userRegisterDto = new UserRegisterDto();

        userRegisterDto.setName(name);
        userRegisterDto.setEmail(email);
        userRegisterDto.setPassword(password);

        return userService.save(userRegisterDto, checkBoxValue);
    }
}
