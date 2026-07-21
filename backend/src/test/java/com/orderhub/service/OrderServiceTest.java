package com.orderhub.service;

import com.orderhub.dto.OrderRequest;
import com.orderhub.dto.OrderResponse;
import com.orderhub.mapper.OrderMapper;
import com.orderhub.model.Order;
import com.orderhub.model.OrderStatus;
import com.orderhub.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new OrderRequest("Jean Dupont", "Clavier mécanique", 2);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        UUID orderId = UUID.randomUUID();
        Order savedOrder = Order.builder()
                .id(orderId)
                .customerName("Jean Dupont")
                .product("Clavier mécanique")
                .quantity(2)
                .status(OrderStatus.PENDING)
                .build();

        OrderResponse expectedResponse = new OrderResponse(
                orderId, "Jean Dupont", "Clavier mécanique", 2,
                OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(expectedResponse);

        OrderResponse response = orderService.createOrder(validRequest);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order captured = captor.getValue();
        assertThat(captured.getCustomerName()).isEqualTo("Jean Dupont");
        assertThat(captured.getQuantity()).isEqualTo(2);
        assertThat(response.id()).isEqualTo(orderId);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
    }
}