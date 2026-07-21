package com.orderhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderhub.dto.OrderRequest;
import com.orderhub.dto.OrderResponse;
import com.orderhub.model.OrderStatus;
import com.orderhub.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@WithMockUser // Basic Auth arrivera en Sprint 2 (ORD-13) ; on neutralise la sécurité par défaut ici
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void shouldReturn201WhenCreatingValidOrder() throws Exception {
        OrderRequest request = new OrderRequest("Jean Dupont", "Clavier mécanique", 2);
        OrderResponse response = new OrderResponse(
                UUID.randomUUID(), "Jean Dupont", "Clavier mécanique", 2,
                OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Jean Dupont"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturn400WhenCustomerNameIsBlank() throws Exception {
        OrderRequest invalidRequest = new OrderRequest("", "Clavier mécanique", 2);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenQuantityIsZeroOrNegative() throws Exception {
        OrderRequest invalidRequest = new OrderRequest("Jean Dupont", "Clavier mécanique", 0);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}