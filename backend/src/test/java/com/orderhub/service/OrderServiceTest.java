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


    @Test
    void shouldReturnAllOrders() {
        Order order1 = Order.builder().id(UUID.randomUUID()).customerName("A").product("P1").quantity(1).status(OrderStatus.PENDING).build();
        Order order2 = Order.builder().id(UUID.randomUUID()).customerName("B").product("P2").quantity(3).status(OrderStatus.CONFIRMED).build();

        OrderResponse response1 = new OrderResponse(order1.getId(), "A", "P1", 1, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        OrderResponse response2 = new OrderResponse(order2.getId(), "B", "P2", 3, OrderStatus.CONFIRMED, LocalDateTime.now(), LocalDateTime.now());

        when(orderRepository.findAll()).thenReturn(java.util.List.of(order1, order2));
        when(orderMapper.toResponse(order1)).thenReturn(response1);
        when(orderMapper.toResponse(order2)).thenReturn(response2);

        java.util.List<OrderResponse> result = orderService.getAllOrders();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(response1, response2);
    }

    @Test
    void shouldReturnOrderWhenIdExists() {
        UUID id = UUID.randomUUID();
        Order order = Order.builder().id(id).customerName("A").product("P1").quantity(1).status(OrderStatus.PENDING).build();
        OrderResponse expected = new OrderResponse(id, "A", "P1", 1, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        when(orderRepository.findById(id)).thenReturn(java.util.Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(expected);

        OrderResponse result = orderService.getOrderById(id);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(java.util.Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.orderhub.exception.OrderNotFoundException.class,
                () -> orderService.getOrderById(id)
        );
    }

    @Test
    void shouldUpdateOrderWhenIdExists() {
        UUID id = UUID.randomUUID();
        Order existing = Order.builder().id(id).customerName("A").product("P1").quantity(1).status(OrderStatus.PENDING).build();
        OrderRequest updateRequest = new OrderRequest("A Modifié", "P1 Modifié", 5);
        OrderResponse expected = new OrderResponse(id, "A Modifié", "P1 Modifié", 5, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        when(orderRepository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenReturn(existing);
        when(orderMapper.toResponse(existing)).thenReturn(expected);

        OrderResponse result = orderService.updateOrder(id, updateRequest);

        assertThat(result.customerName()).isEqualTo("A Modifié");
        verify(orderRepository).save(existing);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentOrder() {
        UUID id = UUID.randomUUID();
        OrderRequest updateRequest = new OrderRequest("A", "P1", 1);
        when(orderRepository.findById(id)).thenReturn(java.util.Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.orderhub.exception.OrderNotFoundException.class,
                () -> orderService.updateOrder(id, updateRequest)
        );
    }
}