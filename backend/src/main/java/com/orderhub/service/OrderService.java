package com.orderhub.service;

import com.orderhub.dto.OrderRequest;
import com.orderhub.dto.OrderResponse;
import com.orderhub.exception.OrderNotFoundException;
import com.orderhub.mapper.OrderMapper;
import com.orderhub.model.Order;
import com.orderhub.model.OrderStatus;
import com.orderhub.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderResponse createOrder(OrderRequest request) {
        Order order = Order.builder()
                .customerName(request.customerName())
                .product(request.product())
                .quantity(request.quantity())
                .status(OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    public java.util.List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new com.orderhub.exception.OrderNotFoundException(id));
        return orderMapper.toResponse(order);
    }
}