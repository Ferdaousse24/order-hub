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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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

     @Test
    void shouldReturn200WithEmptyListWhenNoOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn200WithOrdersList() throws Exception {
        OrderResponse response = new OrderResponse(
                UUID.randomUUID(), "Jean Dupont", "Clavier mécanique", 2,
                OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        when(orderService.getAllOrders()).thenReturn(java.util.List.of(response));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Jean Dupont"));
    }

     @Test
    void shouldReturn200WhenOrderExists() throws Exception {
        UUID id = UUID.randomUUID();
        OrderResponse response = new OrderResponse(id, "Jean Dupont", "Clavier", 2, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        when(orderService.getOrderById(id)).thenReturn(response);

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.getOrderById(id)).thenThrow(new com.orderhub.exception.OrderNotFoundException(id));

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200WhenUpdatingExistingOrder() throws Exception {
        UUID id = UUID.randomUUID();
        OrderRequest request = new OrderRequest("A Modifié", "P1 Modifié", 5);
        OrderResponse response = new OrderResponse(id, "A Modifié", "P1 Modifié", 5, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        when(orderService.updateOrder(org.mockito.ArgumentMatchers.eq(id), any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("A Modifié"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentOrder() throws Exception {
        UUID id = UUID.randomUUID();
        OrderRequest request = new OrderRequest("A", "P1", 1);

        when(orderService.updateOrder(org.mockito.ArgumentMatchers.eq(id), any(OrderRequest.class)))
                .thenThrow(new com.orderhub.exception.OrderNotFoundException(id));

        mockMvc.perform(put("/api/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}