package com.minimarket.minimarket_nilda_api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EntradaInventarioDTO {
    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    private String motivo = "Abastecimiento de mercadería";
}