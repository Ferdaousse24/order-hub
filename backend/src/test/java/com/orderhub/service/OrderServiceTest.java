package com.orderhub.service;

import com.orderhub.dto.OrderRequest;
import com.orderhub.dto.OrderResponse;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new OrderRequest("Jean Dupont", "Clavier mécanique", 2);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        // GIVEN
        Order savedOrder = Order.builder()
                .id(UUID.randomUUID())
                .customerName("Jean Dupont")
                .product("Clavier mécanique")
                .quantity(2)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // WHEN
        OrderResponse response = orderService.createOrder(validRequest);

        // THEN
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order captured = captor.getValue();
        assertThat(captured.getCustomerName()).isEqualTo("Jean Dupont");
        assertThat(captured.getProduct()).isEqualTo("Clavier mécanique");
        assertThat(captured.getQuantity()).isEqualTo(2);

        assertThat(response.id()).isEqualTo(savedOrder.getId());
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
    }
}