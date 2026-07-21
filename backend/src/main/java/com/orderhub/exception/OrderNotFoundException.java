package com.orderhub.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID id) {
        super("Commande introuvable avec l'ID : " + id);
    }
}