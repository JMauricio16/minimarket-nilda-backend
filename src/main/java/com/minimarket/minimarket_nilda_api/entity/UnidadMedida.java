package com.minimarket.minimarket_nilda_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "unidad_medida")
@Data
public class UnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private Long idUnidad;

    @Column(nullable = false, unique = true, length = 20)
    private String abreviatura;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true;
}