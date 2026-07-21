package com.orderhub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotBlank(message = "Le nom du client est obligatoire")
        String customerName,

        @NotBlank(message = "Le produit est obligatoire")
        String product,

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "La quantité doit être supérieure à 0")
        Integer quantity
) {
}