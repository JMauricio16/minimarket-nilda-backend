package com.minimarket.minimarket_nilda_api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaRequestDTO {
    private String cliente = "Público general";

    @NotEmpty(message = "El carrito no puede estar vacío")
    private List<ItemVentaDTO> items;

    @Data
    public static class ItemVentaDTO {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long idProducto;

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        private BigDecimal cantidad;
    }
}