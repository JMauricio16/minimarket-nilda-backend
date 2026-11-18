package com.minimarket.minimarket_nilda_api.repository;

import com.minimarket.minimarket_nilda_api.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByCodigo(String codigo);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo")
    List<Producto> findStockCritico();

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:buscar IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :buscar, '%'))) " +
           "AND (:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria)")
    List<Producto> buscarConFiltros(@Param("buscar") String buscar, @Param("idCategoria") Long idCategoria);
}