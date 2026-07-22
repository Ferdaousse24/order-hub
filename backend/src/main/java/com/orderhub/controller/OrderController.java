package com.orderhub.controller;

import com.orderhub.dto.OrderRequest;
import com.orderhub.dto.OrderResponse;
import com.orderhub.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestion des commandes")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle commande")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les commandes")
    public ResponseEntity<java.util.List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @ExceptionHandler(com.orderhub.exception.OrderNotFoundException.class)
    public ResponseEntity<String> handleNotFound(com.orderhub.exception.OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une commande par son identifiant")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une commande existante")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable java.util.UUID id,
                                                       @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commande")
    public ResponseEntity<Void> deleteOrder(@PathVariable java.util.UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}