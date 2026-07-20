package com.orderhub.repository;

import com.orderhub.model.Order;
import com.orderhub.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPersistAndRetrieveOrder() {
        // GIVEN
        Order order = Order.builder()
                .customerName("Jean Dupont")
                .product("Clavier mécanique")
                .quantity(2)
                .status(OrderStatus.PENDING)
                .build();

        // WHEN
        Order saved = orderRepository.save(order);
        Optional<Order> found = orderRepository.findById(saved.getId());

        // THEN
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("Jean Dupont");
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    void shouldGenerateUuidAsId() {
        Order order = Order.builder()
                .customerName("Marie Curie")
                .product("Livre")
                .quantity(1)
                .status(OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);

        assertThat(saved.getId()).isInstanceOf(UUID.class);
    }
}