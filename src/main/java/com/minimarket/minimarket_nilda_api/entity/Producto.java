package com.minimarket.minimarket_nilda_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotBlank(message = "El código es obligatorio")
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @NotNull
    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @NotNull
    @Positive(message = "El precio de venta debe ser mayor a cero")
    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @NotNull
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @NotNull
    @Min(value = 0)
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 5;

    @Column(nullable = false)
    private Boolean activo = true;
}