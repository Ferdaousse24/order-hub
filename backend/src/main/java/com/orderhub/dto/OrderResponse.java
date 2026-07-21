package com.orderhub.dto;

import com.orderhub.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerName,
        String product,
        Integer quantity,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}